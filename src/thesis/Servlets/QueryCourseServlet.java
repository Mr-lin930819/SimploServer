package thesis.Servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import thesis.logic.InfoQueryTemplate;

/**
 * Servlet implementation class QueryCourseServlet
 */
@WebServlet(description = "获取课程表信息", urlPatterns = { "/QueryCourseServlet" })
public class QueryCourseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryCourseServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String result;
		response.setCharacterEncoding("gb2312");
		
		result = new CourseQuery(request.getParameter("number"), request.getParameter("name"), 
				request.getParameter("cookie"), "N181617").doQuery();
		response.getWriter().append(result);
	}
	
	class CourseQuery extends InfoQueryTemplate{

		public CourseQuery(String number, String name, String cookie, String funcId) {
			super(number, name, cookie, funcId);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected String parseReply(String reply) {
			// TODO Auto-generated method stub
			Document doc = null;
			Element table;
			Elements courses;
			String name,grade,result = null;
			JSONObject jGrades = new JSONObject();
			JSONObject jMain = new JSONObject();
			
			doc = Jsoup.parse(reply);
			table = doc.select("table[class=datelist]").first();
			courses = table.select("tbody").select("tr");
			
			for(Element course:courses){
				name = course.select("td").get(3).text();
				grade = course.select("td").get(8).text();
				if(name.equals("课程名称"))
					continue;
				try {
					jGrades.put(name, grade);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//jGrades.put(grades);
			try {
				jMain.put("GRADE", jGrades);
				result = jMain.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void setSpecialParams(HashMap<String, String> params) {
			// TODO Auto-generated method stub
			params.put("ddlXN",user.getxNStr());//学年
			params.put("ddlXQ",user.getxQStr());//学期
			params.put("Button1","按学期查询");//统一改为按学期查询，因为学期栏不填，同样可以完成按学年查询
		}
		
	}

}
