package thesis.Servlet.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thesis.httpMethod.HttpManager;

/**
 * Servlet implementation class MainPageTestServlet
 */
@WebServlet(urlPatterns = { "/MainPageTestServlet" })
public class MainPageTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String loginUrlStr = "http://jwgl.fjnu.edu.cn/default2.aspx";
	private final String mainUrlStr = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=";
	private final String checkImageUrlStr = "http://jwgl.fjnu.edu.cn/CheckCode.aspx";
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
//        params.put("RadioButtonList1","学生");
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
		resultPage = PostOne(request.getParameter("viewState"), request.getParameter("checkCode")
				, request.getParameter("cookie"));
		
		response.getWriter().write(resultPage);
	}
	
	private String PostOne(String viewState,String checkCode,String cookie){
    	HashMap<String,String> params = new HashMap<String,String>();
    	String reply = "";
    	String xmStr = "",gnmkdmStr = "";
    	/** 
    	 *  第2步，根据登录信息，发送post请求，其参数包括学好、密码、验证码
    	 *  返回页面：取得响应头中的Set-Cookie数据。
    	 */
    	params.put("__VIEWSTATE",viewState);
    	params.put("txtUserName","105052012035");
    	params.put("TextBox2","a4842029578");
        params.put("txtSecretCode",checkCode);
        params.put("RadioButtonList1","学生");
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
         *  第3步，设置Cookie头，get请求；
         *  返回页面：包含姓名信息，解析获得[XM]
         */
        System.out.println("I will Comming!!!");
        HttpManager.clearSpecialHeader();
		HttpManager.addSpecialHeader("Cookie", /*HttpManager.*/cookie);
		reply = HttpManager.sendGet(mainUrlStr + "105052012035", "");
		System.out.println("I am Comming!!!");
		
	    Matcher xmMatcher = Pattern.compile("xm=(.{0,12})&gnmkdm=N121618").matcher(reply);
	    if(xmMatcher.find())
        	xmStr=xmMatcher.group(1);
        gnmkdmStr="N121618";//表示成绩查询的编号
        
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX"+xmStr);
		
		/**
		 * 	第4步，get请求，获取 最新的ViewState以访问成绩查询页面
		 *  返回页面：包含VIEWSTATE,解析，并且将其中的“+”替换成“%2B”
		 */
        String newMainUrl = "http://jwgl.fjnu.edu.cn/xscjcx_dq.aspx?xh="+"105052012035"+"&xm="+xmStr+"&gnmkdm="+gnmkdmStr;
		String refererUrl = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=" + "105052012035";
		HttpManager.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
		HttpManager.addSpecialHeader("Referer",refererUrl);
		reply = HttpManager.sendGet(newMainUrl, "");
		return reply;
    }

}
