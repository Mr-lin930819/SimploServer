package thesis.Servlet.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import thesis.JavaBean.SearchInfo;
import thesis.httpMethod.HttpManager;
import thesis.httpMethod.NetworkManager;

/**
 * Servlet implementation class MainPageTestServlet
 */
@WebServlet(urlPatterns = { "/MainPageTestServlet" })
public class MainPageTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String loginUrlStr = "http://jwgl.fjnu.edu.cn/default2.aspx";
	private final String mainUrlStr = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=";
//	private String xmStr = "", gnmkdmStr = "";
	NetworkManager nm = null;
//	private final String checkImageUrlStr = "http://jwgl.fjnu.edu.cn/CheckCode.aspx";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainPageTestServlet() {
        super();
        nm = new NetworkManager();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("gb2312");
		String resultPage = "",xmStr = "";
		SearchInfo user = (SearchInfo)request.getAttribute("loginInfo");
		
		if(request.getParameter("isFirst").equals("0"))
			xmStr = postForLogin(user);
		resultPage = postForGradeQuery(user,xmStr);
		getForLogout(user.getCookie());
		String jsonText = saveGradeToJson(resultPage,user);
		response.getWriter().write(jsonText);
	}
	
	private String postForLogin(SearchInfo user) {
//		NetworkManager nm = new NetworkManager();
    	HashMap<String,String> params = new HashMap<String,String>();
    	String reply = "",nameStr = "";
//   	String xmStr = "",gnmkdmStr = "";
    	/**
    	 *  ��1����get��¼ҳ��
    	 *  ����ҳ�棺�õ�ҳ���е�ViewStateֵ
    	 */
    	user.setInitViewState(user.getInitViewState().replaceAll("[+]", "%2B"));
    	/** 
    	 *  ��2�������ݵ�¼��Ϣ������post�������������ѧ�á����롢��֤��
    	 *  ����ҳ�棺ȡ����Ӧͷ�е�Set-Cookie���ݡ�
    	 */
    	params.put("__VIEWSTATE",user.getInitViewState());
    	params.put("txtUserName",user.getNumber());
    	params.put("TextBox2",user.getPassword());
        params.put("txtSecretCode",user.getCheckCode());
        params.put("RadioButtonList1","ѧ��");
        params.put("Button1","");
        params.put("lbLanguage","");
        params.put("hidPdrs","");
        params.put("hidsc","");
        
        nm.clearSpecialHeader();
        nm.addSpecialHeader("Accept", "image/gif, image/jpeg, image/pjpeg, application/x-ms-application, application/xaml+xml, application/x-ms-xbap, */*");
        nm.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
        nm.addSpecialHeader("Cache-Control","max-age=0");
        nm.addSpecialHeader("Host", "jwgl.fjnu.edu.cn");
        nm.addSpecialHeader("Referer","http://jwgl.fjnu.edu.cn/");
        nm.addSpecialHeader("Cookie", user.getCookie());
        nm.addSpecialHeader("Accept-Encoding","gzip, deflate");
        nm.addSpecialHeader("Accept-Language","zh-CN,en,*");
        //System.out.println("Params:" + params.toString());
        //System.out.print(HttpManager.sendPost(loginUrlStr, params));
        nm.sendPost(loginUrlStr, params);
        
        /** 
         *  ��3��������Cookieͷ��get����
         *  ����ҳ�棺����������Ϣ���������[XM]
         */
        //System.out.println("I will Comming!!!");
        nm.clearSpecialHeader();
		nm.addSpecialHeader("Cookie", /*HttpManager.*/user.getCookie());
		reply = nm.sendGet(mainUrlStr + user.getNumber(), "");
		//System.out.println("I am Comming!!!");
		
	    Matcher xmMatcher = Pattern.compile("xm=(.{0,12})&gnmkdm=N121618").matcher(reply);
	    if(xmMatcher.find())
        	nameStr=xmMatcher.group(1);
        return nameStr;
	}
	
	private String postForGradeQuery(SearchInfo user,String xmStr){
		//NetworkManager nm = new NetworkManager();
    	HashMap<String,String> params = new HashMap<String,String>();
    	String reply = "";
    	//String xmStr = "",gnmkdmStr = "";
    	String updatedViewState = "";
    	//String viewState = "";
    	//String ddlXNStr = "2014-2015",ddlXQStr="";
    	Document doc = null;
    	Element form;
    	/**
    	 *  ��1����get��¼ҳ��
    	 *  ����ҳ�棺�õ�ҳ���е�ViewStateֵ
    	 */
////    	HttpManager.clearSpecialHeader();
////		HttpManager.addSpecialHeader("content-type", "application/x-www-form-urlencoded");
////		reply = HttpManager.sendGet(loginUrlStr, "");
////    	doc = Jsoup.parse(reply);
////		form = doc.select("input[name=__VIEWSTATE]").first();
////		viewState = form.attr("value");
//    	viewState =  viewState.replaceAll("[+]", "%2B");
////		System.out.println("__VIEWSTATE-->"+viewState);
////    	
//    	/** 
//    	 *  ��2�������ݵ�¼��Ϣ������post�������������ѧ�á����롢��֤��
//    	 *  ����ҳ�棺ȡ����Ӧͷ�е�Set-Cookie���ݡ�
//    	 */
//    	params.put("__VIEWSTATE",viewState);
//    	params.put("txtUserName","105052012035");
//    	params.put("TextBox2","a4842029578");
//        params.put("txtSecretCode",checkCode);
//        params.put("RadioButtonList1","ѧ��");
//        params.put("Button1","");
//        params.put("lbLanguage","");
//        params.put("hidPdrs","");
//        params.put("hidsc","");
//        
//        nm.clearSpecialHeader();
//        nm.addSpecialHeader("Accept", "image/gif, image/jpeg, image/pjpeg, application/x-ms-application, application/xaml+xml, application/x-ms-xbap, */*");
//        nm.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
//        nm.addSpecialHeader("Cache-Control","max-age=0");
//        nm.addSpecialHeader("Host", "jwgl.fjnu.edu.cn");
//        nm.addSpecialHeader("Referer","http://jwgl.fjnu.edu.cn/");
//        nm.addSpecialHeader("Cookie", cookie);
//        nm.addSpecialHeader("Accept-Encoding","gzip, deflate");
//        nm.addSpecialHeader("Accept-Language","zh-CN,en,*");
//        //System.out.println("Params:" + params.toString());
//        //System.out.print(HttpManager.sendPost(loginUrlStr, params));
//        nm.sendPost(loginUrlStr, params);
//        
//        /** 
//         *  ��3��������Cookieͷ��get����
//         *  ����ҳ�棺����������Ϣ���������[XM]
//         */
//        //System.out.println("I will Comming!!!");
//        nm.clearSpecialHeader();
//		nm.addSpecialHeader("Cookie", /*HttpManager.*/cookie);
//		reply = nm.sendGet(mainUrlStr + "105052012035", "");
//		//System.out.println("I am Comming!!!");
//		
//	    Matcher xmMatcher = Pattern.compile("xm=(.{0,12})&gnmkdm=N121618").matcher(reply);
//	    if(xmMatcher.find())
//        	xmStr=xmMatcher.group(1);
        
        //System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX"+xmStr);
		
		/**
		 * 	��4����get���󣬻�ȡ ���µ�ViewState�Է��ʳɼ���ѯҳ��
		 *  ����ҳ�棺����VIEWSTATE,���������ҽ����еġ�+���滻�ɡ�%2B��
		 */
        String newMainUrl = "http://jwgl.fjnu.edu.cn/xscj_gc.aspx?xh="+user.getNumber()+"&xm="+xmStr+"&gnmkdm="+"N121618";//gnmkdm="N121618";��ʾ�ɼ���ѯ�ı��
		String refererUrl = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=" + user.getNumber();
		nm.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
		nm.addSpecialHeader("Referer",refererUrl);
		reply = nm.sendGet(newMainUrl, "");
//		Matcher newVSmatcher = Pattern.compile("__VIEWSTATE\" value=\"([^>]*)\" />").matcher(reply);
//		if(newVSmatcher.find())
//			updatedViewState = newVSmatcher.group().replaceAll("+", "%2B");
		doc = Jsoup.parse(reply);
		form = doc.select("input[name=__VIEWSTATE]").first();
		updatedViewState = form.attr("value");
		updatedViewState =  updatedViewState.replaceAll("[+]", "%2B");
		/**
		 * ��5����post��������ѧ�����Ϣ��Ϊpost����������ͷ��Ϣ
		 * ����ҳ�棺�ɼ���ѯ��������Խ�����óɼ�
		 */
		params.clear();
//		params.put("__EVENTARGUMENT","");
//		params.put("__EVENTTARGET","");
		params.put("__VIEWSTATE",updatedViewState);
		//params.put("hidLanguage","");
		params.put("ddlXN",user.getxNStr());//ѧ��
		params.put("ddlXQ",user.getxQStr());//ѧ��
		//params.put("btnCx","+��++ѯ+");//��ѧ�ڻ��߰���ѧ���ѯ
		params.put("Button5","��ѧ���ѯ");
		
		refererUrl = "http://jwgl.fjnu.edu.cn/xscj_gc.aspx?xh=" + 
				user.getNumber() + "&xm="+xmStr+"&gnmkdm="+"N121618";
		nm.clearSpecialHeader();
		nm.addSpecialHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		nm.addSpecialHeader("Accept-Encoding","gzip,deflate");
		nm.addSpecialHeader("Accept-Language","zh-CN");
		nm.addSpecialHeader("Cache-Control","no-cache");
		nm.addSpecialHeader("Connection","Keep-Alive");
		nm.addSpecialHeader("Content-Length","4413");
		nm.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
		nm.addSpecialHeader("Host","jwgl.fjnu.edu.cn");
		nm.addSpecialHeader("Referer",refererUrl);
		nm.addSpecialHeader("User-Agent","Mozilla/5.0");
		nm.addSpecialHeader("Cookie", user.getCookie());
		
		reply = nm.sendPost(refererUrl, params);
		return reply;
    }

	private void getForLogout(String cookie){
		//�ǳ�
		nm.clearSpecialHeader();
		nm.addSpecialHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		nm.addSpecialHeader("Accept-Encoding","gzip,deflate");
		nm.addSpecialHeader("Accept-Language","zh-CN");
		nm.addSpecialHeader("Cache-Control","no-cache");
		nm.addSpecialHeader("Connection","Keep-Alive");
		nm.addSpecialHeader("Content-Length","4413");
		nm.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
		nm.addSpecialHeader("Host","jwgl.fjnu.edu.cn");
//		nm.addSpecialHeader("Referer",refererUrl);
		nm.addSpecialHeader("User-Agent","Mozilla/5.0");
		nm.addSpecialHeader("Cookie", cookie);
		nm.sendGet("http://jwgl.fjnu.edu.cn/logout.aspx", "");
	}
	
	/**
	 * ���һ������ȡĿ��ҳ���еĳɼ����ݣ�����װΪjson����
	 * @param content	ҳ������
	 * @param user		��¼�û���Ϣ
	 * @return			json����
	 */
	private String saveGradeToJson(String content,SearchInfo user){
		HashMap<String, String> grades = new HashMap<String,String>();
		Document doc = null;
		Element table;
		Elements courses;
		String name,grade,result = null;
		JSONArray jGrades = new JSONArray();
		JSONObject jMain = new JSONObject();
		
		doc = Jsoup.parse(content);
		table = doc.select("table[class=datelist]").first();
		courses = table.select("tbody").select("tr");
		
		for(Element course:courses){
			name = course.select("td").get(3).text();
			grade = course.select("td").get(8).text();
			grades.put(name, grade);
		}
		jGrades.put(grades);
		try {
			jMain.put("GRADE", jGrades);
			result = jMain.toString(4);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
}
