package com.kiuwan.customersuccess;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import org.json.JSONObject;


public class GetInsightsInfo {

	// API Insights endpoint
	private final static String KIUWAN_BASE_URL = "https://api.kiuwan.com";
	private final static String ANALYSIS_INSIGHTS_URL = "/insights/analysis/components/?analysisCode={code}&application={app}";
	
	
	private static String EncodeUserPass (String user, String pass) {
		String authString = user + ":" + pass;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
		return authStringEnc;
	}

	private static String RestApiCall(String userNameKiuwan, String passwordKiuwan, String url, ArrayList <String[]> parameters) {
		// Generic call to Kiuwan Rest-Api
		HttpClient httpclient = HttpClientBuilder.create().build();
		RequestConfig params = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();

		URIBuilder builder = null;
		try {
			builder = new URIBuilder(url);
		} catch (URISyntaxException e1) {
			System.out.println("URISyntaxException:");
			e1.printStackTrace();
		}
		for (String[] parameter : parameters) {
			builder.setParameter(parameter[0], parameter[1]);
		}

		HttpGet getCall = null;
		try {
			getCall = new HttpGet(builder.build());
		} catch (URISyntaxException e1) {
			System.out.println("URISyntaxException:");
			e1.printStackTrace();
		}
		getCall.setConfig(params);
		String authStringEnc = EncodeUserPass(userNameKiuwan, passwordKiuwan);
		getCall.addHeader("Authorization", "Basic " + authStringEnc);
		getCall.addHeader("Content-Type", "application/json");

		HttpResponse response = null;
		try {
			response = httpclient.execute(getCall);
		} catch (ClientProtocolException e) {
			System.out.println("ClientProtocolException:");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException:");
			e.printStackTrace();
		}

		StatusLine status = response.getStatusLine();
		if (status.getStatusCode() == 200) {
			System.out.println("Login and get OK");
		} else {
			System.out.println("Login and get NOK: Exiting");
			System.out.println(status);
			return null;
		}	

		String json_response = "";
		try {
			json_response = EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			System.out.println("ParseException:");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException:");
			e.printStackTrace();
		}

		return json_response;
	}

	public static void main(String[] args) throws ClientProtocolException, IOException {

		if (args.length != 2) {
			System.out.println("Program must have 2 arguments: <user> <password>");
			return;
		}

		ArrayList <String[]> parametersCall = new ArrayList <String[]>();
		String url = "";
		String json_apps_response = "";
		JSONObject jsonobj = null;
		
		Scanner sc = new Scanner(System.in);
		
		String usernameKiuwan = args[0];
		String passwordKiuwan = args[1];

		url = KIUWAN_BASE_URL + ANALYSIS_INSIGHTS_URL;
		System.out.println("\nPlease, enter the analysis code: " + url);
		String analysis_code = sc.next();
		url = url.replace("{code}", analysis_code);
		System.out.println("\nPlease, enter the appName: " + url);
		String appName = sc.next();
		url = url.replace("{app}", appName);
		System.out.println("\nURL: " + url);
		
		json_apps_response = RestApiCall(usernameKiuwan, passwordKiuwan, url, parametersCall);
		jsonobj = new JSONObject(json_apps_response);
		System.out.println(jsonobj.toString());

		sc.close();
		
		System.out.println("===============END===============");
	}
}

