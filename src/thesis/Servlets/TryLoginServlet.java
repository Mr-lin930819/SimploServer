package thesis.Servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import thesis.DBOperation.HBUtil;
import thesis.JavaBean.UserInfoEntity;
import thesis.Main.Main;
import thesis.httpMethod.NetworkManager;

/**
 * Servlet implementation class TryLoginServlet
 */
@WebServlet(description = "尝试登陆的Servlet", urlPatterns = { "/TryLoginServlet" })
public class TryLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String xmStr = "";
	
	enum LoginRstCode{
		SUCCESS,
		CHECKCODE_ERROR,
		PASSWD_ERROR
	};
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TryLoginServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("gb2312");

//        HBUtil hbUtil = HBUtil.getInstance();
        Session session = null;
        System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
    	session = Main.getSession();
        session.beginTransaction();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        UserInfoEntity userInfo = new UserInfoEntity(){
            {
                setStuNumber(request.getParameter("number"));
                setStuPassword(request.getParameter("password"));
                setStoredCookie(request.getParameter("cookie"));
                setGenDate(java.sql.Date.valueOf(sdf.format(new Date())));
            }
        };
        session.save(userInfo);
        session.getTransaction().commit();
        session.close();

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
		JSONObject main = new JSONObject();
		JSONObject body = new JSONObject();
		try{
			if(tryLogin(loginInfo) == LoginRstCode.SUCCESS){
				main.put("xm", xmStr);
				main.put("can", "1");

			}else{
				main.put("xm", "");
				main.put("can", "0");
			}
			body.put("TRY", main);
			System.out.println("\n" + body.toString());
			response.getWriter().write(body.toString());
		}catch(JSONException e){
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private LoginRstCode tryLogin(Map<String,String> loginInfo){
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
        
        //nm.sendPost("http://jwgl.fjnu.edu.cn/default2.aspx", params);
        
        System.out.println(nm.sendPost("http://jwgl.fjnu.edu.cn/default2.aspx", params));
        String temp = nm.sendGet("http://jwgl.fjnu.edu.cn/xs_main.aspx?xh="+ loginInfo.get("number"), "");
		if(temp == null)
			return LoginRstCode.PASSWD_ERROR;
        //Matcher xmMatcher = Pattern.compile("xm=(.{0,12})&gnmkdm=N121618")
		//		.matcher(temp);
		Matcher xmMatcher = Pattern.compile("<span id=\"xhxm\">(.{0,12})同学</span>").matcher(temp);
		
		if(xmMatcher.find()){
			xmStr = xmMatcher.group(1);
			System.out.print("NNNNNNNNNNNNNNAME--" + xmStr);
			return LoginRstCode.SUCCESS;
		}else{
			return LoginRstCode.PASSWD_ERROR;
		}
		
//        if(nm.sendGet("http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=" + loginInfo.get("number"), "") != null){
//        	return true;
//        }else{
//        	return false;
//        }
	}

}
