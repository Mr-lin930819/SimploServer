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

import org.hibernate.Session;
import org.json.JSONException;
import org.json.JSONObject;

import thesis.CommonInfo.RequestKey;
import thesis.DBOperation.HBUtil;
import thesis.JavaBean.UserInfoEntity;
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
        
		HashMap<String, String> loginInfo = new HashMap<String,String>(){
			{
				put("number",request.getParameter(RequestKey.NUMBER));
				put("password",request.getParameter(RequestKey.PASSWORD));
				put("viewState",request.getParameter(RequestKey.VIEWSTATE));
				put("cookie",request.getParameter(RequestKey.COOKIE));
				put("checkCode",request.getParameter(RequestKey.CHECKCODE));
			}
		};
		System.out.print(loginInfo.toString());
		JSONObject main = new JSONObject();
		JSONObject body = new JSONObject();
		try{
			LoginRstCode rstCode = tryLogin(loginInfo);
			if( rstCode == LoginRstCode.SUCCESS){
				main.put("xm", xmStr);
				main.put("lgRstCode", "1");
				main.put("openAppId", saveUser(xmStr, loginInfo));
			}else if(rstCode == LoginRstCode.CHECKCODE_ERROR){
				main.put("xm", "");
				main.put("lgRstCode", "2");
                main.put("openAppId", "");
			}else{
				main.put("xm", "");
				main.put("lgRstCode", "0");
                main.put("openAppId", "");
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
    	String reply = "",nameStr = "", lgRstPage, stuMainPage;
        Matcher codeVaileMatcher, xmMatcher;

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
        
        lgRstPage = nm.sendPost("http://jwgl.fjnu.edu.cn/default2.aspx", params);
        //System.out.println(nm.sendPost("http://jwgl.fjnu.edu.cn/default2.aspx", params));
        stuMainPage = nm.sendGet("http://jwgl.fjnu.edu.cn/xs_main.aspx?xh="+ loginInfo.get("number"), "");
		
        System.out.println(stuMainPage);
        
        if(lgRstPage == null || stuMainPage == null)
			return LoginRstCode.PASSWD_ERROR;
        //Matcher xmMatcher = Pattern.compile("xm=(.{0,12})&gnmkdm=N121618")
		//		.matcher(temp);
		codeVaileMatcher = Pattern.compile("验证码不正确").matcher(lgRstPage);
        if(codeVaileMatcher.find()) {    //验证码不正确
            return LoginRstCode.CHECKCODE_ERROR;
        }
        xmMatcher = Pattern.compile("<span id=\"xhxm\">(.{0,12})同学</span>").matcher(stuMainPage);
		if(xmMatcher.find()){
			xmStr = xmMatcher.group(1);
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
	
	private String saveUser(String name, HashMap<String, String>loginInfo){
		Session session = HBUtil.getSession();
		String uuid;
        session.beginTransaction();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        UserInfoEntity userInfo = new UserInfoEntity();
        userInfo.setStuNumber(loginInfo.get("number"));
        userInfo.setStuPassword(loginInfo.get("password"));
        userInfo.setStoredCookie(loginInfo.get("cookie"));
        userInfo.setStuName(name);
        userInfo.setGenDate(java.sql.Date.valueOf(sdf.format(new Date())));
        
        session.save(userInfo);
        uuid = userInfo.getOpenAppUserId();
        session.getTransaction().commit();
        session.close();
        return uuid;
	}

}
