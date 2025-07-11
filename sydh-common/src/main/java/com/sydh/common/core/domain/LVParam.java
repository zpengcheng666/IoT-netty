package com.sydh.common.core.domain;
//
//public class LVParam {
//    private String subject;
//    private String publicAlias;
//    private String storePass;
//    private String licensePath;
//    private String publicKeysStorePath;
//
//    public LVParam() {
//    }
//
//    public String getSubject() {
//        return this.subject;
//    }
//
//    public String getPublicAlias() {
//        return this.publicAlias;
//    }
//
//    public String getStorePass() {
//        return this.storePass;
//    }
//
//    public String getLicensePath() {
//        return this.licensePath;
//    }
//
//    public String getPublicKeysStorePath() {
//        return this.publicKeysStorePath;
//    }
//
//    public void setSubject(String subject) {
//        this.subject = subject;
//    }
//
//    public void setPublicAlias(String publicAlias) {
//        this.publicAlias = publicAlias;
//    }
//
//    public void setStorePass(String storePass) {
//        this.storePass = storePass;
//    }
//
//    public void setLicensePath(String licensePath) {
//        this.licensePath = licensePath;
//    }
//
//    public void setPublicKeysStorePath(String publicKeysStorePath) {
//        this.publicKeysStorePath = publicKeysStorePath;
//    }
//
//    public boolean equals(Object o) {
//        if (o == this) {
//            return true;
//        } else if (!(o instanceof LVParam)) {
//            return false;
//        } else {
//            LVParam var2 = (LVParam)o;
//            if (!var2.canEqual(this)) {
//                return false;
//            } else {
//                label71: {
//                    String var3 = this.getSubject();
//                    String var4 = var2.getSubject();
//                    if (var3 == null) {
//                        if (var4 == null) {
//                            break label71;
//                        }
//                    } else if (var3.equals(var4)) {
//                        break label71;
//                    }
//
//                    return false;
//                }
//
//                String var5 = this.getPublicAlias();
//                String var6 = var2.getPublicAlias();
//                if (var5 == null) {
//                    if (var6 != null) {
//                        return false;
//                    }
//                } else if (!var5.equals(var6)) {
//                    return false;
//                }
//
//                label57: {
//                    String var7 = this.getStorePass();
//                    String var8 = var2.getStorePass();
//                    if (var7 == null) {
//                        if (var8 == null) {
//                            break label57;
//                        }
//                    } else if (var7.equals(var8)) {
//                        break label57;
//                    }
//
//                    return false;
//                }
//
//                String var9 = this.getLicensePath();
//                String var10 = var2.getLicensePath();
//                if (var9 == null) {
//                    if (var10 != null) {
//                        return false;
//                    }
//                } else if (!var9.equals(var10)) {
//                    return false;
//                }
//
//                String var11 = this.getPublicKeysStorePath();
//                String var12 = var2.getPublicKeysStorePath();
//                if (var11 == null) {
//                    if (var12 == null) {
//                        return true;
//                    }
//                } else if (var11.equals(var12)) {
//                    return true;
//                }
//
//                return false;
//            }
//        }
//    }
//
//    protected boolean canEqual(Object other) {
//        return other instanceof LVParam;
//    }
//
//    public int hashCode() {
//        boolean var1 = true;
//        int var2 = 1;
//        String var3 = this.getSubject();
//        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
//        String var4 = this.getPublicAlias();
//        var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
//        String var5 = this.getStorePass();
//        var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
//        String var6 = this.getLicensePath();
//        var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
//        String var7 = this.getPublicKeysStorePath();
//        var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
//        return var2;
//    }
//
//    public String toString() {
//        return "LVParam(subject=" + this.getSubject() + ", publicAlias=" + this.getPublicAlias() + ", storePass=" + this.getStorePass() + ", licensePath=" + this.getLicensePath() + ", publicKeysStorePath=" + this.getPublicKeysStorePath() + ")";
//    }
//}