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

import thesis.JavaBean.SearchInfo;
import thesis.httpMethod.NetworkManager;

/**
 * Servlet implementation class QueryGradeServlet
 */
@WebServlet(description = "��ѯ�ɼ���Servlet", urlPatterns = { "/QueryGradeServlet" })
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
		 * 	��4����get���󣬻�ȡ ���µ�ViewState�Է��ʳɼ���ѯҳ��
		 *  ����ҳ�棺����VIEWSTATE,���������ҽ����еġ�+���滻�ɡ�%2B��
		 */
		String refererUrl = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=" + user.getNumber();
		
		nm.clearSpecialHeader();
		nm.addSpecialHeader("Cookie", /*HttpManager.*/user.getCookie());
		
		/**
		 * �˴�������ʱ�Ƶ���¼���ִ����������
		 */
//		reply = nm.sendGet(refererUrl, "");
//		Matcher xmMatcher = Pattern.compile("xm=(.{0,12})&gnmkdm=N121618").matcher(reply);
//		if(xmMatcher.find())
//	       	xmStr = xmMatcher.group(1);
		xmStr = user.getName();
		String newMainUrl = "http://jwgl.fjnu.edu.cn/xscj_gc.aspx?xh="+user.getNumber()+"&xm="+xmStr+"&gnmkdm="+"N121618";//gnmkdm="N121618";��ʾ�ɼ���ѯ�ı��
		
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
		 * ��5����post��������ѧ�����Ϣ��Ϊpost����������ͷ��Ϣ
		 * ����ҳ�棺�ɼ���ѯ��������Խ�����óɼ�
		 */
		params.clear();
//		params.put("__EVENTARGUMENT","");
//		params.put("__EVENTTARGET","");
		params.put("__VIEWSTATE",updatedViewState);
		//params.put("hidLanguage","");
		params.put("ddlXN",user.getxNStr());//ѧ��
		params.put("ddlXQ",user.getxQStr());//ѧ��
		//params.put("btnCx","+��++ѯ+");//��ѧ�ڻ��߰���ѧ���ѯ
		//params.put("Button5","��ѧ���ѯ");
		params.put("Button1","��ѧ�ڲ�ѯ");//ͳһ��Ϊ��ѧ�ڲ�ѯ����Ϊѧ�������ͬ��������ɰ�ѧ���ѯ
		
		refererUrl = "http://jwgl.fjnu.edu.cn/xscj_gc.aspx?xh=" + 
				user.getNumber() + "&xm="+xmStr+"&gnmkdm="+"N121618";
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
	 * ���һ������ȡĿ��ҳ���еĳɼ����ݣ�����װΪjson����
	 * @param content	ҳ������
	 * @param user		��¼�û���Ϣ
	 * @return			json����
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
		
		for(Element course:courses){
			name = course.select("td").get(3).text();
			grade = course.select("td").get(8).text();
			if(name.equals("�γ�����"))
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
