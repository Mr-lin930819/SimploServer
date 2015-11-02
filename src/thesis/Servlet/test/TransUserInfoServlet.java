package thesis.Servlet.test;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thesis.JavaBean.SearchInfo;

/**
 * Servlet implementation class TransUserInfoServlet
 */
@WebServlet(description = "将登录用户数据保存并转存", urlPatterns = { "/TransUserInfoServlet" })
public class TransUserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TransUserInfoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		SearchInfo user = new SearchInfo();
		user.setNumber(request.getParameter("username"));
		user.setPassword(request.getParameter("password"));
		user.setCookie(request.getParameter("cookie"));
		user.setCheckCode(request.getParameter("checkCode"));
		user.setxNStr(request.getParameter("xnStr"));
		user.setxQStr(request.getParameter("xqStr"));
		user.setInitViewState(request.getParameter("viewState"));
		request.setAttribute("loginInfo", user);
		request.getRequestDispatcher("/MainPageTestServlet?isFirst=0").forward(request, response);
	}
}
