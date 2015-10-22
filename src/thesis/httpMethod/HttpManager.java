package thesis.httpMethod;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;


public class HttpManager {

	static{
		specialHeader = new HashMap<String,String>();
	}
	private static HashMap<String,String> specialHeader;
	public static String cookie;
	
	public static byte[] getCheckcodeToSave(String url){
		InputStream in = null;
		 URL realUrl;
		 byte[] ret = null;
		try {
			realUrl = new URL(url);
		
	         // 打开和URL之间的连接
	         HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
	         connection.setRequestProperty("accept", "*/*");
	         connection.setRequestProperty("connection", "Keep-Alive");
	         connection.setRequestProperty("user-agent",
	                 "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	         connection.connect();
	         in = connection.getInputStream();
	         
	         BufferedImage bi = ImageIO.read(in);
	         File f = new File( "D:/workspace/SimploServer/WebContent/check_img.gif");
	         if(f.exists()){
	        	 System.out.println("NULL");
	         }
	         ImageIO.write(bi, "gif", f);
	         
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
	
	public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            for(Map.Entry<String, String> entry : specialHeader.entrySet()){
            	connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            // 建立实际的连接
            connection.connect();
            if(connection.getResponseCode() != 200){
            	return "";
            }
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
                if(key == null)
                	continue;
                if(key.equalsIgnoreCase("set-cookie")){
                	Matcher cookieMatcher = Pattern.compile("ASP.NET_SessionId=(.*);").matcher(map.get(key).get(0));
                	//System.out.println(map.get(key).get(0));
                	if(cookieMatcher.find())
                		cookie = "ASP.NET_SessionId=" + cookieMatcher.group(1);
                }
                
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
	
	 /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            //conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            for(Map.Entry<String, String> entry : specialHeader.entrySet()){
            	conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    public static String sendPost(String url, Map<String,String> params) {
        StringBuffer param = new StringBuffer();
        for(Map.Entry<String,String> entry : params.entrySet()){
        	param.append(entry.getKey() + "=" + entry.getValue());
        	param.append('&');
        }
        param.deleteCharAt(param.length() - 1);
        String result = sendPost(url,param.toString());
        return result;
    }
    
    public static void addSpecialHeader(String key,String value){
    	specialHeader.put(key, value);
    }
    
    public static void clearSpecialHeader(){
    	specialHeader.clear();
    }
    

}
