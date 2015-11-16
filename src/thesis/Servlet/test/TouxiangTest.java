/**
 * 此Servlet在云服务器上运行存在问题，已废弃，将头像获取转到客户端直接从网站获取
 */
package thesis.Servlet.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TouxiangTest
 */
@WebServlet(description = "测试获取头像的Servlet", urlPatterns = { "/TouxiangTest" })
public class TouxiangTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TouxiangTest() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("gb2312");
		byte[] data = getForTouxiang(request.getParameter("cookie"), request.getParameter("number"));
		byte2image(data,"D:/1.jpg");
		response.getOutputStream().write(data);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
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
	         Thread.sleep(2000);
	         
 			retImg = new byte[in.available()];
 			System.out.println(in.available());
 			in.read(retImg);
		}catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
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
		return retImg;
	}
	
	public void byte2image(byte[] data,String path){
	    if(data.length<3||path.equals("")) return;
	    try{
	    FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
	    imageOutput.write(data, 0, data.length);
	    imageOutput.close();
	    System.out.println("Make Picture success,Please find image in " + path);
	    } catch(Exception ex) {
	      System.out.println("Exception: " + ex);
	      ex.printStackTrace();
	    }
	  }

}
