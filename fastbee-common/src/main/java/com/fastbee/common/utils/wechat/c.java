/*     */ package com.fastbee.common.utils.wechat;
/*     */ 
/*     */ import java.io.StringReader;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.InputSource;
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
/*     */ 
/*     */ class c
/*     */ {
/*     */   public static Object[] r(String paramString) throws AesException {
/*  34 */     Object[] arrayOfObject = new Object[3];
/*     */     try {
/*  36 */       DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
/*     */       
/*  38 */       String str = null;
/*     */ 
/*     */       
/*  41 */       str = "http://apache.org/xml/features/disallow-doctype-decl";
/*  42 */       documentBuilderFactory.setFeature(str, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  48 */       str = "http://xml.org/sax/features/external-general-entities";
/*  49 */       documentBuilderFactory.setFeature(str, false);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  54 */       str = "http://xml.org/sax/features/external-parameter-entities";
/*  55 */       documentBuilderFactory.setFeature(str, false);
/*     */ 
/*     */       
/*  58 */       str = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
/*  59 */       documentBuilderFactory.setFeature(str, false);
/*     */ 
/*     */       
/*  62 */       documentBuilderFactory.setXIncludeAware(false);
/*  63 */       documentBuilderFactory.setExpandEntityReferences(false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  71 */       DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
/*  72 */       StringReader stringReader = new StringReader(paramString);
/*  73 */       InputSource inputSource = new InputSource(stringReader);
/*  74 */       Document document = documentBuilder.parse(inputSource);
/*     */       
/*  76 */       Element element = document.getDocumentElement();
/*  77 */       NodeList nodeList = element.getElementsByTagName("Encrypt");
/*  78 */       arrayOfObject[0] = Integer.valueOf(0);
/*  79 */       arrayOfObject[1] = nodeList.item(0).getTextContent();
/*  80 */       return arrayOfObject;
/*  81 */     } catch (Exception exception) {
/*  82 */       exception.printStackTrace();
/*  83 */       throw new AesException(-40002);
/*     */     } 
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
/*     */   public static String a(String paramString1, String paramString2, String paramString3, String paramString4) {
/*  97 */     String str = "<xml>\n<Encrypt><![CDATA[%1$s]]></Encrypt>\n<MsgSignature><![CDATA[%2$s]]></MsgSignature>\n<TimeStamp>%3$s</TimeStamp>\n<Nonce><![CDATA[%4$s]]></Nonce>\n</xml>";
/*     */ 
/*     */     
/* 100 */     return String.format(str, new Object[] { paramString1, paramString2, paramString3, paramString4 });
/*     */   }
/*     */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\wechat\c.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */