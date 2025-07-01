/*     */ package com.fastbee.common.utils.http;
/*     */ 
/*     */ import com.fastbee.common.utils.StringUtils;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.ConnectException;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.cert.X509Certificate;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpUtils
/*     */ {
/*  32 */   private static final Logger bV = LoggerFactory.getLogger(HttpUtils.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sendGet(String url) {
/*  42 */     return sendGet(url, "");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sendGet(String url, String param) {
/*  54 */     return sendGet(url, param, "UTF-8");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sendGet(String url, String param, String contentType) {
/*  67 */     StringBuilder stringBuilder = new StringBuilder();
/*  68 */     BufferedReader bufferedReader = null;
/*     */     
/*     */     try {
/*  71 */       String str1 = StringUtils.isNotBlank(param) ? (url + "?" + param) : url;
/*  72 */       bV.info("sendGet - {}", str1);
/*  73 */       URL uRL = new URL(str1);
/*  74 */       URLConnection uRLConnection = uRL.openConnection();
/*  75 */       uRLConnection.setRequestProperty("accept", "*/*");
/*  76 */       uRLConnection.setRequestProperty("connection", "Keep-Alive");
/*  77 */       uRLConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
/*  78 */       uRLConnection.setConnectTimeout(30000);
/*  79 */       uRLConnection.setReadTimeout(30000);
/*  80 */       uRLConnection.connect();
/*  81 */       bufferedReader = new BufferedReader(new InputStreamReader(uRLConnection.getInputStream(), contentType));
/*     */       String str2;
/*  83 */       while ((str2 = bufferedReader.readLine()) != null)
/*     */       {
/*  85 */         stringBuilder.append(str2);
/*     */       }
/*  87 */       bV.info("recv - {}", stringBuilder);
/*     */     }
/*  89 */     catch (ConnectException connectException) {
/*     */       
/*  91 */       bV.error("调用HttpUtils.sendGet ConnectException, url=" + url + ",param=" + param, connectException);
/*     */     }
/*  93 */     catch (SocketTimeoutException socketTimeoutException) {
/*     */       
/*  95 */       bV.error("调用HttpUtils.sendGet SocketTimeoutException, url=" + url + ",param=" + param, socketTimeoutException);
/*     */     }
/*  97 */     catch (IOException iOException) {
/*     */       
/*  99 */       bV.error("调用HttpUtils.sendGet IOException, url=" + url + ",param=" + param, iOException);
/*     */     }
/* 101 */     catch (Exception exception) {
/*     */       
/* 103 */       bV.error("调用HttpsUtil.sendGet Exception, url=" + url + ",param=" + param, exception);
/*     */     } finally {
/*     */ 
/*     */       
/*     */       try {
/*     */         
/* 109 */         if (bufferedReader != null)
/*     */         {
/* 111 */           bufferedReader.close();
/*     */         }
/*     */       }
/* 114 */       catch (Exception exception) {
/*     */         
/* 116 */         bV.error("调用in.close Exception, url=" + url + ",param=" + param, exception);
/*     */       } 
/*     */     } 
/* 119 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sendPost(String url, String param) {
/* 131 */     PrintWriter printWriter = null;
/* 132 */     BufferedReader bufferedReader = null;
/* 133 */     StringBuilder stringBuilder = new StringBuilder();
/*     */     
/*     */     try {
/* 136 */       bV.info("sendPost - {}", url);
/* 137 */       URL uRL = new URL(url);
/* 138 */       URLConnection uRLConnection = uRL.openConnection();
/* 139 */       uRLConnection.setRequestProperty("accept", "*/*");
/* 140 */       uRLConnection.setRequestProperty("connection", "Keep-Alive");
/* 141 */       uRLConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
/* 142 */       uRLConnection.setRequestProperty("Accept-Charset", "utf-8");
/* 143 */       uRLConnection.setRequestProperty("contentType", "utf-8");
/* 144 */       uRLConnection.setDoOutput(true);
/* 145 */       uRLConnection.setDoInput(true);
/* 146 */       printWriter = new PrintWriter(uRLConnection.getOutputStream());
/* 147 */       printWriter.print(param);
/* 148 */       printWriter.flush();
/* 149 */       bufferedReader = new BufferedReader(new InputStreamReader(uRLConnection.getInputStream(), StandardCharsets.UTF_8));
/*     */       String str;
/* 151 */       while ((str = bufferedReader.readLine()) != null)
/*     */       {
/* 153 */         stringBuilder.append(str);
/*     */       }
/* 155 */       bV.info("recv - {}", stringBuilder);
/*     */     }
/* 157 */     catch (ConnectException connectException) {
/*     */       
/* 159 */       bV.error("调用HttpUtils.sendPost ConnectException, url=" + url + ",param=" + param, connectException);
/*     */     }
/* 161 */     catch (SocketTimeoutException socketTimeoutException) {
/*     */       
/* 163 */       bV.error("调用HttpUtils.sendPost SocketTimeoutException, url=" + url + ",param=" + param, socketTimeoutException);
/*     */     }
/* 165 */     catch (IOException iOException) {
/*     */       
/* 167 */       bV.error("调用HttpUtils.sendPost IOException, url=" + url + ",param=" + param, iOException);
/*     */     }
/* 169 */     catch (Exception exception) {
/*     */       
/* 171 */       bV.error("调用HttpsUtil.sendPost Exception, url=" + url + ",param=" + param, exception);
/*     */     } finally {
/*     */ 
/*     */       
/*     */       try {
/*     */         
/* 177 */         if (printWriter != null)
/*     */         {
/* 179 */           printWriter.close();
/*     */         }
/* 181 */         if (bufferedReader != null)
/*     */         {
/* 183 */           bufferedReader.close();
/*     */         }
/*     */       }
/* 186 */       catch (IOException iOException) {
/*     */         
/* 188 */         bV.error("调用in.close Exception, url=" + url + ",param=" + param, iOException);
/*     */       } 
/*     */     } 
/* 191 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public static String sendSSLPost(String url, String param) {
/* 196 */     StringBuilder stringBuilder = new StringBuilder();
/* 197 */     String str = url + "?" + param;
/*     */     
/*     */     try {
/* 200 */       bV.info("sendSSLPost - {}", str);
/* 201 */       SSLContext sSLContext = SSLContext.getInstance("SSL");
/* 202 */       sSLContext.init(null, new TrustManager[] { new b() }, new SecureRandom());
/* 203 */       URL uRL = new URL(str);
/* 204 */       HttpsURLConnection httpsURLConnection = (HttpsURLConnection)uRL.openConnection();
/* 205 */       httpsURLConnection.setRequestProperty("accept", "*/*");
/* 206 */       httpsURLConnection.setRequestProperty("connection", "Keep-Alive");
/* 207 */       httpsURLConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
/* 208 */       httpsURLConnection.setRequestProperty("Accept-Charset", "utf-8");
/* 209 */       httpsURLConnection.setRequestProperty("contentType", "utf-8");
/* 210 */       httpsURLConnection.setDoOutput(true);
/* 211 */       httpsURLConnection.setDoInput(true);
/*     */       
/* 213 */       httpsURLConnection.setSSLSocketFactory(sSLContext.getSocketFactory());
/* 214 */       httpsURLConnection.setHostnameVerifier(new a());
/* 215 */       httpsURLConnection.connect();
/* 216 */       InputStream inputStream = httpsURLConnection.getInputStream();
/* 217 */       BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
/* 218 */       String str1 = "";
/* 219 */       while ((str1 = bufferedReader.readLine()) != null) {
/*     */         
/* 221 */         if (str1 != null && !"".equals(str1.trim()))
/*     */         {
/* 223 */           stringBuilder.append(new String(str1.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
/*     */         }
/*     */       } 
/* 226 */       bV.info("recv - {}", stringBuilder);
/* 227 */       httpsURLConnection.disconnect();
/* 228 */       bufferedReader.close();
/*     */     }
/* 230 */     catch (ConnectException connectException) {
/*     */       
/* 232 */       bV.error("调用HttpUtils.sendSSLPost ConnectException, url=" + url + ",param=" + param, connectException);
/*     */     }
/* 234 */     catch (SocketTimeoutException socketTimeoutException) {
/*     */       
/* 236 */       bV.error("调用HttpUtils.sendSSLPost SocketTimeoutException, url=" + url + ",param=" + param, socketTimeoutException);
/*     */     }
/* 238 */     catch (IOException iOException) {
/*     */       
/* 240 */       bV.error("调用HttpUtils.sendSSLPost IOException, url=" + url + ",param=" + param, iOException);
/*     */     }
/* 242 */     catch (Exception exception) {
/*     */       
/* 244 */       bV.error("调用HttpsUtil.sendSSLPost Exception, url=" + url + ",param=" + param, exception);
/*     */     } 
/* 246 */     return stringBuilder.toString();
/*     */   }
/*     */   
/*     */   public static String sendJsonPost(String url, String json) throws IOException {
/* 250 */     PrintWriter printWriter = null;
/* 251 */     BufferedReader bufferedReader = null;
/* 252 */     StringBuilder stringBuilder = new StringBuilder();
/*     */     
/*     */     try {
/* 255 */       bV.info("sendPost - {}", url);
/* 256 */       URL uRL = new URL(url);
/* 257 */       URLConnection uRLConnection = uRL.openConnection();
/* 258 */       uRLConnection.setRequestProperty("accept", "*/*");
/* 259 */       uRLConnection.setRequestProperty("connection", "Keep-Alive");
/* 260 */       uRLConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
/* 261 */       uRLConnection.setRequestProperty("Accept-Charset", "utf-8");
/* 262 */       uRLConnection.setRequestProperty("Content-Type", "application/json");
/* 263 */       uRLConnection.setDoOutput(true);
/* 264 */       uRLConnection.setDoInput(true);
/* 265 */       printWriter = new PrintWriter(uRLConnection.getOutputStream());
/* 266 */       printWriter.print(json);
/* 267 */       printWriter.flush();
/* 268 */       bufferedReader = new BufferedReader(new InputStreamReader(uRLConnection.getInputStream(), StandardCharsets.UTF_8));
/*     */       String str;
/* 270 */       while ((str = bufferedReader.readLine()) != null)
/*     */       {
/* 272 */         stringBuilder.append(str);
/*     */       }
/* 274 */       bV.info("recv - {}", stringBuilder);
/*     */     }
/* 276 */     catch (ConnectException connectException) {
/*     */       
/* 278 */       bV.error("调用HttpUtils.sendPost ConnectException, url=" + url + ",param=" + json, connectException);
/*     */     }
/* 280 */     catch (SocketTimeoutException socketTimeoutException) {
/*     */       
/* 282 */       bV.error("调用HttpUtils.sendPost SocketTimeoutException, url=" + url + ",param=" + json, socketTimeoutException);
/*     */     }
/* 284 */     catch (IOException iOException) {
/*     */       
/* 286 */       bV.error("调用HttpUtils.sendPost IOException, url=" + url + ",param=" + json, iOException);
/*     */     }
/* 288 */     catch (Exception exception) {
/*     */       
/* 290 */       bV.error("调用HttpsUtil.sendPost Exception, url=" + url + ",param=" + json, exception);
/*     */     } finally {
/*     */ 
/*     */       
/*     */       try {
/*     */         
/* 296 */         if (printWriter != null)
/*     */         {
/* 298 */           printWriter.close();
/*     */         }
/* 300 */         if (bufferedReader != null)
/*     */         {
/* 302 */           bufferedReader.close();
/*     */         }
/*     */       }
/* 305 */       catch (IOException iOException) {
/*     */         
/* 307 */         bV.error("调用in.close Exception, url=" + url + ",param=" + json, iOException);
/*     */       } 
/*     */     } 
/* 310 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class b
/*     */     implements X509TrustManager
/*     */   {
/*     */     private b() {}
/*     */ 
/*     */     
/*     */     public void checkClientTrusted(X509Certificate[] chain, String authType) {}
/*     */ 
/*     */     
/*     */     public void checkServerTrusted(X509Certificate[] chain, String authType) {}
/*     */ 
/*     */     
/*     */     public X509Certificate[] getAcceptedIssuers() {
/* 328 */       return new X509Certificate[0];
/*     */     }
/*     */   }
/*     */   
/*     */   private static class a
/*     */     implements HostnameVerifier {
/*     */     private a() {}
/*     */     
/*     */     public boolean verify(String hostname, SSLSession session) {
/* 337 */       return true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\http\HttpUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */