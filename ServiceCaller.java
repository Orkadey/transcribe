package transcribe;

import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.commons.io.IOUtils;

final class ServiceCaller {

	static final String listFirst = "<div id=\"transcr_output\">";
	static final String listLast = "</div>";
	static final String wordFirst = "<span class=\"transcribed_word\">";
	static final String wordLast = "</span><br />";
	static final String delimiter = "</span>";

	static List<String> getTranscription(List<String> wordList) throws SocketTimeoutException, Exception {

		URL url = new URL("http://lingorado.com/ipa/");
		List<String> transcription = new ArrayList<String>();
		Map<String, Object> params = setParameters(wordList);
		byte[] postDataBytes = paramsToString(params).getBytes("UTF-8");
		HttpURLConnection conn = null;
		InputStream is = null;

		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(2000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);
			
			is = conn.getInputStream();
			//TODO: remove
			OutputStream out = new FileOutputStream("d:/html.txt");
			IOUtils.copy(is, out);
			transcription = parseHTML(is);

		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		
		return transcription;
	}

	private static Map<String, Object> setParameters(List<String> wordList) {

		Map<String, Object> params = new LinkedHashMap<>();

		params.put("text_to_transcribe", listToString(wordList));
		params.put("submit", "Show+transcription");
		params.put("output_dialect", "am");
		params.put("output_style", "only_tr");
		params.put("preBracket", "");
		params.put("postBracket", "");
		params.put("speech_support", 0);

		return params;
	}

	private static String paramsToString(Map<String, Object> params) {
		StringBuilder postData = new StringBuilder();
		for (Map.Entry<String, Object> param : params.entrySet()) {
			if (postData.length() != 0)
				postData.append('&');
			try {
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(postData);
		return postData.toString();

	}

	private static List<String> parseHTML(InputStream in) {

		List<String> transcription = new ArrayList<String>();
		String page;
		try {
			page = IOUtils.toString(in, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		// TODO:check indexOf thoroughly, test different values
		int listF = page.indexOf(listFirst) + listFirst.length();
		int listL = page.indexOf(listLast, listF);
		int wordF = page.indexOf(wordFirst, listF) + wordFirst.length();
		int wordL = page.indexOf(wordLast, wordF);
		int delim = page.indexOf(delimiter, wordF);
		String word = "";
		
		while (wordF < listL) {
			if (delim == wordL)
				word = page.substring(wordF, wordL);
			else
				while (delim <= wordL) {
					word += page.substring(wordF, delim);
					wordF = page.indexOf(wordFirst, delim) + wordFirst.length();
					if (delim >= wordL)
						break;
					word += page.substring(delim + delimiter.length(), wordF - wordFirst.length());
					delim = page.indexOf(delimiter, wordF);
				}
			transcription.add(word.toString());
			wordF = page.indexOf(wordFirst, wordL) + wordFirst.length();
			delim = page.indexOf(delimiter, wordF);
			if (wordF < wordFirst.length())
				break;
			wordL = page.indexOf(wordLast, wordF);
			word = "";
		}

		return transcription;
	}

	private static String listToString(List<String> list) {
		String listString = "";
		for (String s : list)
			listString += s + "\n";

		return listString;
	}
}
