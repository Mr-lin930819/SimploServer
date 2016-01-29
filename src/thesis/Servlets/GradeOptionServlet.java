package thesis.Servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import thesis.CommonInfo.QueryCode;
import thesis.CommonInfo.QueryUrl;
import thesis.DBOperation.HBUtil;
import thesis.JavaBean.UserInfoEntity;
import thesis.httpMethod.NetworkManager;

/**
 * Servlet implementation class GradeOptionServlet
 */
@WebServlet(description = "��ȡ��ѯ�ɼ�ѡ��Լ�ѧ��רҵ��Ϣ����Servlet", urlPatterns = { "/GradeOptionServlet" })
public class GradeOptionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GradeOptionServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("gb2312");
		String resultPage = null,jsonText = null;
		System.out.println(request.getParameter("openUserId"));
//		System.out.println(request.getParameter("number") + "  " + request.getParameter("xm") +
//				" " + request.getParameter("cookie"));
		Session session = HBUtil.getSession();
		UserInfoEntity userInfo = (UserInfoEntity)session.load(UserInfoEntity.class,
                request.getParameter("openUserId"));
//		resultPage = postForCondition(request.getParameter("number"),request.getParameter("xm"),
//				request.getParameter("cookie"));
        
        resultPage = postForCondition(userInfo.getStuNumber(),userInfo.getStuName(),
				userInfo.getStoredCookie());
		jsonText = parseReply2Json(resultPage);
		session.close();
		response.getWriter().append(jsonText);
	}
	
	private String postForCondition(String number,String xm,String cookie){
		NetworkManager nm = new NetworkManager();
    	String reply;
    	
    	String referUrl = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=" + number;
    	String mainUrl = QueryUrl.GRADE_OPTION;
//    	+ "?xh=" + number+"&xm="+"���"+
//    			"&gnmkdm="+QueryCode.QUERY_GRADE_CODE;		//gnmkdm="N121618";
    	String mainParm = "xh=" + number+"&xm="+ xm + "&gnmkdm="+QueryCode.QUERY_GRADE;		//gnmkdm="N121618";
    	
		nm.clearSpecialHeader();
		nm.addSpecialHeader("Cookie", /*HttpManager.*/cookie);
		nm.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
		nm.addSpecialHeader("Referer",referUrl);
		reply = nm.sendGet(mainUrl, mainParm);
		return reply;
	}
	
	/**
	 * ��ҳ���е���Ϣ������������Ϊjson
	 * @param reply
	 * @param option
	 * @return
	 */
	private String parseReply2Json(String reply){
    	Element main_div,select,zy_span/*רҵ��ǩ*/,xy_span/*ѧԺ*/;
		Elements options;
		List<String> data = new ArrayList<String>();
    	Matcher majorMatcher = Pattern.compile("רҵ��(.{0,30})</td>").matcher(reply),
    			collegeMatcher = Pattern.compile("ѧԺ��(.{0,30})</td>").matcher(reply);
		//main_div = Jsoup.parse(reply).select("div[id=divcxtj]").first();
    	//zy_span = main_div.select("span[id=Label7]").first();
    	//xy_span = main_div.select("span[id=Label6]").first();
//		System.out.println(reply);
    	select = Jsoup.parse(reply).select("select[name=ddlxn]").first();
    	options = select.select("option");
    	for(Element elem:options){
    		data.add(elem.val());
    	}
    	
    	JSONObject body = new JSONObject();
    	try {
			body.put("CXTJ", data);//��ѯ������ѧ�꣩
			//body.put("ZY", zy_span.text());//רҵ
			//body.put("XY", xy_span.text());//ѧԺ
			if(majorMatcher.find())
				body.put("ZY", majorMatcher.group(1));
			else
				body.put("ZY", "����רҵ��Ϣ");
			if(collegeMatcher.find())
				body.put("XY", collegeMatcher.group(1));//ѧԺ
			else
				body.put("XY", "����ѧԺ��Ϣ");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return body.toString();
	}

}
