package thesis.Servlets;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import thesis.CommonInfo.QueryCode;
import thesis.CommonInfo.QueryUrl;
import thesis.CommonInfo.RequestKey;
import thesis.DBOperation.HBEntityUtil;
import thesis.JavaBean.UserInfoEntity;
import thesis.httpMethod.NetworkManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CourseOptionServlet
 */
@WebServlet(description = "�α��ѯѡ��", urlPatterns = { "/CourseOptionServlet" })
public class CourseOptionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CourseOptionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
        UserInfoEntity user = HBEntityUtil.getUserInfo(request.getParameter(RequestKey.OPEN_ID));
//        String jsonData = parse2Json(
//                post4Option(request.getParameter("number"), request.getParameter("xm"), request.getParameter("cookie")));
        String jsonData = parse2Json(post4Option(
                user.getStuNumber(), user.getStuName(), user.getStoredCookie()));
        response.getWriter().append(jsonData);
	}

    private String post4Option(String number,String xm,String cookie){
        NetworkManager nm = new NetworkManager();
        String reply;
        String referUrl = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=" + number;
        String mainParm = "xh=" + number+"&xm="+ xm + "&gnmkdm="+ QueryCode.QUERY_COURSE;

        nm.clearSpecialHeader();
        nm.addSpecialHeader("Cookie", cookie);
        nm.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
        nm.addSpecialHeader("Referer",referUrl);
        reply = nm.sendGet(QueryUrl.COURSE_QUERY, mainParm);
        return reply;
    }

    private String parse2Json(String reply){
        Element select;
        Elements options;
        List<String> data = new ArrayList<String>();
        select = Jsoup.parse(reply).select("select[name=xnd]").first();
        options = select.select("option");
        for(Element elem:options){
            data.add(elem.val());
        }

        JSONObject body = new JSONObject();
        try {
            body.put("CXTJ", data);//��ѯ������ѧ�꣩
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body.toString();
    }

}
