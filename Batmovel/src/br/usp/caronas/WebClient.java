package br.usp.caronas;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/* Modificado do gist 
 *       https://gist.github.com/SuelenGC/10788937
 * compartilhado por Suelen Carvalho */

public class WebClient {
	private final String url;

	public WebClient(String url) {
		this.url = url;
	}

	public String postHttps(String user, String password) {
		String res = "";
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		X509TrustManager naiveTrustManager = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[]{};
			}
		};

		params.add(new BasicNameValuePair("usp_id", user));
		params.add(new BasicNameValuePair("password", password));

		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[]{ naiveTrustManager }, null);

			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});

			HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());

			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(this.url);

			request.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse response = client.execute(request);

			res = EntityUtils.toString(response.getEntity());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;

	}

	public boolean postJson(String json) {
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) (new URL(url)).openConnection();

			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/json");
			OutputStream stream = con.getOutputStream();
			OutputStreamWriter wr= new OutputStreamWriter(stream);
			wr.write(json);
			wr.flush();
			wr.close();

			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//TODO remover
			System.err.println(response.toString());

			con.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private String convertStreamToString(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();

		return sb.toString();
	}
	
	public JSONObject getJson(){
		
		try {
			URL url = new URL(this.url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			InputStream stream = conn.getInputStream();
			String response = convertStreamToString(stream);
			stream.close();
			JSONObject jsonResponse = new JSONObject(response);
			return jsonResponse;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}catch (IOException e){
			e.printStackTrace();
			return null;
		} catch (JSONException e){
			e.printStackTrace();
			return null;
		}
	}
}