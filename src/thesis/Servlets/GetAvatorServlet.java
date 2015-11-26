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
 * Servlet implementation class GetAvatorServlet
 */
@WebServlet(description = "获取头像图片的Servlet", urlPatterns = { "/GetAvatorServlet" })
public class GetAvatorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAvatorServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("gb2312");
		byte[] data = getForTouxiang(request.getParameter("cookie"), request.getParameter("number"));
		response.getOutputStream().write(data);
	}
	
	private byte[] getForTouxiang(String cookie,String number){
		byte[] retImg = null;
		InputStream in = null;
		URL realUrl;
		try {
			realUrl = new URL("http://jwgl.fjnu.edu.cn/readimagexs.aspx?xh=" + number);
		
	         // 打开和URL之间的连接
	         HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
	         connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
	         connection.setRequestProperty("Connection", "Keep-Alive");
	         connection.setRequestProperty("User-Agent",
	        		 "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
	         connection.setRequestProperty("Cookie",cookie);
	         connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
	         connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
	         connection.connect();
	         in = connection.getInputStream();
	         //休眠以获得完整的图片，因为网址采取分块传输，若不休眠1秒会造成读取不完整
	         Thread.sleep(2000);
	         
 			retImg = new byte[in.available()];
 			System.out.println(in.available());
 			in.read(retImg);
		}catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
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
