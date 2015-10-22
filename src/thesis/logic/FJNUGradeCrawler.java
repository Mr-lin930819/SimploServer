package thesis.logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.*;
import org.jsoup.nodes.Element;
import thesis.httpMethod.HttpManager;

public class FJNUGradeCrawler {
	
	static private final String loginUrlStr = "http://jwgl.fjnu.edu.cn/default2.aspx";
	private final String mainUrlStr = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=";
	static private final String checkImageUrlStr = "http://jwgl.fjnu.edu.cn/CheckCode.aspx";
	
	private String nameStr,passwordStr,checkCodeStr;
	private String ddlXNStr,ddlXQStr,xnxqStr;
	private String xmStr,gnmkdmStr;
	
	private HashMap<String,String> grades;
	private URL loginUrl,mainUrl;
	private String viewStateStr;
	
	static public byte[] checkCodeTest(){
		byte[] res = HttpManager.getCheckcodeToSave(checkImageUrlStr);
		String ret = null;
		try {
			ret = new String(res,"gb2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(ret);
		return res;
	}
	
	public FJNUGradeCrawler() throws IOException{
		loginUrl = new URL(loginUrlStr);
		grades = new HashMap<String,String>();
	}
	
	public static void getStart(String name,String passwordNum){
		HttpManager.clearSpecialHeader();
		HttpManager.addSpecialHeader("content-type", "application/x-www-form-urlencoded");
		//getViewState(HttpManager.sendPost(loginUrlStr,""));
	}
	
	public static String getViewState(){
		HttpManager.clearSpecialHeader();
		HttpManager.addSpecialHeader("content-type", "application/x-www-form-urlencoded");
		String reply = HttpManager.sendGet(loginUrlStr,"");
		String viewState = "";
		Pattern pattern = Pattern.compile("__VIEWSTATE\" value=\"([^>]*)\" />");
    	Matcher matcher = pattern.matcher(reply);
    	if(matcher.find()){
    		viewState = matcher.group(1);
    		System.out.println("Matched! ViewState is :"+viewState );
    		return viewState;
    	}
    	else
    		return null;
	}
	
	public void setLoginInfo(String name,String passwordNum,String checkCode,String viewState){
		initialize();
		nameStr = name;
		passwordStr = passwordNum;
		checkCodeStr = checkCode;
		viewStateStr = viewState;
	}
	public void setSearcInfo(String xn,String xq,String xqxn){
	    ddlXNStr=xn;
	    ddlXQStr=xq;
	    xnxqStr=xqxn;
	    grades.clear();
	    getStart(nameStr,passwordStr);
	}
	
	public String getCourseData(int i){//获取课程信息
		return null;
	}
	
	public String getGradeData(int i){//获取成绩信息
		return null;
	}
	
	public int getDataCount(){//获取数据数量
		return 0;
	}
	
    private void setXm(String pageContent){
        Matcher xmMatcher = Pattern.compile("xm=(.{0,12})&gnmkdm=N121617").matcher(pageContent);
        if(xmMatcher.find())
        	xmStr=xmMatcher.group(1);
        gnmkdmStr="N121617";//表示成绩查询的编号
    }
    private void saveData(String pageContent){
    	String rexStr = "<td>"+ddlXNStr+"</td><td>"+ddlXQStr+"</td><td>([0-9]{2,10})</td><td>"
        			  +	"([^</td>]{0,30})</td><td>";
        //String rexStr_2 = "</td><td>([\.0-9]{3})</td><td>([0-9/.&nbsp;]{1,6})</td><td>([0-9\.]{1,5})</td><td>([01&nbsp;]{1,6})</td><td>&nbsp;";
    	String rexStr_2 =  "";
    	Matcher courseMatcher = Pattern.compile(rexStr).matcher(pageContent);
        Pattern gradeREX = Pattern.compile(rexStr_2);
        Matcher gradeMatcher = gradeREX.matcher(pageContent);
        int i = courseMatcher.start();
        int j = gradeMatcher.start();

//        while(i!=-1&&j!=-1){
        while(courseMatcher.find() && gradeMatcher.find()){
        	grades.put(courseMatcher.group(2), gradeMatcher.group(3));
//            courseData.append(courseREX.cap(2));
            i+=35;
            courseMatcher.region(++i, courseMatcher.regionEnd());
            i = courseMatcher.start();
//            i=pageContent.indexOf(courseREX,++i);
//            qDebug()<<courseREX.cap(2);
//            gradeData.append(gradeREX.cap(3));
            j+=110;
            gradeMatcher.region(++j, gradeMatcher.regionEnd());
            j = gradeMatcher.start();
//            j=pageContent.indexOf(gradeREX,++j);
//            qDebug()<<gradeREX.cap(1)<<" "<<gradeREX.cap(2)<<" "<<gradeREX.cap(3)<<"\n";
        }
    }
    private void initialize(){
        grades.clear();
        nameStr = "";
        passwordStr= "";
        ddlXNStr = "";
        ddlXQStr = "";
        xnxqStr = "";
        viewStateStr = "";
    }
    private void saveAsXml(String src){
    	StringBuffer newContent = new StringBuffer();
    	File xmlFile = new File("34.xml");
//        if (!xmlFile.open(QIODevice::ReadWrite))
//                return;
//        xmlFile.resize(0);
//        QTextStream ts(&xmlFile);
//        QTextCodec *codec=QTextCodec::codecForName("UTF-8");
//        ts.setCodec(codec);
        //newContent.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<form>");
    	
    	if(!xmlFile.exists()){
    		try {
				xmlFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	Matcher formMatcher = Pattern.compile("<form(.*)</form>").matcher(src);
    	newContent.append(formMatcher.group(1));
        newContent.append("</form>");
        
    	BufferedWriter output;
    	try {
    		output = new BufferedWriter(new FileWriter(xmlFile));
			output.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n" + "<form");
			output.write(newContent.toString());
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        ts<<"<?xml version=\"1.0\" encoding=\"utf-8\"?>"<<endl<<"<form";
//        QRegExp formExp("<form(.*)</form>");
    	
        //QString tmp(src);
//        src.indexOf(formExp,0);
//        newContent.append(formExp.cap(1));
    	

//        ts<<newContent;
//        qDebug()<<"saved 34.xml";
    }
    
    public void emptyReplyFinish(){
    	//System.out.println(reply);
//    	Pattern pattern = Pattern.compile("__VIEWSTATE\" value=\"([^>]*)\" />");
//    	Matcher matcher = pattern.matcher(reply);
//    	if(matcher.find())
//    		viewState = matcher.group();
//    	else
//    		return;
    	HashMap<String,String> params = new HashMap<String,String>();
    	//params.put("__VIEWSTATE",viewStateStr);
    	//params.put("txtUserName",nameStr);
    	//params.put("TextBox2",passwordStr);
        //params.put("txtSecretCode",checkCodeStr);
    	params.put("__VIEWSTATE","dDwyODE2NTM0OTg7Oz79m7/udPyyThgiewzzwnr1AnohgA==");
    	params.put("txtUserName","105052012035");
    	params.put("TextBox2","a4842029578");
        params.put("txtSecretCode","febk");
        params.put("RadioButtonList1","学生");
        params.put("Button1","");
        params.put("lbLanguage","");
        params.put("hidPdrs","");
        params.put("hidsc","");
        
        HttpManager.clearSpecialHeader();
        HttpManager.addSpecialHeader("Accept", "image/gif, image/jpeg, image/pjpeg, application/x-ms-application, application/xaml+xml, application/x-ms-xbap, */*");
        HttpManager.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
        HttpManager.addSpecialHeader("Cache-Control","no-cache");
        HttpManager.addSpecialHeader("Host", "jwgl.fjnu.edu.cn");
        HttpManager.addSpecialHeader("Referer","http://jwgl.fjnu.edu.cn/");
        HttpManager.addSpecialHeader("Cookie", HttpManager.cookie);
        System.out.println("Params:" + params.toString());
        System.out.print(HttpManager.sendPost(loginUrlStr, params));
        //replyFinish(HttpManager.sendPost(loginUrlStr, params));
    }

	private void replyFinish(String reply){
		System.out.println(HttpManager.cookie);
		HttpManager.clearSpecialHeader();
		HttpManager.addSpecialHeader("Cookie", HttpManager.cookie);
		getXMFinished(HttpManager.sendGet(mainUrlStr + nameStr, ""));
	}
	
	private void getXMFinished(String reply){
		saveAsXml(reply);
		setXm(reply);
		String newMainUrl = "http://jwgl.fjnu.edu.cn/xscjcx_dq.aspx?xh="+nameStr+"&xm="+xmStr+"&gnmkdm="+gnmkdmStr;
		String refererUrl = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=" + nameStr;
		HttpManager.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
		HttpManager.addSpecialHeader("Referer",refererUrl);
		getReplyFinished(HttpManager.sendGet(newMainUrl, ""));
	}
	//post查成绩的数据，同时获取新的__VIEWSTATE存入以便之后读取
	private void getReplyFinished(String reply){
		Pattern pattern = Pattern.compile("__VIEWSTATE\" value=\"([^>]*)\" />");
		Matcher matcher = pattern.matcher(reply);
		viewStateStr = matcher.group().replaceAll("+", "%2B");
		
		HashMap<String,String> param = new HashMap<String,String>();
		param.put("__EVENTARGUMENT","");
		param.put("__EVENTTARGET","");
		param.put("__VIEWSTATE",viewStateStr);
		param.put("hidLanguage","");
		param.put("ddlxn",ddlXNStr);//学年
		param.put("ddlxq",ddlXQStr);//学期
		param.put("btnCx","+查++询+");//按学期或者按照学年查询
		
		String refererUrl = "http://jwgl.fjnu.edu.cn/xscjcx_dq.aspx?xh=" + 
								nameStr + "&xm="+xmStr+"&gnmkdm="+gnmkdmStr;
		
		HttpManager.clearSpecialHeader();
		HttpManager.addSpecialHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		HttpManager.addSpecialHeader("Accept-Encoding","gzip,deflate");
		HttpManager.addSpecialHeader("Accept-Language","zh-CN");
		HttpManager.addSpecialHeader("Cache-Control","no-cache");
		HttpManager.addSpecialHeader("Connection","Keep-Alive");
		HttpManager.addSpecialHeader("Content-Length","4413");
		HttpManager.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
		HttpManager.addSpecialHeader("Host","jwgl.fjnu.edu.cn");
		HttpManager.addSpecialHeader("Referer",refererUrl);
		HttpManager.addSpecialHeader("User-Agent","Mozilla/5.0");
		HttpManager.addSpecialHeader("Cookie", HttpManager.cookie);
		
		updateVSReplyFinished(HttpManager.sendPost(refererUrl, param));
	}
	
	//查询成功，发送结束信号，更新数据！
	private void updateVSReplyFinished(String reply){
	    saveData(reply);
	}
}
