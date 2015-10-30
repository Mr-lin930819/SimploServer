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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import thesis.httpMethod.HttpManager;

/**
 * Servlet implementation class MainPageTestServlet
 */
@WebServlet(urlPatterns = { "/MainPageTestServlet" })
public class MainPageTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String loginUrlStr = "http://jwgl.fjnu.edu.cn/default2.aspx";
	private final String mainUrlStr = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=";
//	private final String checkImageUrlStr = "http://jwgl.fjnu.edu.cn/CheckCode.aspx";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainPageTestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("gb2312");
//		HashMap<String,String> params = new HashMap<String,String>();
		String resultPage = "";
//    	params.put("__VIEWSTATE",request.getParameter("viewState"));
//    	params.put("txtUserName","105052012035");
//    	params.put("TextBox2","a4842029578");
//        params.put("txtSecretCode",request.getParameter("checkCode"));
//        params.put("RadioButtonList1","ѧ��");
//        params.put("Button1","");
//        params.put("lbLanguage","");
//        params.put("hidPdrs","");
//        params.put("hidsc","");
//        
//        HttpManager.clearSpecialHeader();
//        HttpManager.addSpecialHeader("Accept", "image/gif, image/jpeg, image/pjpeg, application/x-ms-application, application/xaml+xml, application/x-ms-xbap, */*");
//        HttpManager.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
//        HttpManager.addSpecialHeader("Cache-Control","max-age=0");
//        HttpManager.addSpecialHeader("Host", "jwgl.fjnu.edu.cn");
//        HttpManager.addSpecialHeader("Referer","http://jwgl.fjnu.edu.cn/");
//        HttpManager.addSpecialHeader("Cookie", request.getParameter("cookie"));
//        HttpManager.addSpecialHeader("Content_Length", "196");
//        resultPage = HttpManager.sendPost("http://jwgl.fjnu.edu.cn/default2.aspx", params);
//		System.out.println("Params:" + params);
		
		
		
//		FileWriter writer = null;
//		try {
//			writer = new FileWriter(new File("D:/main.html"));
//			writer.write(resultPage);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally {
//			try {
//				writer.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		resultPage = postOne(request.getParameter("viewState"), request.getParameter("checkCode")
				, request.getParameter("cookie"));
		
		response.getWriter().write(resultPage);
	}
	
	private String postOne(String viewState,String checkCode,String cookie){
    	HashMap<String,String> params = new HashMap<String,String>();
    	String reply = "";
    	String xmStr = "",gnmkdmStr = "";
    	String updatedViewState = "";
    	//String viewState = "";
    	String ddlXNStr = "2014-2015",ddlXQStr="";
    	int debug = 0;
    	Document doc = null;
    	Element form;
    	/**
    	 *  ��1����get��¼ҳ��
    	 *  ����ҳ�棺�õ�ҳ���е�ViewStateֵ
    	 */
//    	HttpManager.clearSpecialHeader();
//		HttpManager.addSpecialHeader("content-type", "application/x-www-form-urlencoded");
//		reply = HttpManager.sendGet(loginUrlStr, "");
//    	doc = Jsoup.parse(reply);
//		form = doc.select("input[name=__VIEWSTATE]").first();
//		viewState = form.attr("value");
    	viewState =  viewState.replaceAll("[+]", "%2B");
//		System.out.println("__VIEWSTATE-->"+viewState);
//    	
    	/** 
    	 *  ��2�������ݵ�¼��Ϣ������post�������������ѧ�á����롢��֤��
    	 *  ����ҳ�棺ȡ����Ӧͷ�е�Set-Cookie���ݡ�
    	 */
    	params.put("__VIEWSTATE",viewState);
    	params.put("txtUserName","105052012035");
    	params.put("TextBox2","a4842029578");
        params.put("txtSecretCode",checkCode);
        params.put("RadioButtonList1","ѧ��");
        params.put("Button1","");
        params.put("lbLanguage","");
        params.put("hidPdrs","");
        params.put("hidsc","");
        
        HttpManager.clearSpecialHeader();
        HttpManager.addSpecialHeader("Accept", "image/gif, image/jpeg, image/pjpeg, application/x-ms-application, application/xaml+xml, application/x-ms-xbap, */*");
        HttpManager.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
        HttpManager.addSpecialHeader("Cache-Control","max-age=0");
        HttpManager.addSpecialHeader("Host", "jwgl.fjnu.edu.cn");
        HttpManager.addSpecialHeader("Referer","http://jwgl.fjnu.edu.cn/");
        HttpManager.addSpecialHeader("Cookie", cookie);
        HttpManager.addSpecialHeader("Accept-Encoding","gzip, deflate");
        HttpManager.addSpecialHeader("Accept-Language","zh-CN,en,*");
        System.out.println("Params:" + params.toString());
        //System.out.print(HttpManager.sendPost(loginUrlStr, params));
        HttpManager.sendPost(loginUrlStr, params);
        
        /** 
         *  ��3��������Cookieͷ��get����
         *  ����ҳ�棺����������Ϣ���������[XM]
         */
        System.out.println("I will Comming!!!");
        HttpManager.clearSpecialHeader();
		HttpManager.addSpecialHeader("Cookie", /*HttpManager.*/cookie);
		reply = HttpManager.sendGet(mainUrlStr + "105052012035", "");
		System.out.println("I am Comming!!!");
		
	    Matcher xmMatcher = Pattern.compile("xm=(.{0,12})&gnmkdm=N121618").matcher(reply);
	    if(xmMatcher.find())
        	xmStr=xmMatcher.group(1);
        gnmkdmStr="N121618";//��ʾ�ɼ���ѯ�ı��
        
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX"+xmStr);
		
		/**
		 * 	��4����get���󣬻�ȡ ���µ�ViewState�Է��ʳɼ���ѯҳ��
		 *  ����ҳ�棺����VIEWSTATE,���������ҽ����еġ�+���滻�ɡ�%2B��
		 */
        String newMainUrl = "http://jwgl.fjnu.edu.cn/xscj_gc.aspx?xh="+"105052012035"+"&xm="+xmStr+"&gnmkdm="+gnmkdmStr;
		String refererUrl = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=" + "105052012035";
		HttpManager.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
		HttpManager.addSpecialHeader("Referer",refererUrl);
		reply = HttpManager.sendGet(newMainUrl, "");
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
		params.put("ddlXN",ddlXNStr);//ѧ��
		params.put("ddlXQ",ddlXQStr);//ѧ��
		//params.put("btnCx","+��++ѯ+");//��ѧ�ڻ��߰���ѧ���ѯ
		params.put("Button5","��ѧ���ѯ");
		
		refererUrl = "http://jwgl.fjnu.edu.cn/xscj_gc.aspx?xh=" + 
				"105052012035" + "&xm="+xmStr+"&gnmkdm="+gnmkdmStr;
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
		HttpManager.addSpecialHeader("Cookie", cookie);
		
		reply = HttpManager.sendPost(refererUrl, params);
		
		return reply;
    }

}
