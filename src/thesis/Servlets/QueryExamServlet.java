package thesis.Servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import thesis.logic.InfoQueryTemplate;

/**
 * Servlet implementation class QueryExamServlet
 */
@WebServlet(description = "查询考试时间的Servlet", urlPatterns = { "/QueryExamServlet" })
public class QueryExamServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryExamServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("gb2312");
		ExamQueryInfo queryInfo = new ExamQueryInfo(){
			{
				setNumber(request.getParameter("number"));
				setName(request.getParameter("name"));
				setCookie(request.getParameter("cookie"));
				setXn(request.getParameter("xn"));
				setXq(request.getParameter("xq"));
				setFuncId("N121604");
			}
		};
		response.getWriter().write(new ExamQuery(queryInfo, "http://jwgl.fjnu.edu.cn/xskscx.aspx").doQuery());
	}
	
	class ExamQueryInfo{
		private String number;
		private String name;
		private String cookie;
		private String xn;
		private String xq;
		private String funcId;
		public String getNumber() {
			return number;
		}
		public void setNumber(String number) {
			this.number = number;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getCookie() {
			return cookie;
		}
		public void setCookie(String cookie) {
			this.cookie = cookie;
		}
		public String getXn() {
			return xn;
		}
		public void setXn(String xn) {
			this.xn = xn;
		}
		public String getXq() {
			return xq;
		}
		public void setXq(String xq) {
			this.xq = xq;
		}
		public String getFuncId() {
			return funcId;
		}
		public void setFuncId(String funcId) {
			this.funcId = funcId;
		}
		
	}
	
	class ExamQuery extends InfoQueryTemplate{
		ExamQueryInfo mQueryInfo = null;

		public ExamQuery(ExamQueryInfo queryInfo, String url) {
			super(queryInfo.number, queryInfo.name, queryInfo.cookie, queryInfo.funcId, url);
			// TODO Auto-generated constructor stub
			mQueryInfo = queryInfo;
		}

		@Override
		protected String parseReply(String reply) {
			Document doc;
			Element table;
			Elements exams;
			JSONArray examInfo = null;
			JSONObject retJson = new JSONObject();
			
			doc = Jsoup.parse(reply);
			table = doc.select("table[class=datelist]").first();
			exams = table.select("tbody").first().select("tr");
			
			//exmas包含所有考试信息的节点列表，遍历获得每一项
			for(Element exam : exams){
				if(exams.indexOf(exam) == 0)
					continue;
				examInfo = new JSONArray();
				examInfo.put(exam.select("td").get(3).text());//考试时间
				examInfo.put(exam.select("td").get(4).text());//考试地点
				examInfo.put(exam.select("td").get(6).text());//座位号
				examInfo.put(exam.select("td").get(7).text());//校区
				try {
					retJson.put(exam.select("td").get(1).text(),examInfo);//课程名称
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//课程名称, value)
			}
			return retJson.toString();
		}

		@Override
		protected void setSpecialParams(HashMap<String, String> params) {
			// TODO Auto-generated method stub
			params.put("__EVENTTARGET", "xnd");
			params.put("__EVENTARGUMENT", "");
			params.put("xnd", mQueryInfo.xn);
			params.put("xqd", mQueryInfo.xq);
		}
		
	}

}
