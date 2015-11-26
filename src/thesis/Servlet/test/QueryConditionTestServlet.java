package thesis.Servlet.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 * Servlet implementation class QueryConditionTestServlet
 */
@WebServlet("/QueryConditionTestServlet")
public class QueryConditionTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryConditionTestServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("gb2312");
		String resultPage = null,jsonText = null;


		resultPage = postForCondition(request.getParameter("number"),request.getParameter("xm"),
				request.getParameter("cookie"));
		
		jsonText = parseReply2Json(resultPage);
		System.out.print(jsonText);
		response.getWriter().append(jsonText);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private String postForCondition(String number,String xm,String cookie){
		NetworkManager nm = new NetworkManager();
    	String reply;
    	
    	String referUrl = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=" + number;
    	String mainUrl = "http://jwgl.fjnu.edu.cn/xscj_gc.aspx?xh="+number+"&xm="+xm+"&gnmkdm="+"N121618";//gnmkdm="N121618";
    	
		nm.clearSpecialHeader();
		nm.addSpecialHeader("Cookie", /*HttpManager.*/cookie);
		nm.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
		nm.addSpecialHeader("Referer",referUrl);
		reply = nm.sendGet(mainUrl, "");
		return reply;
	}
	
	private String parseReply2Json(String reply){
		Document doc = null;
    	Element select;
		Elements options;
		List<String> data = new ArrayList<String>();
    	doc = Jsoup.parse(reply);
    	select = doc.select("select[id=ddlXN]").first();
    	options = select.select("option");
    	for(Element elem:options){
    		data.add(elem.val());
    	}
    	
    	JSONObject body = new JSONObject();
    	JSONArray jsonData = new JSONArray(data);
    	try {
			body.put("CXTJ", data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return body.toString();
	}
	

}
