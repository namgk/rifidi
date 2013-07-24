package kr.ac.kaist.resl.stis.listener.coaplistener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 * 
 */
public class UrlMatcherTest {

	public UrlMatcherTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String urlToMatch = "/30744B5A1C0000100000000A/element1/append";
		String urlToTest = "/mail.google.com/mail/u/0/#inbox";
		
		String patternStr = "^/[A-Fa-f0-9]{1,30}/element1/append$";
		
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(urlToMatch);
		while (matcher.find()) {
			System.out.print("Start index: " + matcher.start());
			System.out.print(" End index: " + matcher.end() + " ");
			System.out.println(matcher.group());
		}

	}

}
