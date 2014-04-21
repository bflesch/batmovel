package com.example.batmovel;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
 
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
 
    X509TrustManager naiveTrustManager = new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    };
 
    public String postHttps(String user, String password) {
        String res = "";
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
 
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
}