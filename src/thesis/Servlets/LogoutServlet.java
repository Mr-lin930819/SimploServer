package thesis.Servlets;

import org.hibernate.Session;
import thesis.DBOperation.HBUtil;
import thesis.JavaBean.UserInfoEntity;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Lin on 2016/1/29.
 */
@WebServlet(name = "LogoutServlet", urlPatterns = "/Logout")
public class LogoutServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doLogout(request.getParameter("openAppId"));
        //doLogout("56756565");
    }

    private void doLogout(String id){
        Session session = HBUtil.getSession();
        session.beginTransaction();
        UserInfoEntity userInfo = (UserInfoEntity)session.get(UserInfoEntity.class, id);
        session.delete(userInfo);
        session.getTransaction().commit();
        session.close();
    }
}
