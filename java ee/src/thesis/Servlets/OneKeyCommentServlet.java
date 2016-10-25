package thesis.Servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thesis.CommonInfo.RequestKey;
import thesis.DBOperation.HBEntityUtil;
import thesis.JavaBean.UserInfoEntity;
import thesis.logic.InfoQueryTemplate;

/**
 * Servlet implementation class OneKeyCommentServlet
 */
@WebServlet(description = "һ����ѧ����", urlPatterns = { "/OneKeyCommentServlet" })
public class OneKeyCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OneKeyCommentServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("utf-8");
		UserInfoEntity userInfo = HBEntityUtil.getUserInfo(request.getParameter(RequestKey.OPEN_ID));
		String result = new DoComment(userInfo.getStuNumber(), userInfo.getStuName(), userInfo.getStoredCookie())
				.doQuery();
//		String result = new DoComment(request.getParameter("number"), request.getParameter("name"),
//				request.getParameter("cookie")).doQuery();
		response.getWriter().append(result);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	class DoComment extends InfoQueryTemplate{

		public DoComment(String number, String name, String cookie) {
			super(number, name, cookie, "N12141", "http://jwgl.fjnu.edu.cn/xsjxpj.aspx?xkkh=(2015-2016-1)-1000060208-205090-1");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected String handleError(String reply) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected String parseReply(String reply) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void setSpecialParams(HashMap<String, String> params) {
			// TODO Auto-generated method stub
			params.put("__EVENTTARGET", "");
			params.put("__EVENTARGUMENT", "");
			params.put("DataGrid1:_ctl2:JS1", "�ǳ�����");
			params.put("DataGrid1:_ctl3:JS1", "�ǳ�����");
			params.put("DataGrid1:_ctl4:JS1", "�ǳ�����");
			params.put("DataGrid1:_ctl5:JS1", "�ǳ�����");
			params.put("DataGrid1:_ctl6:JS1", "�ǳ�����");
			params.put("DataGrid1:_ctl7:JS1", "�ǳ�����");
			params.put("DataGrid1:_ctl8:JS1", "�ǳ�����");
			params.put("DataGrid1:_ctl9:JS1", "�ǳ�����");
			params.put("DataGrid1:_ctl10:JS1", "�ǳ�����");
			params.put("DataGrid1:_ctl11:JS1", "�ǳ�����");
		}
		
	}

}
