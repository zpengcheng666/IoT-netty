/*    */ package com.fastbee.common.utils.ip;
/*    */ 
/*    */ import com.alibaba.fastjson2.JSON;
/*    */ import com.alibaba.fastjson2.JSONObject;
/*    */ import com.fastbee.common.config.RuoYiConfig;
/*    */ import com.fastbee.common.utils.StringUtils;
/*    */ import com.fastbee.common.utils.http.HttpUtils;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AddressUtils
/*    */ {
/* 19 */   private static final Logger bW = LoggerFactory.getLogger(AddressUtils.class);
/*    */ 
/*    */   
/*    */   public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";
/*    */ 
/*    */   
/*    */   public static final String UNKNOWN = "XX XX";
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getRealAddressByIP(String ip) {
/* 30 */     if (IpUtils.internalIp(ip))
/*    */     {
/* 32 */       return "内网IP";
/*    */     }
/* 34 */     if (RuoYiConfig.isAddressEnabled()) {
/*    */       
/*    */       try {
/*    */         
/* 38 */         String str1 = HttpUtils.sendGet("http://whois.pconline.com.cn/ipJson.jsp", "ip=" + ip + "&json=true", "GBK");
/* 39 */         if (StringUtils.isEmpty(str1)) {
/*    */           
/* 41 */           bW.error("获取地理位置异常 {}", ip);
/* 42 */           return "XX XX";
/*    */         } 
/* 44 */         JSONObject jSONObject = JSON.parseObject(str1);
/* 45 */         String str2 = jSONObject.getString("pro");
/* 46 */         String str3 = jSONObject.getString("city");
/* 47 */         return String.format("%s %s", new Object[] { str2, str3 });
/*    */       }
/* 49 */       catch (Exception exception) {
/*    */         
/* 51 */         bW.error("获取地理位置异常 {}", ip);
/*    */       } 
/*    */     }
/* 54 */     return "XX XX";
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\ip\AddressUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */