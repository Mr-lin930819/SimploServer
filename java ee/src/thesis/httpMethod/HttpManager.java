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
		cookies = new HashMap<String,String>();
	}
	private static HashMap<String,String> specialHeader;
	public static Map<String,String> cookies;
	
	public static byte[] getCheckcodeToSave(String url){
		InputStream in = null;
		 URL realUrl;
		 byte[] ret = null;
		try {
			realUrl = new URL(url);
		
	         // �򿪺�URL֮�������
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
		
		return ret;
	}
	
	public static String sendGet(String url, String param) {
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
            // �򿪺�URL֮�������
            HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
            // ����ͨ�õ���������
            connection.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, application/x-ms-application, application/xaml+xml, application/x-ms-xbap, */*");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
            for(Map.Entry<String, String> entry : specialHeader.entrySet()){
            	connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            // ����ʵ�ʵ�����
            connection.connect();
            //if(connection.getResponseCode() != 200){
            //	return "";
            //}
            // ��ȡ������Ӧͷ�ֶ�
            Map<String, List<String>> map = connection.getHeaderFields();
            // �������е���Ӧͷ�ֶ�
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
                if(key == null)
                	continue;
                if(key.equalsIgnoreCase("set-cookie")){
                	Matcher cookieMatcher = Pattern.compile("ASP.NET_SessionId=(.*);").matcher(map.get(key).get(0));
                	//System.out.println(map.get(key).get(0));
                	if(cookieMatcher.find()){
                		//cookie = "ASP.NET_SessionId=" + cookieMatcher.group(1);
                		cookies.put(url, "ASP.NET_SessionId=" + cookieMatcher.group(1));
                	}
                }
                
            }
            // ���� BufferedReader����������ȡURL����Ӧ
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("����GET��������쳣��" + e);
            e.printStackTrace();
        }
        // ʹ��finally�����ر�������
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
     * ��ָ�� URL ����POST����������
     * 
     * @param url
     *            ��������� URL
     * @param param
     *            ����������������Ӧ���� name1=value1&name2=value2 ����ʽ��
     * @return ������Զ����Դ����Ӧ���
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "",cookieHeader = "";
        try {
            URL realUrl = new URL(url);
            // �򿪺�URL֮�������
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // ����ͨ�õ���������
            conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, application/x-ms-application, application/xaml+xml, application/x-ms-xbap, */*");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
            for(Map.Entry<String, String> entry : specialHeader.entrySet()){
            	String key = entry.getKey(), value = entry.getValue();
//            	conn.setRequestProperty(entry.getKey(), entry.getValue());
            	conn.setRequestProperty(key, value);
            	System.out.println("SendPostPara:"+key+","+value);
            }
            // ����POST�������������������
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // ��ȡURLConnection�����Ӧ�������
            out = new PrintWriter(conn.getOutputStream());
            // �����������
            out.print(param);
            // flush������Ļ���
            out.flush();
            
            //������Ӧͷ������Cookie��ͷ����
//            Map<String, List<String>> map = conn.getHeaderFields();
//            // �������е���Ӧͷ�ֶ�
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
        	
            // ����BufferedReader����������ȡURL����Ӧ            
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("���� POST ��������쳣��"+e);
            e.printStackTrace();
        }
        //ʹ��finally�����ر��������������
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