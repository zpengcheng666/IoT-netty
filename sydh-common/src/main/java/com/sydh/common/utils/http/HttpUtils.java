package com.sydh.common.utils.http;

import com.sydh.common.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpUtils {
    private static final Logger bV = LoggerFactory.getLogger(HttpUtils.class);


    public static String sendGet(String url) {
        return sendGet(url, "");
    }


    public static String sendGet(String url, String param) {
        return sendGet(url, param, "UTF-8");
    }


    public static String sendGet(String url, String param, String contentType) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            String str1 = StringUtils.isNotBlank(param) ? (url + "?" + param) : url;
            bV.info("sendGet - {}", str1);
            URL uRL = new URL(str1);
            URLConnection uRLConnection = uRL.openConnection();
            uRLConnection.setRequestProperty("accept", "*/*");
            uRLConnection.setRequestProperty("connection", "Keep-Alive");
            uRLConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            uRLConnection.setConnectTimeout(30000);
            uRLConnection.setReadTimeout(30000);
            uRLConnection.connect();
            bufferedReader = new BufferedReader(new InputStreamReader(uRLConnection.getInputStream(), contentType));
            String str2;
            while ((str2 = bufferedReader.readLine()) != null) {
                stringBuilder.append(str2);
            }
            bV.info("recv - {}", stringBuilder);
        } catch (ConnectException connectException) {

            bV.error("调用HttpUtils.sendGet ConnectException, url=" + url + ",param=" + param, connectException);
        } catch (SocketTimeoutException socketTimeoutException) {

            bV.error("调用HttpUtils.sendGet SocketTimeoutException, url=" + url + ",param=" + param, socketTimeoutException);
        } catch (IOException iOException) {

            bV.error("调用HttpUtils.sendGet IOException, url=" + url + ",param=" + param, iOException);
        } catch (Exception exception) {

            bV.error("调用HttpsUtil.sendGet Exception, url=" + url + ",param=" + param, exception);
        } finally {


            try {

                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception exception) {

                bV.error("调用in.close Exception, url=" + url + ",param=" + param, exception);
            }
        }
        return stringBuilder.toString();
    }


    public static String sendPost(String url, String param) {
        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            bV.info("sendPost - {}", url);
            URL uRL = new URL(url);
            URLConnection uRLConnection = uRL.openConnection();
            uRLConnection.setRequestProperty("accept", "*/*");
            uRLConnection.setRequestProperty("connection", "Keep-Alive");
            uRLConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            uRLConnection.setRequestProperty("Accept-Charset", "utf-8");
            uRLConnection.setRequestProperty("contentType", "utf-8");
            uRLConnection.setDoOutput(true);
            uRLConnection.setDoInput(true);
            printWriter = new PrintWriter(uRLConnection.getOutputStream());
            printWriter.print(param);
            printWriter.flush();
            bufferedReader = new BufferedReader(new InputStreamReader(uRLConnection.getInputStream(), StandardCharsets.UTF_8));
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str);
            }
            bV.info("recv - {}", stringBuilder);
        } catch (ConnectException connectException) {

            bV.error("调用HttpUtils.sendPost ConnectException, url=" + url + ",param=" + param, connectException);
        } catch (SocketTimeoutException socketTimeoutException) {

            bV.error("调用HttpUtils.sendPost SocketTimeoutException, url=" + url + ",param=" + param, socketTimeoutException);
        } catch (IOException iOException) {

            bV.error("调用HttpUtils.sendPost IOException, url=" + url + ",param=" + param, iOException);
        } catch (Exception exception) {

            bV.error("调用HttpsUtil.sendPost Exception, url=" + url + ",param=" + param, exception);
        } finally {


            try {

                if (printWriter != null) {
                    printWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException iOException) {

                bV.error("调用in.close Exception, url=" + url + ",param=" + param, iOException);
            }
        }
        return stringBuilder.toString();
    }


    public static String sendSSLPost(String url, String param) {
        StringBuilder stringBuilder = new StringBuilder();
        String str = url + "?" + param;

        try {
            bV.info("sendSSLPost - {}", str);
            SSLContext sSLContext = SSLContext.getInstance("SSL");
            sSLContext.init(null, new TrustManager[]{new b()}, new SecureRandom());
            URL uRL = new URL(str);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) uRL.openConnection();
            httpsURLConnection.setRequestProperty("accept", "*/*");
            httpsURLConnection.setRequestProperty("connection", "Keep-Alive");
            httpsURLConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            httpsURLConnection.setRequestProperty("Accept-Charset", "utf-8");
            httpsURLConnection.setRequestProperty("contentType", "utf-8");
            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setDoInput(true);

            httpsURLConnection.setSSLSocketFactory(sSLContext.getSocketFactory());
            httpsURLConnection.setHostnameVerifier(new a());
            httpsURLConnection.connect();
            InputStream inputStream = httpsURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String str1 = "";
            while ((str1 = bufferedReader.readLine()) != null) {

                if (str1 != null && !"".equals(str1.trim())) {
                    stringBuilder.append(new String(str1.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                }
            }
            bV.info("recv - {}", stringBuilder);
            httpsURLConnection.disconnect();
            bufferedReader.close();
        } catch (ConnectException connectException) {

            bV.error("调用HttpUtils.sendSSLPost ConnectException, url=" + url + ",param=" + param, connectException);
        } catch (SocketTimeoutException socketTimeoutException) {

            bV.error("调用HttpUtils.sendSSLPost SocketTimeoutException, url=" + url + ",param=" + param, socketTimeoutException);
        } catch (IOException iOException) {

            bV.error("调用HttpUtils.sendSSLPost IOException, url=" + url + ",param=" + param, iOException);
        } catch (Exception exception) {

            bV.error("调用HttpsUtil.sendSSLPost Exception, url=" + url + ",param=" + param, exception);
        }
        return stringBuilder.toString();
    }

    public static String sendJsonPost(String url, String json) throws IOException {
        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            bV.info("sendPost - {}", url);
            URL uRL = new URL(url);
            URLConnection uRLConnection = uRL.openConnection();
            uRLConnection.setRequestProperty("accept", "*/*");
            uRLConnection.setRequestProperty("connection", "Keep-Alive");
            uRLConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            uRLConnection.setRequestProperty("Accept-Charset", "utf-8");
            uRLConnection.setRequestProperty("Content-Type", "application/json");
            uRLConnection.setDoOutput(true);
            uRLConnection.setDoInput(true);
            printWriter = new PrintWriter(uRLConnection.getOutputStream());
            printWriter.print(json);
            printWriter.flush();
            bufferedReader = new BufferedReader(new InputStreamReader(uRLConnection.getInputStream(), StandardCharsets.UTF_8));
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str);
            }
            bV.info("recv - {}", stringBuilder);
        } catch (ConnectException connectException) {

            bV.error("调用HttpUtils.sendPost ConnectException, url=" + url + ",param=" + json, connectException);
        } catch (SocketTimeoutException socketTimeoutException) {

            bV.error("调用HttpUtils.sendPost SocketTimeoutException, url=" + url + ",param=" + json, socketTimeoutException);
        } catch (IOException iOException) {

            bV.error("调用HttpUtils.sendPost IOException, url=" + url + ",param=" + json, iOException);
        } catch (Exception exception) {

            bV.error("调用HttpsUtil.sendPost Exception, url=" + url + ",param=" + json, exception);
        } finally {


            try {

                if (printWriter != null) {
                    printWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException iOException) {

                bV.error("调用in.close Exception, url=" + url + ",param=" + json, iOException);
            }
        }
        return stringBuilder.toString();
    }


    private static class b
            implements X509TrustManager {
        private b() {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class a
            implements HostnameVerifier {
        private a() {
        }

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}

