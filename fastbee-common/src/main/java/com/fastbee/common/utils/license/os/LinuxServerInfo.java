/*    */ package com.fastbee.common.utils.license.os;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.InputStreamReader;
/*    */ import java.net.InetAddress;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.stream.Collectors;
/*    */ import org.apache.commons.lang3.StringUtils;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class LinuxServerInfo
/*    */   extends AbstractServerInfo
/*    */ {
/* 16 */   private static final Logger cs = LoggerFactory.getLogger(LinuxServerInfo.class);
/*    */ 
/*    */   
/*    */   public List<String> getIpAddress() {
/* 20 */     List<String> list = new ArrayList();
/*    */     
/*    */     try {
/* 23 */       List<InetAddress> list1 = getLocalAllInetAddress();
/*    */       
/* 25 */       if (list1 != null && !list1.isEmpty()) {
/* 26 */         list = (List)list1.stream().map(InetAddress::getHostAddress).distinct().map(String::toLowerCase).collect(Collectors.toList());
/*    */       }
/* 28 */     } catch (Exception exception) {
/* 29 */       cs.error("获取ip地址异常 {}", exception.getMessage());
/*    */     } 
/*    */     
/* 32 */     return list;
/*    */   }
/*    */ 
/*    */   
/*    */   public List<String> getMacAddress() {
/* 37 */     ArrayList<String> arrayList = new ArrayList();
/*    */     
/*    */     try {
/* 40 */       List<InetAddress> list = getLocalAllInetAddress();
/* 41 */       if (list != null && !list.isEmpty()) {
/* 42 */         for (InetAddress inetAddress : list) {
/* 43 */           String str = getMacByInetAddress(inetAddress);
/* 44 */           if (str != null) {
/* 45 */             arrayList.add(str);
/*    */           }
/*    */         } 
/*    */       }
/* 49 */     } catch (Exception exception) {
/* 50 */       cs.error("获取Mac地址异常 {}", exception.getMessage());
/*    */     } 
/* 52 */     return arrayList;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCPUSerial() throws Exception {
/* 58 */     String str1 = "";
/*    */ 
/*    */     
/* 61 */     String[] arrayOfString = { "/bin/bash", "-c", "dmidecode -t processor | grep 'ID' | awk -F ':' '{print $2}' | head -n 1" };
/* 62 */     Process process = Runtime.getRuntime().exec(arrayOfString);
/* 63 */     process.getOutputStream().close();
/*    */     
/* 65 */     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
/*    */     
/* 67 */     String str2 = bufferedReader.readLine().trim();
/* 68 */     if (StringUtils.isNotBlank(str2)) {
/* 69 */       str1 = str2;
/*    */     }
/*    */     
/* 72 */     bufferedReader.close();
/* 73 */     return str1;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMainBoardSerial() throws Exception {
/* 79 */     String str1 = "";
/*    */ 
/*    */     
/* 82 */     String[] arrayOfString = { "/bin/bash", "-c", "dmidecode | grep 'Serial Number' | awk -F ':' '{print $2}' | head -n 1" };
/* 83 */     Process process = Runtime.getRuntime().exec(arrayOfString);
/* 84 */     process.getOutputStream().close();
/*    */     
/* 86 */     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
/*    */     
/* 88 */     String str2 = bufferedReader.readLine().trim();
/* 89 */     if (StringUtils.isNotBlank(str2)) {
/* 90 */       str1 = str2;
/*    */     }
/*    */     
/* 93 */     bufferedReader.close();
/* 94 */     return str1;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\license\os\LinuxServerInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */