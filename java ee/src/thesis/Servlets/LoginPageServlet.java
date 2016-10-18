package thesis.Servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import thesis.httpMethod.NetworkManager;
import thesis.logic.JsonTool;

/**
 * Servlet implementation class LoginPageServlet
 */
@WebServlet(description = "获取登录界面的ViewState和Cookie", urlPatterns = { "/LoginPageServlet" })
public class LoginPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginPageServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("gb2312");
		NetworkManager nm = new NetworkManager();
		
		String viewState	= getOne(nm);
		String cookie		= nm.cookies.get("http://jwgl.fjnu.edu.cn/default2.aspx");
		HashMap<String, String> data = new HashMap<String,String>(){
			{
				put("viewState", viewState);
				put("cookie",cookie);
			}
		};
		response.getWriter().write(JsonTool.convMap2Json(data, "loginPage").toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private String getOne(NetworkManager nm){
		String reply = "",viewState = "";
		Document doc = null;
    	Element form;
    	/**
    	 *  第1步，get登录页面
    	 *  返回页面：得到页面中的ViewState值
    	 */
    	nm.clearSpecialHeader();
		nm.addSpecialHeader("content-type", "application/x-www-form-urlencoded");
		//HttpManager.addSpecialHeader("Cookie", "ASP.NET_SessionId=k2koub55s0khs5anx22pmb55");
		reply = nm.sendGet("http://jwgl.fjnu.edu.cn/default2.aspx", "");
    	doc = Jsoup.parse(reply);
		form = doc.select("input[name=__VIEWSTATE]").first();
		viewState = form.attr("value");
    	viewState = viewState.replaceAll("[+]", "%2B");
    	return viewState;
	}


}
