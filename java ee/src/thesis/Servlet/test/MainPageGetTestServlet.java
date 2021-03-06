package thesis.Servlet.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ForkJoinPool.ManagedBlocker;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thesis.httpMethod.HttpManager;

/**
 * Servlet implementation class MainPageGetTestServlet
 */
@WebServlet(description = "主界面get测试", urlPatterns = { "/MainPageGetTestServlet" })
public class MainPageGetTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String loginUrlStr = "http://jwgl.fjnu.edu.cn/default2.aspx";
	private final String mainUrlStr = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=";
	private final String checkImageUrlStr = "http://jwgl.fjnu.edu.cn/CheckCode.aspx";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainPageGetTestServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String mainUrl = "http://jwgl.fjnu.edu.cn/xs_main.aspx";
		String resultPage = "";
		response.setCharacterEncoding("gb2312");
		HttpManager.clearSpecialHeader();
//		HttpManager.addSpecialHeader("Host", "jwgl.fjnu.edu.cn");
//		HttpManager.addSpecialHeader("Cache-Control", "max-age=0");
//		HttpManager.addSpecialHeader("Referer", "http://jwgl.fjnu.edu.cn/");
//		HttpManager.addSpecialHeader("Accept_Encoding", "gzip, deflate, sdch");
//		HttpManager.addSpecialHeader("Accept_Language", "zh-CN,zh;q=0.8");
		HttpManager.addSpecialHeader("Cookie", request.getParameter("cookie"));
		resultPage = HttpManager.sendGet(mainUrl, "xh=105052012035");
		
		//resultPage = PostOne(request.getParameter("viewState"), request.getParameter("viewState")
		//		, request.getParameter("cookie"));
		
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
        HttpManager.addSpecialHeader("Cache-Control","no-cache");
        HttpManager.addSpecialHeader("Host", "jwgl.fjnu.edu.cn");
        HttpManager.addSpecialHeader("Referer","http://jwgl.fjnu.edu.cn/");
        HttpManager.addSpecialHeader("Cookie", cookie);
        System.out.println("Params:" + params.toString());
        //System.out.print(HttpManager.sendPost(loginUrlStr, params));
        HttpManager.sendPost(loginUrlStr, params);
        
        /** 
         *  第3步，设置Cookie头，get请求；
         *  返回页面：包含姓名信息，解析获得[XM]
         */
        System.out.println("I will Comming!!!");
        HttpManager.clearSpecialHeader();
		HttpManager.addSpecialHeader("Cookie", HttpManager.cookies.get(loginUrlStr));
		reply = HttpManager.sendGet(mainUrlStr + "105052012035", "");
		System.out.println("I am Comming!!!");
		System.out.print(reply);
		
	    Matcher xmMatcher = Pattern.compile("xm=(.{0,12})&gnmkdm=N121617").matcher(reply);
	    if(xmMatcher.find())
        	xmStr=xmMatcher.group(1);
        gnmkdmStr="N121617";//表示成绩查询的编号
        
        System.out.println("\n"+xmStr);
		
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
