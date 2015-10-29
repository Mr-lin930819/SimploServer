package thesis.Servlet.test;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thesis.httpMethod.HttpManager;

/**
 * Servlet implementation class TestGetCookieServlet
 */
@WebServlet(description = "测试获取Cookie过程", urlPatterns = { "/TestGetCookieServlet" })
public class TestGetCookieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestGetCookieServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("gb2312");
		HttpManager.clearSpecialHeader();
		HttpManager.addSpecialHeader("Host", "jwgl.fjnu.edu.cn");
		HttpManager.addSpecialHeader("Cache-Control", "max-age=0");
		HttpManager.addSpecialHeader("Accept_Encoding", "gzip, deflate, sdch");
		HttpManager.addSpecialHeader("Accept_Language", "zh-CN,zh;q=0.8");
		//HttpManager.sendGet("http://jwgl.fjnu.edu.cn/", "");
		HttpManager.addSpecialHeader("Cookie",request.getParameter("checkCode"));
		response.getWriter().write(HttpManager.sendGet("http://jwgl.fjnu.edu.cn/xs_main.aspx", "xh=105052012035"));
	}

}
