package thesis.httpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NetworkManager {

	
	private HashMap<String,String> specialHeader;
	public Map<String,String> cookies;
	
	public NetworkManager(){
		specialHeader = new HashMap<String,String>();
		cookies = new HashMap<String,String>();
	}
	
	public String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = "";
            if(param.equals("")){
            	urlNameString = url ;
            }else{
            	urlNameString = url + "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, application/x-ms-application, application/xaml+xml, application/x-ms-xbap, */*");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
            for(Map.Entry<String, String> entry : specialHeader.entrySet()){
            	connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            // 建立实际的连接
            connection.connect();
            //if(connection.getResponseCode() != 200){
            //	return "";
            //}
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
                	if(cookieMatcher.find()){
                		//cookie = "ASP.NET_SessionId=" + cookieMatcher.group(1);
                		
                		if(!cookies.containsKey(url))
                			cookies.put(url, "ASP.NET_SessionId=" + cookieMatcher.group(1));
                	}
                }
                
            }
            System.out.println("Post Response Code-------->> "+ connection.getResponseCode());
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"GBK"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        }catch(ProtocolException e){
        	System.out.println("协议异常");
        	return null;
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
    public String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "",cookieHeader = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, application/x-ms-application, application/xaml+xml, application/x-ms-xbap, */*");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
            for(Map.Entry<String, String> entry : specialHeader.entrySet()){
            	String key = entry.getKey(), value = entry.getValue();
//            	conn.setRequestProperty(entry.getKey(), entry.getValue());
            	conn.setRequestProperty(key, value);
            	//System.out.println("SendPostPara:"+key+","+value);
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
            
            //保存响应头中设置Cookie的头数据
//            Map<String, List<String>> map = conn.getHeaderFields();
//            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//                if(key == null)
//                	continue;
//                if(key.equalsIgnoreCase("set-cookie")){
//                	Matcher cookieMatcher = Pattern.compile("ASP.NET_SessionId=(.*);").matcher(map.get(key).get(0));
//                	//System.out.println(map.get(key).get(0));
//                	if(cookieMatcher.find())
//                		cookie = "ASP.NET_SessionId=" + cookieMatcher.group(1);
//                }
//                
//            }
//            cookieHeader = conn.getHeaderField("Set-Cookie");
//            if(cookieHeader != null){
//            	Matcher cookieMatcher = Pattern.compile("ASP.NET_SessionId=(.*);").matcher(cookieHeader);
//            //System.out.println(map.get(key).get(0));
//	        	if(cookieMatcher.find())
//	        		cookie = "ASP.NET_SessionId=" + cookieMatcher.group(1);
//	        	System.out.println("POST==COOKIE:"+cookie);
//            }
        	
            // 定义BufferedReader输入流来读取URL的响应   
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"GBK"));
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
    
    public String sendPost(String url, Map<String,String> params) {
        StringBuffer param = new StringBuffer();
        for(Map.Entry<String,String> entry : params.entrySet()){
        	param.append(entry.getKey() + "=" + entry.getValue());
        	param.append('&');
        }
        param.deleteCharAt(param.length() - 1);
        String result = sendPost(url,param.toString());
        return result;
    }
    
    public void addSpecialHeader(String key,String value){
    	specialHeader.put(key, value);
    }
    
    public void clearSpecialHeader(){
    	specialHeader.clear();
    }
    
    public void removeSpecialHeader(String key){
    	specialHeader.remove(key);
    }
    

}
