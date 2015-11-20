package thesis.logic;

import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import thesis.httpMethod.NetworkManager;

public abstract class InfoQueryTemplate {
	
	private String mNumber,mName,mCookie,mFuncId,mUrl;
	boolean mIsPost;
	private InfoQueryTemplate() {
	}
	
	public InfoQueryTemplate(String number,String name,String cookie,String funcId,String url,boolean isPost) {
		// TODO Auto-generated constructor stub
		mNumber = number;
		mName	= name;
		mCookie = cookie;
		mFuncId = funcId;
		mUrl	= url;
		mIsPost = isPost;
	}
	
	public InfoQueryTemplate(String number,String name,String cookie,String funcId,String url) {
		// TODO Auto-generated constructor stub
		mNumber = number;
		mName	= name;
		mCookie = cookie;
		mFuncId = funcId;
		mUrl	= url;
		mIsPost = true;
	}
	
	public String doQuery(){
		String reply = getReply(mNumber, mName, mCookie, mFuncId, mUrl);
		//System.out.println(reply);
		return parseReply(reply);
	}
	
	protected final String getReply(String number,String name,String cookie,String funcId,String url){
		NetworkManager nm = new NetworkManager();
    	HashMap<String,String> params = new HashMap<String,String>();
    	String reply = "";
    	String updatedViewState = "";
    	Document doc = null;
    	Element form;
		/**
		 * 	��4����get���󣬻�ȡ ���µ�ViewState�Է��ʳɼ���ѯҳ��
		 *  ����ҳ�棺����VIEWSTATE,���������ҽ����еġ�+���滻�ɡ�%2B��
		 */
		String refererUrl = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=" + number;
		
		nm.clearSpecialHeader();
		nm.addSpecialHeader("Cookie", /*HttpManager.*/cookie);
		
		/**
		 * �˴�������ʱ�Ƶ���¼���ִ����������
		 */
//		reply = nm.sendGet(refererUrl, "");
//		Matcher xmMatcher = Pattern.compile("xm=(.{0,12})&gnmkdm=N121618").matcher(reply);
//		if(xmMatcher.find())
//	       	xmStr = xmMatcher.group(1);
		String newMainUrl = url + "?xh="+number+"&xm="+name+"&gnmkdm="+funcId;//gnmkdm="N121618";��ʾ�ɼ���ѯ�ı��
		
		nm.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
		nm.addSpecialHeader("Referer",refererUrl);
		reply = nm.sendGet(newMainUrl, "");
		//���������get������ô����ֱ�ӷ�����ҳ����
		if(!mIsPost)
			return reply;
		
//		Matcher newVSmatcher = Pattern.compile("__VIEWSTATE\" value=\"([^>]*)\" />").matcher(reply);
//		if(newVSmatcher.find())
//			updatedViewState = newVSmatcher.group().replaceAll("+", "%2B");
		doc = Jsoup.parse(reply);
		form = doc.select("input[name=__VIEWSTATE]").first();
		updatedViewState = form.attr("value");
		updatedViewState =  updatedViewState.replaceAll("[+]", "%2B");
		/**
		 * ��5����post��������ѧ�����Ϣ��Ϊpost����������ͷ��Ϣ
		 * ����ҳ�棺�ɼ���ѯ��������Խ�����óɼ�
		 */
		params.clear();
		params.put("__VIEWSTATE",updatedViewState);
		setSpecialParams(params);
		
//		refererUrl = "http://jwgl.fjnu.edu.cn/xscj_gc.aspx?xh=" + 
//				user.getNumber() + "&xm="+xmStr+"&gnmkdm="+"N121618";
		nm.clearSpecialHeader();
		nm.addSpecialHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		nm.addSpecialHeader("Accept-Encoding","gzip,deflate");
		nm.addSpecialHeader("Accept-Language","zh-CN");
		nm.addSpecialHeader("Cache-Control","no-cache");
		nm.addSpecialHeader("Connection","Keep-Alive");
		//nm.addSpecialHeader("Content-Length","4413");
		nm.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
		nm.addSpecialHeader("Host","jwgl.fjnu.edu.cn");
		nm.addSpecialHeader("Referer",newMainUrl);
		nm.addSpecialHeader("User-Agent","Mozilla/5.0");
		nm.addSpecialHeader("Cookie", cookie);
		
		reply = nm.sendPost(newMainUrl, params);
		return reply;
		
	}
	
	protected abstract String parseReply(String reply);
	
	protected abstract void setSpecialParams(HashMap<String,String> params);
}
