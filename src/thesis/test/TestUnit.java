package thesis.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestUnit {
	
	private static String str = "<form name=\"form1\" method=\"post\" action=\"default2.aspx\" id=\"form1\">"+
			"<input type=\"hidden\" name=\"__VIEWSTATE\" value=\"dDwyODE2NTM0OTg7Oz79m7/udPyyThgiewzzwnr1AnohgA==\" />";
	
	public static void regExpTest(){
		String ret = "";
		Pattern pattern = Pattern.compile("__VIEWSTATE\" value=\"([^>]*)\" />");
    	Matcher matcher = pattern.matcher(str);
    	if(matcher.find()){
    		ret = matcher.group(1);	
    	}else{
    		ret = "No Match!";
    	}
    	System.out.println(ret);
	}
}
