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
@WebServlet(description = "��ѯ�ɼ���Ϣ", urlPatterns = { "/GradeQueryServlet" })
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
			outJsonDoc.put("����Json", 780);
			outJsonDoc.put("�ɼ�", queryResult);
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
	 * ͨ���û����������ѯ�ɼ���Ϣ����������HashMap��һ����Ŀ��Ӧһ���ɼ�
	 * @author Lin
	 * @param name 		- �û���
	 * @param passwd	- ����
	 * @return HashMap<String,Double> 
	 */
	private HashMap<String,Double> queryGrade(String name,String passwd){
		if(!tryLogin(name, passwd)){
			return null;
		}
		
		HashMap<String,Double> grades = new HashMap<String,Double>();
		grades.put("��ѧ", 93.3);
		return grades;
		
	}
	
	/**
	 * �����Ƿ��ǺϷ����û���������
	 * @param name		���� �û���
	 * @param passwd	���� ����
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
