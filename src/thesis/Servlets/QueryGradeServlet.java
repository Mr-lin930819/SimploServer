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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import thesis.JavaBean.QueryInfo;
import thesis.JavaBean.SearchInfo;
import thesis.httpMethod.NetworkManager;
import thesis.CommonInfo.PostParamKey;
import thesis.CommonInfo.QueryCode;
import thesis.CommonInfo.QueryUrl;

/**
 * Servlet implementation class QueryGradeServlet
 */
@WebServlet(description = "查询成绩的Servlet", urlPatterns = { "/QueryGradeServlet" })
public class QueryGradeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryGradeServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("gb2312");
		String resultPage = null,jsonText = null;
		SearchInfo searchInfo = new SearchInfo(){
			{
				setNumber(request.getParameter("number"));
				setCookie(request.getParameter("cookie"));
				setxNStr(request.getParameter("xn"));
				setxQStr(request.getParameter("xq"));
				setName(request.getParameter("xm"));
			}
		};
		resultPage = postForGradeQuery(searchInfo);
		jsonText = saveGradeToJson(resultPage);
		System.out.print(jsonText);
		response.getWriter().append(jsonText);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private String postForGradeQuery(SearchInfo user){
		NetworkManager nm = new NetworkManager();
    	HashMap<String,String> params = new HashMap<String,String>();
    	String reply = "";
    	//String xmStr = "",gnmkdmStr = "";
    	String updatedViewState = "",xmStr = "";
    	//String viewState = "";
    	//String ddlXNStr = "2014-2015",ddlXQStr="";
    	Document doc = null;
    	Element form;
		/**
		 * 	第4步，get请求，获取 最新的ViewState以访问成绩查询页面
		 *  返回页面：包含VIEWSTATE,解析，并且将其中的“+”替换成“%2B”
		 */
		String refererUrl = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=" + user.getNumber();
		
		nm.clearSpecialHeader();
		nm.addSpecialHeader("Cookie", /*HttpManager.*/user.getCookie());
		
		/**
		 * 此处请求暂时移到登录部分处理，获得姓名
		 */
//		reply = nm.sendGet(refererUrl, "");
//		Matcher xmMatcher = Pattern.compile("xm=(.{0,12})&gnmkdm=N121618").matcher(reply);
//		if(xmMatcher.find())
//	       	xmStr = xmMatcher.group(1);
		xmStr = user.getName();
		//String newMainUrl = "http://jwgl.fjnu.edu.cn/xscj_gc.aspx?xh="+user.getNumber()+"&xm="+xmStr+"&gnmkdm="+"N121618";//gnmkdm="N121618";表示成绩查询的编号
		String newMainUrl = QueryUrl.GRADE_QUERY + 
				"?xh=" + user.getNumber()+"&xm="+xmStr+"&gnmkdm="+QueryCode.QUERY_GRADE;//gnmkdm="N121618";表示成绩查询的编号
		nm.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
		nm.addSpecialHeader("Referer",refererUrl);
		reply = nm.sendGet(newMainUrl, "");
//		Matcher newVSmatcher = Pattern.compile("__VIEWSTATE\" value=\"([^>]*)\" />").matcher(reply);
//		if(newVSmatcher.find())
//			updatedViewState = newVSmatcher.group().replaceAll("+", "%2B");
		doc = Jsoup.parse(reply);
		form = doc.select("input[name=__VIEWSTATE]").first();
		updatedViewState = form.attr("value");
		updatedViewState =  updatedViewState.replaceAll("[+]", "%2B");
		/**
		 * 第5步，post请求，设置学年等信息作为post参数，设置头信息
		 * 返回页面：成绩查询结果，可以解析获得成绩
		 */
		params.clear();
//		params.put("__EVENTARGUMENT","");
//		params.put("__EVENTTARGET","");
		params.put("__VIEWSTATE",updatedViewState);
		//params.put("hidLanguage","");
		params.put(PostParamKey.DDL_XN,user.getxNStr());//学年
		params.put(PostParamKey.DDL_XQ,user.getxQStr());//学期
		//params.put("btnCx","+查++询+");//按学期或者按照学年查询
		//params.put("Button5","按学年查询");
		//params.put("Button1","按学期查询");//统一改为按学期查询，因为学期栏不填，同样可以完成按学年查询
		params.put(PostParamKey.CX_BTN, PostParamKey.CX_BTN_VAL);
		
		refererUrl = QueryUrl.GRADE_QUERY +"?xh=" + 
				user.getNumber() + "&xm="+xmStr+"&gnmkdm="+QueryCode.QUERY_GRADE;
		nm.clearSpecialHeader();
		nm.addSpecialHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		nm.addSpecialHeader("Accept-Encoding","gzip,deflate");
		nm.addSpecialHeader("Accept-Language","zh-CN");
		nm.addSpecialHeader("Cache-Control","no-cache");
		nm.addSpecialHeader("Connection","Keep-Alive");
		//nm.addSpecialHeader("Content-Length","4413");
		nm.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
		nm.addSpecialHeader("Host","jwgl.fjnu.edu.cn");
		nm.addSpecialHeader("Referer",refererUrl);
		nm.addSpecialHeader("User-Agent","Mozilla/5.0");
		nm.addSpecialHeader("Cookie", user.getCookie());
		
		reply = nm.sendPost(refererUrl, params);
		return reply;
    }
	
	/**
	 * 最后一步，提取目标页面中的成绩数据，并封装为json返回
	 * @param content	页面内容
	 * @param user		登录用户信息
	 * @return			json数据
	 */
	private String saveGradeToJson(String content){
		Document doc = null;
		Element table;
		Elements courses;
		String name,grade,result = null;
		JSONObject jGrades = new JSONObject();
		JSONObject jMain = new JSONObject();
		
		doc = Jsoup.parse(content);
		table = doc.select("table[class=datelist]").first();
		courses = table.select("tbody").select("tr");
		
		System.out.println(content);
		
		for(Element course:courses){
			name = course.select("td").get(3).text();
			grade = course.select("td").get(11).text();
			if(name.equals("课程名称"))
				continue;
			try {
				jGrades.put(name, grade);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		//jGrades.put(grades);
		try {
			jMain.put("GRADE", jGrades);
			result = jMain.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
