/*    */ package com.fastbee.common.core.domain;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TreeEntity
/*    */   extends BaseEntity
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private String parentName;
/*    */   private Long parentId;
/*    */   private Integer orderNum;
/*    */   private String ancestors;
/* 28 */   private List<?> children = new ArrayList();
/*    */ 
/*    */   
/*    */   public String getParentName() {
/* 32 */     return this.parentName;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setParentName(String parentName) {
/* 37 */     this.parentName = parentName;
/*    */   }
/*    */ 
/*    */   
/*    */   public Long getParentId() {
/* 42 */     return this.parentId;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setParentId(Long parentId) {
/* 47 */     this.parentId = parentId;
/*    */   }
/*    */ 
/*    */   
/*    */   public Integer getOrderNum() {
/* 52 */     return this.orderNum;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setOrderNum(Integer orderNum) {
/* 57 */     this.orderNum = orderNum;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAncestors() {
/* 62 */     return this.ancestors;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setAncestors(String ancestors) {
/* 67 */     this.ancestors = ancestors;
/*    */   }
/*    */ 
/*    */   
/*    */   public List<?> getChildren() {
/* 72 */     return this.children;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setChildren(List<?> children) {
/* 77 */     this.children = children;
/*    */   }
/*    */ }


/* Location:              D:\repository\fastbee-common\fastbee-common-2.6.0.jar!\com\fastbee\common\core\domain\TreeEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */