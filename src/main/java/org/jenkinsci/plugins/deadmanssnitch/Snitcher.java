package org.jenkinsci.plugins.deadmanssnitch;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Collections;

public class Snitcher {
    public static void snitch(String token, String message) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet request = new HttpGet(getUrl(token, message));
        request.addHeader("User-Agent", "Jenkins Dead Man's Snitch Notifier Plugin");

        CloseableHttpResponse response = httpclient.execute(request);
        int status = response.getStatusLine().getStatusCode();
        if(status > 300) {
            HttpEntity entity = response.getEntity();
            String body = entity != null ? EntityUtils.toString(entity) : null;
            throw new IOException(status + " - " + body);
        }
    }

    private static String getUrl(String token, String message) {
        String url = "https://nosnch.in/" + token;
        if(message != null && !"".equals(message)) {
            url += "?" + URLEncodedUtils.format(Collections.singletonList(new BasicNameValuePair("m", message)), "UTF-8");
        }
        return url;
    }
}
