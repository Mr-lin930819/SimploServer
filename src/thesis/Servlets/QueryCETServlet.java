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

import thesis.Servlets.QueryExamServlet.ExamQueryInfo;
import thesis.logic.InfoQueryTemplate;

/**
 * Servlet implementation class QueryCETServlet
 */
@WebServlet("/QueryCETServlet")
public class QueryCETServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryCETServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("gb2312");
		CETQueryInfo queryInfo = new CETQueryInfo(){
			{
				setNumber(request.getParameter("number"));
				setName(request.getParameter("name"));
				setCookie(request.getParameter("cookie"));
				setXn(request.getParameter("xn"));
				setXq(request.getParameter("xq"));
				setFuncId("N121606");
			}
		};
		response.getWriter().write(new QueryCET(queryInfo, "http://jwgl.fjnu.edu.cn/xsdjkscx.aspx").doQuery());
	}
	
	class QueryCET extends InfoQueryTemplate{
		CETQueryInfo mCETInfo; 
		public QueryCET(CETQueryInfo info,String url){
			super(info.getNumber(), info.getName() ,info.getCookie(), info.getFuncId(), url, false);
			mCETInfo = info;
		}
		@Override
		protected String parseReply(String reply) {
			Document doc;
			Element table;
			Elements exams;
			JSONArray examInfo = null;
			JSONObject retJson = new JSONObject();
			doc = Jsoup.parse(reply);
			table = doc.select("table[id=DataGrid1]").first();
			exams = table.select("tbody").first().select("tr");
			
			//exmas包含所有考试信息的节点列表，遍历获得每一项
			for(Element exam : exams){
				if(exams.indexOf(exam) == 0)
					continue;
				examInfo = new JSONArray();
				examInfo.put(exam.select("td").get(0).text());//学年
				examInfo.put(exam.select("td").get(1).text());//学期
				examInfo.put(exam.select("td").get(2).text());//等级考试名称
				examInfo.put(exam.select("td").get(3).text());//准考证号
				examInfo.put(exam.select("td").get(4).text());//考试日期
				examInfo.put(exam.select("td").get(5).text());//成绩
				
				examInfo.put(exam.select("td").get(6).text());//听力成绩
				examInfo.put(exam.select("td").get(7).text());//阅读成绩
				examInfo.put(exam.select("td").get(8).text());//综合成绩
				try {
					retJson.put(String.valueOf(exams.indexOf(exam)),examInfo);
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}//课程名称, value)
			}
			return retJson.toString();
		}
		@Override
		protected void setSpecialParams(HashMap<String, String> params) {
			
		}
		
	}
	
	class CETQueryInfo{
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
	
}
