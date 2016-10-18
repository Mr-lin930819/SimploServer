package thesis.Servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CheckImgServlet
 */
@WebServlet(description = "获取 验证码的Servlet", urlPatterns = { "/CheckImgServlet" })
public class CheckImgServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckImgServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("gb2312");
//		System.out.println("Cookie Is <-->" + request.getParameter("cookie"));
		byte [] responseData = getCheckImg(request.getParameter("cookie"));
		response.getOutputStream().write(responseData);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private byte[] getCheckImg(String cookie){
		byte[] retImg = null;
		final String imgUrlStr = "http://jwgl.fjnu.edu.cn/CheckCode.aspx";
		InputStream in = null;
		 URL realUrl;
		try {
			realUrl = new URL(imgUrlStr);
		
	         // 打开和URL之间的连接
	         HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
	         connection.setRequestProperty("Accept", "*/*");
	         connection.setRequestProperty("Connection", "Keep-Alive");
	         connection.setRequestProperty("User-Agent",
	        		 "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
	         connection.setRequestProperty("Cookie",cookie);
	         connection.connect();
	         in = connection.getInputStream();
	         
//	         BufferedImage bi = ImageIO.read(in);
	         
 			retImg = new byte[in.available()];
 			in.read(retImg);
		}catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
		return retImg;
	}

}
