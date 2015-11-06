package thesis.Servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thesis.httpMethod.NetworkManager;

/**
 * Servlet implementation class TryLoginServlet
 */
@WebServlet(description = "尝试登陆的Servlet", urlPatterns = { "/TryLoginServlet" })
public class TryLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TryLoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("gb2312");
		HashMap<String, String> loginInfo = new HashMap<String,String>(){
			{
				put("number",request.getParameter("number"));
				put("password",request.getParameter("password"));
				put("viewState",request.getParameter("viewState"));
				put("cookie",request.getParameter("cookie"));
				put("checkCode",request.getParameter("checkCode"));
			}
		};
		System.out.print(loginInfo.toString());
		if(tryLogin(loginInfo) == true){
			response.getWriter().write("1");
		}else{
			response.getWriter().write("0");
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private boolean tryLogin(Map<String,String> loginInfo){
		NetworkManager nm = new NetworkManager();
		HashMap<String,String> params = new HashMap<String,String>();
    	String reply = "",nameStr = "";
//   	String xmStr = "",gnmkdmStr = "";
    	/**
    	 *  第1步，get登录页面
    	 *  返回页面：得到页面中的ViewState值
    	 */
    	String viewStateNew = loginInfo.get("viewState").replaceAll("[+]", "%2B");
    	/** 
    	 *  第2步，根据登录信息，发送post请求，其参数包括学好、密码、验证码
    	 *  返回页面：取得响应头中的Set-Cookie数据。
    	 */
    	params.put("__VIEWSTATE",viewStateNew);
    	params.put("txtUserName",loginInfo.get("number"));
    	params.put("TextBox2",loginInfo.get("password"));
        params.put("txtSecretCode",loginInfo.get("checkCode"));
        params.put("RadioButtonList1","学生");
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
        nm.addSpecialHeader("Cookie", loginInfo.get("cookie"));
        nm.addSpecialHeader("Accept-Encoding","gzip, deflate");
        nm.addSpecialHeader("Accept-Language","zh-CN,en,*");
        
        nm.sendPost("http://jwgl.fjnu.edu.cn/default2.aspx", params);
		return true;
	}

}
