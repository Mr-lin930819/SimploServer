package thesis.Servlets;

import thesis.CommonInfo.RequestKey;
import thesis.DBOperation.HBEntityUtil;
import thesis.JavaBean.UserInfoEntity;
import thesis.httpMethod.NetworkManager;
import thesis.logic.JsonTool;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mrlin on 2016/2/28.
 */
@WebServlet(urlPatterns = "/SessionVerifyServlet")
public class SessionVerifyServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HashMap<String, String> rspMapData = new HashMap<String, String>();
        response.setCharacterEncoding("gb2312");
        String rstText = verifySession(request.getParameter(RequestKey.OPEN_ID));
        rspMapData.put("result", rstText);
        response.getWriter().append(JsonTool.convMap2Json(rspMapData,"verifyRst").toString());
    }

    private String verifySession(String openId) {
        NetworkManager nm = new NetworkManager();
        String stuMainPage;
        Matcher xmMatcher;
        UserInfoEntity userInfo = HBEntityUtil.getUserInfo(openId);
        nm.clearSpecialHeader();
        nm.addSpecialHeader("Accept", "image/gif, image/jpeg, image/pjpeg, application/x-ms-application, application/xaml+xml, application/x-ms-xbap, */*");
        nm.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
        nm.addSpecialHeader("Cache-Control","max-age=0");
        nm.addSpecialHeader("Host", "jwgl.fjnu.edu.cn");
        nm.addSpecialHeader("Referer","http://jwgl.fjnu.edu.cn/");
        nm.addSpecialHeader("Cookie", userInfo.getStoredCookie());
        nm.addSpecialHeader("Accept-Encoding","gzip, deflate");
        nm.addSpecialHeader("Accept-Language","zh-CN,en,*");
        stuMainPage = nm.sendGet("http://jwgl.fjnu.edu.cn/xs_main.aspx?xh="+ userInfo.getStuNumber(), "");
        xmMatcher = Pattern.compile("<span id=\"xhxm\">(.{0,12})同学</span>").matcher(stuMainPage);
        if(xmMatcher.find()){
            return "SUCCE";     //成功
        }else{
            return "ERREP";     //session过期
        }
    }
}
