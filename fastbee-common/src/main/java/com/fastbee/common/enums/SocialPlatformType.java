/*    */ package com.fastbee.common.enums;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum SocialPlatformType
/*    */ {
/* 12 */   WECHAT_OPEN_WEB("wechat_open_web", "微信开放平台网站应用"),
/* 13 */   WECHAT_OPEN_WEB_BIND("wechat_open_web_bind", "微信开放平台网站应用个人中心绑定"),
/* 14 */   WECHAT_OPEN_MOBILE("wechat_open_mobile", "微信开放平台移动应用"),
/* 15 */   WECHAT_OPEN_MINI_PROGRAM("wechat_open_mini_program", "微信开放平台小程序"),
/* 16 */   WECHAT_OPEN_PUBLIC_ACCOUNT("wechat_open_public_account", "微信开放平台公众号"),
/* 17 */   QQ_OPEN_WEB("qq_open_web", "QQ互联网站应用"),
/* 18 */   QQ_OPEN_APP("qq_open_app", "QQ互联移动应用"),
/* 19 */   QQ_OPEN_MINI_PROGRAM("qq_open_mini_program", "QQ互联小程序");
/*    */   
/*    */   public String sourceClient;
/*    */   
/*    */   public String desc;
/*    */   
/*    */   public static final List<String> listWechatPlatform;
/*    */ 
/*    */   
/*    */   SocialPlatformType(String sourceClient, String desc) {
/* 29 */     this.sourceClient = sourceClient;
/* 30 */     this.desc = desc;
/*    */   }
/*    */   
/*    */   static {
/* 34 */     listWechatPlatform = Arrays.asList(new String[] { WECHAT_OPEN_WEB.sourceClient, WECHAT_OPEN_MOBILE.sourceClient, WECHAT_OPEN_MINI_PROGRAM.sourceClient });
/*    */   }
/*    */   public static String getDesc(String sourceClient) {
/* 37 */     for (SocialPlatformType socialPlatformType : values()) {
/* 38 */       if (socialPlatformType.getSourceClient().equals(sourceClient)) {
/* 39 */         return socialPlatformType.getDesc();
/*    */       }
/*    */     } 
/* 42 */     return null;
/*    */   }
/*    */   
/*    */   public static SocialPlatformType getSocialPlatformType(String sourceClient) {
/* 46 */     for (SocialPlatformType socialPlatformType : values()) {
/* 47 */       if (socialPlatformType.getSourceClient().equals(sourceClient)) {
/* 48 */         return socialPlatformType;
/*    */       }
/*    */     } 
/* 51 */     return null;
/*    */   }
/*    */   
/*    */   public String getSourceClient() {
/* 55 */     return this.sourceClient;
/*    */   }
/*    */   
/*    */   public void setSourceClient(String sourceClient) {
/* 59 */     this.sourceClient = sourceClient;
/*    */   }
/*    */   
/*    */   public String getDesc() {
/* 63 */     return this.desc;
/*    */   }
/*    */   
/*    */   public void setDesc(String desc) {
/* 67 */     this.desc = desc;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\enums\SocialPlatformType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */