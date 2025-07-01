/*    */ package com.fastbee.common.filter;
/*    */ 
/*    */ import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PropertyPreExcludeFilter
/*    */   extends SimplePropertyPreFilter
/*    */ {
/*    */   public PropertyPreExcludeFilter() {
/* 13 */     super(new String[0]);
/*    */   }
/*    */ 
/*    */   
/*    */   public PropertyPreExcludeFilter addExcludes(String... filters) {
/* 18 */     for (byte b = 0; b < filters.length; b++)
/*    */     {
/* 20 */       getExcludes().add(filters[b]);
/*    */     }
/* 22 */     return this;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\filter\PropertyPreExcludeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */