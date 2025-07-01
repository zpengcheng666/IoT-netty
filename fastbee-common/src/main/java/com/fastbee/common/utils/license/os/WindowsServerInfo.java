/*    */ package com.fastbee.common.utils.license.os;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Scanner;
/*    */ import java.util.stream.Collectors;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class WindowsServerInfo
/*    */   extends AbstractServerInfo
/*    */ {
/* 14 */   private static final Logger ct = LoggerFactory.getLogger(WindowsServerInfo.class);
/*    */ 
/*    */ 
/*    */   
/*    */   public List<String> getIpAddress() {
/* 19 */     List<String> list = null;
/*    */     
/*    */     try {
/* 22 */       List<InetAddress> list1 = getLocalAllInetAddress();
/*    */       
/* 24 */       if (list1 != null && !list1.isEmpty()) {
/* 25 */         list = (List)list1.stream().map(InetAddress::getHostAddress).distinct().map(String::toLowerCase).collect(Collectors.toList());
/*    */       }
/* 27 */     } catch (Exception exception) {
/* 28 */       ct.error("获取ip地址异常 {}", exception.getMessage());
/*    */     } 
/* 30 */     return list;
/*    */   }
/*    */ 
/*    */   
/*    */   public List<String> getMacAddress() {
/* 35 */     ArrayList<String> arrayList = new ArrayList();
/*    */     
/*    */     try {
/* 38 */       List<InetAddress> list = getLocalAllInetAddress();
/* 39 */       if (list != null && !list.isEmpty()) {
/* 40 */         for (InetAddress inetAddress : list) {
/* 41 */           String str = getMacByInetAddress(inetAddress);
/* 42 */           ct.info("inetAddress:{},获取mac地址: {}", inetAddress, str);
/* 43 */           if (str != null) {
/* 44 */             arrayList.add(str);
/*    */           }
/*    */         } 
/*    */       }
/* 48 */     } catch (Exception exception) {
/* 49 */       ct.error("获取Mac地址异常 {}", exception.getMessage());
/*    */     } 
/* 51 */     return arrayList;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCPUSerial() throws Exception {
/* 57 */     String str = "";
/*    */ 
/*    */     
/* 60 */     Process process = Runtime.getRuntime().exec("wmic cpu get processorid");
/* 61 */     process.getOutputStream().close();
/* 62 */     Scanner scanner = new Scanner(process.getInputStream());
/*    */     
/* 64 */     if (scanner.hasNext()) {
/* 65 */       scanner.next();
/*    */     }
/*    */     
/* 68 */     if (scanner.hasNext()) {
/* 69 */       str = scanner.next().trim();
/*    */     }
/*    */     
/* 72 */     scanner.close();
/* 73 */     return str;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMainBoardSerial() throws Exception {
/* 79 */     String str = "";
/*    */ 
/*    */     
/* 82 */     Process process = Runtime.getRuntime().exec("wmic baseboard get serialnumber");
/* 83 */     process.getOutputStream().close();
/* 84 */     Scanner scanner = new Scanner(process.getInputStream());
/*    */     
/* 86 */     if (scanner.hasNext()) {
/* 87 */       scanner.next();
/*    */     }
/*    */     
/* 90 */     if (scanner.hasNext()) {
/* 91 */       str = scanner.next().trim();
/*    */     }
/*    */     
/* 94 */     scanner.close();
/* 95 */     return str;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\commo\\utils\license\os\WindowsServerInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */