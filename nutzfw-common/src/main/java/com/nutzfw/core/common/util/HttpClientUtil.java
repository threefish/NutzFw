package com.nutzfw.core.common.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date 2017/5/1616:35
 */
public class HttpClientUtil {

    private static final String USER_AGENT = "Mozilla/5.0";

    private static final Logger log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    public static Response post(String postUrl, String charSet, List<NameValuePair> urlParameters) {

        Response res = new Response();
        res.setOk(false);
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(postUrl);
        httppost.setHeader("User-Agent", USER_AGENT);
        httppost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httppost.setHeader("Accept-Encoding", "gzip, deflate");
        try {
            httppost.setEntity(new UrlEncodedFormEntity(urlParameters, charSet));
            HttpResponse response = httpclient.execute(httppost);

            res.setStatusCode(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                res.setContent(IOUtils.toString(response.getEntity().getContent()));
                res.setOk(true);
            } else {
                String error = response.getStatusLine().getStatusCode() + "错误！" + IOUtils.toString(response.getEntity().getContent());
                log.error(error);
                res.setErrormsg(error);
            }
        } catch (Exception e) {
            log.error("错误", e);
            res.setErrormsg(e.getMessage() + "");
        }
        return res;
    }

    public static Response get(String getUrl) {
        Response res = new Response();
        res.setOk(false);
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(getUrl);
        httpGet.setHeader("User-Agent", USER_AGENT);
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
        try {
            HttpResponse response = httpclient.execute(httpGet);
            res.setStatusCode(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                res.setContent(IOUtils.toString(response.getEntity().getContent()));
                res.setOk(true);
            } else {
                String error = response.getStatusLine().getStatusCode() + "错误！" + IOUtils.toString(response.getEntity().getContent());
                log.error(error);
                res.setErrormsg(error);
            }
        } catch (Exception e) {
            log.error("错误", e);
            res.setErrormsg(e.getMessage() + "");
        }
        return res;
    }

    public static class Response {
        int statusCode;
        String content;
        boolean ok;
        String errormsg;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isOk() {
            return ok;
        }

        public void setOk(boolean ok) {
            this.ok = ok;
        }

        public String getErrormsg() {
            return errormsg;
        }

        public void setErrormsg(String errormsg) {
            this.errormsg = errormsg;
        }
    }
}
