package thesis.Servlets;

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

import thesis.httpMethod.NetworkManager;

/**
 * Servlet implementation class GradeOptionServlet
 */
@WebServlet(description = "获取查询成绩选项（以及学生专业信息）的Servlet", urlPatterns = { "/GradeOptionServlet" })
public class GradeOptionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GradeOptionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("gb2312");
		String resultPage = null,jsonText = null;
		resultPage = postForCondition(request.getParameter("number"),request.getParameter("xm"),
				request.getParameter("cookie"));
		
		jsonText = parseReply2Json(resultPage);
		response.getWriter().append(jsonText);
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
	
	/**
	 * 将页面中的信息解析出来保存为json
	 * @param reply
	 * @param option
	 * @return
	 */
	private String parseReply2Json(String reply){
    	Element main_div,select,zy_span/*专业标签*/,xy_span/*学院*/;
		Elements options;
		List<String> data = new ArrayList<String>();
    	main_div = Jsoup.parse(reply).select("div[id=divcxtj]").first();
    	zy_span = main_div.select("span[id=Label7]").first();
    	xy_span = main_div.select("span[id=Label6]").first();
    	select = main_div.select("select[id=ddlXN]").first();
    	options = select.select("option");
    	for(Element elem:options){
    		data.add(elem.val());
    	}
    	
    	JSONObject body = new JSONObject();
    	try {
			body.put("CXTJ", data);//查询条件（学年）
			body.put("ZY", zy_span.text());//专业
			body.put("XY", xy_span.text());//学院
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return body.toString();
	}

}
