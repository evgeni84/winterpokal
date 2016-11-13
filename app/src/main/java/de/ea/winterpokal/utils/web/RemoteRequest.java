package de.ea.winterpokal.utils.web;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.util.Log;

import de.ea.winterpokal.utils.web.auth.Auth;

public class RemoteRequest {

	/**
	 * Host name verifier that does not perform nay checks.
	 */
	private static class NullHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};

			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static {
		trustAllHosts();
	}

	public static String DoRequest(String urlString, HashMap<String, String> args, RequestType type) {
		return DoRequest(urlString, args, null, type);
	}

	public static String DoRequest(String urlString, RequestType type) {
		return DoRequest(urlString, null, null, type);
	}

	public static String DoRequest(String urlString, String json, RequestType type) {
		return DoRequest(urlString, null, json, type);
	}

	public static String DoRequest(String urlString, HashMap<String, String> args, String json, RequestType type) {
		HttpsURLConnection connection = null;
		BufferedReader rd = null;
		StringBuilder sb = null;
		String line = null;

		URL serverUrl = null;

		String urlParams = "";
		if (args != null && args.size() > 0) {
			for (Entry<String, String> o : args.entrySet()) {
				try {
					urlParams += URLEncoder.encode(o.getKey(), "UTF-8") + "=" + URLEncoder.encode(o.getValue(), "UTF-8") + "&";
				} catch (UnsupportedEncodingException e) {
					Log.e("RemoteRequestURLEncoder", e.getMessage(), e);
					return null;
				}
			}
			if (urlParams.endsWith("&"))
				urlParams = urlParams.substring(0, urlParams.length() - 1);

		}

		try {

			if (type.equals(RequestType.GET) && urlParams != null && urlParams.length() > 0) {
				urlString += "?" + urlParams;
			}
			serverUrl = new URL(urlString);

			connection = (HttpsURLConnection) serverUrl.openConnection();
			connection.setHostnameVerifier(new NullHostnameVerifier());
			connection.setRequestMethod(type.getRequestType());
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setReadTimeout(10000);
			if (Auth.getToken() != null)
				connection.addRequestProperty("api-token", Auth.getToken().getToken());

			if (type.equals(RequestType.POST) && (urlParams != null && urlParams.length() > 0 || json != null)) {

				String data = json != null ? json : (urlParams != null && urlParams.length() > 0 ? urlParams : "");

				// connection.setRequestProperty("Content-Type",
				// "application/x-www-form-urlencoded");
				connection.setRequestProperty("charset", "utf-8");
				// connection.setRequestProperty("Content-Length","" +
				// Integer.toString(data.getBytes().length));

				if (json != null) {
					// connection.setRequestProperty("Content-Type",
					// "application/json");
					// connection.setRequestProperty("Accept",
					// "application/json");
				}
				// connection.setUseCaches(false);

				DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
				dos.write(data.getBytes("UTF-8"));
				dos.flush();
				dos.close();
			}
			connection.connect();

			int responseCode = connection.getResponseCode();

			InputStream stream = null;
			if (responseCode > 200) {
				stream = connection.getErrorStream();
			} else
				stream = connection.getInputStream();
			// read the result from the server
			rd = new BufferedReader(new InputStreamReader(stream));
			sb = new StringBuilder();

			while ((line = rd.readLine()) != null) {
				sb.append(line + '\n');
			}

			String responseString = sb.toString();
			String decoded = responseString;
			try {
				decoded = URLDecoder.decode(responseString);
			} catch (Exception ex) {
				Log.e("error parsing json", responseString, ex);
			}
			return decoded;
			// return Html.fromHtml(decoded).toString();

		} catch (MalformedURLException e) {
			Log.e("RemoteRequest-malformedurl", e.getMessage(), e);
		} catch (ProtocolException e) {
			Log.e("RemoteRequest-protocol", e.getMessage(), e);
		} catch (IOException e) {
			// e.printStackTrace();
			Log.e("RemoteRequest", e.getMessage(), e);
		} catch (Exception e) {
		} finally {
			// close the connection, set all objects to null
			connection.disconnect();
			rd = null;
			sb = null;
			connection = null;
		}
		return null;
	}
}
