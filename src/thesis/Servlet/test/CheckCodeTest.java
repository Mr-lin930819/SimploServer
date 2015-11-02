package thesis.Servlet.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thesis.httpMethod.HttpManager;

/**
 * Servlet implementation class CheckCodeTest
 */
@WebServlet(description = "返回验证码", urlPatterns = { "/CheckCodeTest" })
public class CheckCodeTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckCodeTest() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		byte[] img = null;
		response.setCharacterEncoding("gb2312");
		//final String loginUrlStr = "http://jwgl.fjnu.edu.cn/default2.aspx";
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
	         connection.setRequestProperty("Cookie",request.getParameter("cookie"));
	         connection.connect();
	         in = connection.getInputStream();
	         
//	         BufferedImage bi = ImageIO.read(in);
	         
 			img = new byte[in.available()];
 			in.read(img);
		}catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		response.getOutputStream().write(img);
	}

}
