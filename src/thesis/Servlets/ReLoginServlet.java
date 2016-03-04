package thesis.Servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.JSONException;
import org.json.JSONObject;

import thesis.CommonInfo.RequestKey;
import thesis.DBOperation.HBEntityUtil;
import thesis.DBOperation.HBUtil;
import thesis.JavaBean.UserInfoEntity;
import thesis.Servlets.TryLoginServlet.LoginRstCode;
import thesis.httpMethod.NetworkManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mrlin on 2016/3/2.
 */
@WebServlet(urlPatterns = "/ReLoginServlet", description = "身份过期重登录")
public class ReLoginServlet extends HttpServlet {
	private String xmStr = "";
	
	enum LoginRstCode{
		SUCCESS,
		CHECKCODE_ERROR,
		SERVER_ERROR
	};
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setCharacterEncoding("gb2312");
		HashMap<String, String> loginInfo = new HashMap<String,String>(){
			{
				put("openid",request.getParameter(RequestKey.OPEN_ID));
				put("viewState",request.getParameter(RequestKey.VIEWSTATE));
				put("cookie",request.getParameter(RequestKey.COOKIE));
				put("checkCode",request.getParameter(RequestKey.CHECKCODE));
			}
		};
		JSONObject main = new JSONObject();
		JSONObject body = new JSONObject();
		try{
			LoginRstCode rstCode = tryLogin(loginInfo);
			if( rstCode == LoginRstCode.SUCCESS){
				main.put("result", "SUCCE");
			}else if(rstCode == LoginRstCode.CHECKCODE_ERROR){
				main.put("result", "ERRVR");
			}else if(rstCode == LoginRstCode.SERVER_ERROR){
				main.put("result", "ERRSV");
			}
			body.put("reLoginRst", main);
//			System.out.println("\n" + body.toString());
			response.getWriter().write(body.toString());
		}catch(JSONException e){
			e.printStackTrace();
		}
    }
    
	private LoginRstCode tryLogin(Map<String,String> loginInfo){
		NetworkManager nm = new NetworkManager();
		HashMap<String,String> params = new HashMap<String,String>();
    	String lgRstPage, stuMainPage;
        Matcher codeVaileMatcher, xmMatcher;
        UserInfoEntity user = HBEntityUtil.getUserInfo(loginInfo.get("openid"));

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
    	params.put("txtUserName", user.getStuNumber());
    	params.put("TextBox2", user.getStuPassword());
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
        
        if(lgRstPage == null || stuMainPage == null)
			return LoginRstCode.SERVER_ERROR;
		codeVaileMatcher = Pattern.compile("验证码不正确").matcher(lgRstPage);
        if(codeVaileMatcher.find()) {    //验证码不正确
            return LoginRstCode.CHECKCODE_ERROR;
        }
        xmMatcher = Pattern.compile("<span id=\"xhxm\">(.{0,12})同学</span>").matcher(stuMainPage);
		if(xmMatcher.find()){
			xmStr = xmMatcher.group(1);
			updateUserInfo(loginInfo);
			return LoginRstCode.SUCCESS;
		}else{
			return LoginRstCode.SERVER_ERROR;
		}
	}
	
	private void updateUserInfo(Map<String, String>loginInfo){
		Session session = HBUtil.getSession();
        UserInfoEntity userInfo = (UserInfoEntity)session
                .get(UserInfoEntity.class, loginInfo.get("openid"));
        session.beginTransaction();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        userInfo.setStoredCookie(loginInfo.get("cookie"));
        userInfo.setGenDate(java.sql.Date.valueOf(sdf.format(new Date())));
        
        session.save(userInfo);
        session.getTransaction().commit();
        session.close();
	}
}
