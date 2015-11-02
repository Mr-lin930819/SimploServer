package thesis.logic;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import thesis.httpMethod.HttpManager;
import thesis.httpMethod.NetworkManager;

public class PreLogic {
	private static final String loginUrlStr = "http://jwgl.fjnu.edu.cn/default2.aspx";
	private static final String checkImageUrlStr = "http://jwgl.fjnu.edu.cn/CheckCode.aspx";
	public static String getOne(NetworkManager nm){
		String reply = "",viewState = "";
		Document doc = null;
    	Element form;
    	/**
    	 *  第1步，get登录页面
    	 *  返回页面：得到页面中的ViewState值
    	 */
    	nm.clearSpecialHeader();
		nm.addSpecialHeader("content-type", "application/x-www-form-urlencoded");
		//HttpManager.addSpecialHeader("Cookie", "ASP.NET_SessionId=k2koub55s0khs5anx22pmb55");
		reply = nm.sendGet(loginUrlStr, "");
    	doc = Jsoup.parse(reply);
    	//System.out.print("COOOOOOOOOOOOOOOOOOOOOOOOKIE--->>"+nm.cookies.get("http://jwgl.fjnu.edu.cn/default2.aspx"));
		form = doc.select("input[name=__VIEWSTATE]").first();
		viewState = form.attr("value");
    	viewState = viewState.replaceAll("[+]", "%2B");
//		System.out.println("__VIEWSTATE-->"+viewState);
    	return viewState;
	}
	
	public static byte[] refreshCheckCode(){
		InputStream in = null;
		 URL realUrl;
		 byte[] ret = null;
		try {
			realUrl = new URL(checkImageUrlStr);
		
	         // 打开和URL之间的连接
	         HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
	         connection.setRequestProperty("accept", "*/*");
	         connection.setRequestProperty("connection", "Keep-Alive");
	         connection.setRequestProperty("user-agent",
	                 "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	         connection.connect();
	         in = connection.getInputStream();
			ret = new byte[in.available()];
			in.read(ret);
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
		
		return ret;
	}
}
