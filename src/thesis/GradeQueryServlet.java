package thesis;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;

/**
 * Servlet implementation class GradeQueryServlet
 */
@WebServlet(description = "查询成绩信息", urlPatterns = { "/GradeQueryServlet" })
public class GradeQueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GradeQueryServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("gb2312");
		//response.setHeader("content-type","text/html;charset=UTF-8");
		HashMap<String,Double> grades;
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		JSONArray queryResult = new JSONArray();
		
		grades = queryGrade(userName, password);
		queryResult.put(grades);
		JSONObject outJsonDoc = new JSONObject();
		try {
			outJsonDoc.put("测试Json", 780);
			outJsonDoc.put("成绩", queryResult);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(grades == null)
			request.getRequestDispatcher("/PasswdWrong.jsp").forward(request, response);
		else
			response.getWriter().append("Served at: ").append(request.getContextPath() + " == " + outJsonDoc.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	/**
	 * 通过用户名和密码查询成绩信息，方法返回HashMap，一个科目对应一个成绩
	 * @author Lin
	 * @param name 		- 用户名
	 * @param passwd	- 密码
	 * @return HashMap<String,Double> 
	 */
	private HashMap<String,Double> queryGrade(String name,String passwd){
		if(!tryLogin(name, passwd)){
			return null;
		}
		
		HashMap<String,Double> grades = new HashMap<String,Double>();
		grades.put("数学", 93.3);
		return grades;
		
	}
	
	/**
	 * 测试是否是合法的用户名和密码
	 * @param name		—— 用户名
	 * @param passwd	—— 密码
	 * @return		boolean
	 */
	private boolean tryLogin(String name,String passwd){
		
		if(name.equals("hello")&& passwd.equals("123")){
			return true;
		}else{
			return false;
		}
		
	}

}
