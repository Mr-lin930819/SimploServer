package thesis.Servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import thesis.CommonInfo.QueryCode;
import thesis.CommonInfo.QueryUrl;
import thesis.CommonInfo.RequestKey;
import thesis.DBOperation.HBEntityUtil;
import thesis.JavaBean.UserInfoEntity;
import thesis.logic.InfoQueryTemplate;

/**
 * Servlet implementation class QueryCourseServlet
 */
@WebServlet(description = "获取课程表信息", urlPatterns = { "/QueryCourseServlet" })
public class QueryCourseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private CourseQueryInfo courseQueryInfo = new CourseQueryInfo();   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryCourseServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String result;
		response.setCharacterEncoding("utf-8");
		UserInfoEntity userInfo = HBEntityUtil.getUserInfo(request.getParameter(RequestKey.OPEN_ID));
//		courseQueryInfo.setNumber(request.getParameter("number"));
//		courseQueryInfo.setName(request.getParameter("name"));
//		courseQueryInfo.setCookie(request.getParameter("cookie"));
		courseQueryInfo.setNumber(userInfo.getStuNumber());
		courseQueryInfo.setName(userInfo.getStuName());
		courseQueryInfo.setCookie(userInfo.getStoredCookie());
		courseQueryInfo.setXnd(request.getParameter(RequestKey.XN));
		courseQueryInfo.setXqd(request.getParameter(RequestKey.XQ));
		
		result = new CourseQuery(courseQueryInfo,QueryCode.QUERY_COURSE).doQuery();
		//System.out.print(result);
		response.getWriter().append(result);
	}
	
	class CourseQueryInfo {
		private String number;
		private String name;
		private String cookie;
		private String xnd;
		private String xqd;
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
		public String getXnd() {
			return xnd;
		}
		public void setXnd(String xnd) {
			this.xnd = xnd;
		}
		public String getXqd() {
			return xqd;
		}
		public void setXqd(String xqd) {
			this.xqd = xqd;
		}
		
	}
	
	class CourseQuery extends InfoQueryTemplate{
		
		private CourseQueryInfo mCourseQueryInfo = null;
		public CourseQuery(CourseQueryInfo info, String funcId) {
			super(info.number, info.name, info.cookie, funcId,QueryUrl.COURSE_QUERY);
			mCourseQueryInfo = info;
		}

		@Override
		protected String parseReply(String reply) {
			Document doc = null;
			Element table;
			Elements lesson;
			String lessonNum;
			String[] courseName = new String[7];
			JSONArray courses = null;
			JSONObject lessons = new JSONObject();
			
			doc = Jsoup.parse(reply);
			table = doc.select("table[id=Table1]").first();
			lesson = table.select("tbody").select("tr");
			
			for(Element course:lesson){
				int index = lesson.indexOf(course);
				if( index < 2)
					continue;
				else if(index % 2 == 0){
					lessonNum = course.select("td").get(
							(index==2 || index==6 || index==10) ? 1 : 0).text();
					courses = new JSONArray();
					for(int i = 0;i < 7;i++){
						courseName[i] = course.select("td")
								.get(i + ((index==2 || index==6 || index==10) ? 2 : 1))
								.text();
						//TODO : 添加整理课程数据函数
						courses.put(parseLessonContent(courseName[i]));
					}
					try {
						lessons.put(lessonNum, courses);
					} catch (JSONException e) {
						e.printStackTrace();
						return null;
					}
			 	}
				
			}
			return lessons.toString();
		}

		@Override
		protected void setSpecialParams(HashMap<String, String> params) {
			params.put("xnd",mCourseQueryInfo.getXnd());//学年
			params.put("xqd",mCourseQueryInfo.getXqd());//学期
			params.put("__EVENTTARGET","xqd");
			params.put("__EVENTARGUMENT","");
		}

		@Override
		protected String handleError(String reply) {
			Matcher matcher = Pattern.compile("alert('(.*)'").matcher(reply);
			if(matcher.find()){
				return "CODE2";
			}
			return "";
		}
		
	}
	
	private String parseLessonContent(String text){
		ArrayList<String> dataCollection = null;
		StringBuffer result = new StringBuffer();
		if(text.equals("?") || text.equals(""))
			return "";
		else {
			String []rawData = text.split(" ");
			dataCollection = new ArrayList<String>();
			for(int i=0; i<rawData.length;i++){
				dataCollection.add(rawData[i]);
			}
		}
		Pattern lessonTimePattern = Pattern.compile("第(.{1,2})-(.{1,2})周");
		Pattern lessonOddEvenPattern = Pattern.compile("第(.{1,2})-(.{1,2})周(.)(.)周");
		
		for(String timeText:dataCollection) {
			Matcher tempMatcher = lessonOddEvenPattern.matcher(timeText);
			if(tempMatcher.find()){
				int index = dataCollection.indexOf(timeText);
				result.append(tempMatcher.group(4) + "周;");
				result.append(dataCollection.get(index - 2) + ";");
				result.append(dataCollection.get(index + 1) + ";");
				if((index + 2) < dataCollection.size())
					result.append(dataCollection.get(index + 2) + "$");
				else
					result.append("$");
			} else {
				tempMatcher = lessonTimePattern.matcher(timeText);
				if(tempMatcher.find()) {
					int index = dataCollection.indexOf(timeText);
					result.append(tempMatcher.group(1) + "-" + tempMatcher.group(2) + "周;");
					result.append(dataCollection.get(index - 2) + ";");
					result.append(dataCollection.get(index + 1) + ";");
					if((index + 2) < dataCollection.size())
						result.append(dataCollection.get(index + 2) +"$");
					else
						result.append("$");
				}
			}
		}
		
		return result.toString();
	}

}
