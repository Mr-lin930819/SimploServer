package thesis.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import thesis.httpMethod.NetworkManager;

public abstract class InfoQueryTemplate {
	
	private String mNumber,mName,mCookie,mFuncId,mUrl;
	boolean mIsPost;
	private InfoQueryTemplate() {
	}
	
	public InfoQueryTemplate(String number,String name,String cookie,String funcId,String url,boolean isPost) {
		mNumber = number;
		mName	= name;
		mCookie = cookie;
		mFuncId = funcId;
		mUrl	= url;
		mIsPost = isPost;
	}
	
	public InfoQueryTemplate(String number,String name,String cookie,String funcId,String url) {
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
		//若返回空串则表示请求过程出现异常
		if(reply.equals("")){
			return handleError(reply);
		} else if(reply.equals("N12141")) {//返回"N12141"为特殊情况，进行一键评价
			return "";
		}
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
		 * 	第4步，get请求，获取 最新的ViewState以访问成绩查询页面
		 *  返回页面：包含VIEWSTATE,解析，并且将其中的“+”替换成“%2B”
		 */
		String refererUrl = "http://jwgl.fjnu.edu.cn/xs_main.aspx?xh=" + number;
		
		nm.clearSpecialHeader();
		nm.addSpecialHeader("Cookie", /*HttpManager.*/cookie);
		
		/**
		 * 此处请求暂时移到登录部分处理，获得姓名
		 */
//		reply = nm.sendGet(refererUrl, "");
//		Matcher xmMatcher = Pattern.compile("xm=(.{0,12})&gnmkdm=N121618").matcher(reply);
//		if(xmMatcher.find())
//	       	xmStr = xmMatcher.group(1);
		String newMainUrl = url + "?xh="+number+"&xm="+name+"&gnmkdm="+funcId;//gnmkdm="N121618";表示成绩查询的编号
		ArrayList<String> list = null;
		if(funcId.equals("N12141"))//教学评价
		{
			reply = nm.sendGet(refererUrl, "");
			newMainUrl = parseRef(reply);
			list = parsePJKC(reply);
		}
		
		nm.addSpecialHeader("Content-Type","application/x-www-form-urlencoded");
		nm.addSpecialHeader("Referer",refererUrl);
		reply = nm.sendGet(newMainUrl, "");
		//如果仅仅是get请求，那么可以直接返回网页内容
		if(!mIsPost)
			return reply;
		
//		Matcher newVSmatcher = Pattern.compile("__VIEWSTATE\" value=\"([^>]*)\" />").matcher(reply);
//		if(newVSmatcher.find())
//			updatedViewState = newVSmatcher.group().replaceAll("+", "%2B");
		doc = Jsoup.parse(reply);
		form = doc.select("input[name=__VIEWSTATE]").first();
		//如果寻找__VIEWSTATE出错，则表明提交的参数错误或网页的内容出现变动
		if(form == null){
			return "";
		}
		updatedViewState = form.attr("value");
		updatedViewState =  updatedViewState.replaceAll("[+]", "%2B");
		/**
		 * 第5步，post请求，设置学年等信息作为post参数，设置头信息
		 * 返回页面：成绩查询结果，可以解析获得成绩
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
		
		if(funcId.equals("N12141")){//教学评价
			String tempViewState = updatedViewState;
			for(String s:list){
				params.remove("pjkc");
				params.put("pjkc",s);
				params.put("TextBox1", "0");
				params.put("txt1", "");
				params.put("pjxx","");
				params.remove("__VIEWSTATE");
				params.put("__VIEWSTATE",tempViewState);
				params.remove("Button2");
				params.remove("Button1");
				params.put("Button1", "保  存");
				reply = nm.sendPost(newMainUrl, params);
				tempViewState = parseViewState(reply);
			}
			params.remove("Button1");
			params.remove("Button2");
			params.put("Button2", " 提  交 ");
			nm.sendPost(newMainUrl, params);
			reply = "N12141";
		} else {
			reply = nm.sendPost(newMainUrl, params);
		}
		return reply;
	}
	
	private String parseRef(String reply){
		Document nodes = Jsoup.parse(reply);
		Element ele = nodes.select("li[class=top]").get(2).select("ul[class=sub]").select("li").first();
		String refUrl = ele.select("a").attr("href");
		System.out.println("http://jwgl.fjnu.edu.cn/" + refUrl);
		return "http://jwgl.fjnu.edu.cn/" + refUrl;
	}
	
	private ArrayList<String> parsePJKC(String reply){
		Document nodes = Jsoup.parse(reply);
		Elements eles = nodes.select("li[class=top]").get(2).select("ul[class=sub]").select("li");
		ArrayList<String> kcs = new ArrayList<String>();
		for(Element e:eles){
			String ref = e.select("a").attr("href");
			Matcher matcher = Pattern.compile("xkkh=(.*)&xh").matcher(ref);
			if(matcher.find()){
				kcs.add(matcher.group(1));
			}
		}
		return kcs;
	}
	
	private String parseViewState(String reply){
		Document doc = Jsoup.parse(reply);
		Element form = doc.select("input[name=__VIEWSTATE]").first();
		//如果寻找__VIEWSTATE出错，则表明提交的参数错误或网页的内容出现变动
		if(form == null){
			return "";
		}
		String retViewState = form.attr("value");
		retViewState =  retViewState.replaceAll("[+]", "%2B");
		return retViewState;
	}
	
	protected abstract String handleError(String reply);
	
	protected abstract String parseReply(String reply);
	
	protected abstract void setSpecialParams(HashMap<String,String> params);
}
