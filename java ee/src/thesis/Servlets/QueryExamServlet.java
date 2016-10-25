package thesis.Servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import thesis.CommonInfo.QueryCode;
import thesis.CommonInfo.QueryUrl;
import thesis.CommonInfo.RequestKey;
import thesis.DBOperation.HBEntityUtil;
import thesis.JavaBean.UserInfoEntity;
import thesis.logic.InfoQueryTemplate;

/**
 * Servlet implementation class QueryExamServlet
 */
@WebServlet(description = "��ѯ����ʱ���Servlet", urlPatterns = { "/QueryExamServlet" })
public class QueryExamServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryExamServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");

		UserInfoEntity userInfo = HBEntityUtil.getUserInfo(request.getParameter(RequestKey.OPEN_ID));
		ExamQueryInfo queryInfo = new ExamQueryInfo(){
			{
//				setNumber(request.getParameter("number"));
//				setName(request.getParameter("name"));
//				setCookie(request.getParameter("cookie"));
				setNumber(userInfo.getStuNumber());
				setName(userInfo.getStuName());
				setCookie(userInfo.getStoredCookie());
				setXn(request.getParameter(RequestKey.XN));
				setXq(request.getParameter(RequestKey.XQ));
				setFuncId(QueryCode.QUERY_EXAM);
			}
		};
		response.getWriter().write(new ExamQuery(queryInfo, QueryUrl.EXAM_QUERY).doQuery());
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
			
			//exmas�������п�����Ϣ�Ľڵ��б��������ÿһ��
			for(Element exam : exams){
				if(exams.indexOf(exam) == 0)
					continue;
				examInfo = new JSONArray();
				examInfo.put(exam.select("td").get(3).text());//����ʱ��
				examInfo.put(exam.select("td").get(4).text());//���Եص�
				examInfo.put(exam.select("td").get(6).text());//��λ��
				examInfo.put(exam.select("td").get(7).text());//У��
				try {
					retJson.put(exam.select("td").get(1).text(),examInfo);//�γ�����
				} catch (JSONException e) {
					e.printStackTrace();
				}//�γ�����, value)
			}
			return retJson.toString();
		}

		@Override
		protected void setSpecialParams(HashMap<String, String> params) {
			params.put("__EVENTTARGET", "xnd");
			params.put("__EVENTARGUMENT", "");
			params.put("xnd", mQueryInfo.xn);
			params.put("xqd", mQueryInfo.xq);
		}

		@Override
		protected String handleError(String reply) {
			//FIXME ��ѯ�ɼ�����
			Matcher matcher = Pattern.compile("alert\\('(.*)'").matcher(reply);
			if(matcher.find()){
				return "CODE2";
			}
			return "";
		}
		
	}

}
