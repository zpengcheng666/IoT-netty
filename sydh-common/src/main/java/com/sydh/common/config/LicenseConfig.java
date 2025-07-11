package com.sydh.common.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.DefaultResourceLoader;
//import org.springframework.core.io.Resource;
//
//@Configuration
//@ConfigurationProperties(prefix = "license")
//public class LicenseConfig {
//    private static final Logger log = LoggerFactory.getLogger(LicenseConfig.class);
//    private String subject;
//    private String code;
//    private Long type;
//    private String region;
//    private String company;
//    private String publicAlias;
//    private String privateAlias;
//    private String keyPass;
//    private String storePass;
//    private String licensePath;
//    private String publicKeysStorePath;
//    private String privateKeysStorePath;
//
//    public String getLicensePath() {
//        return this.resolveLicensePath(this.licensePath);
//    }
//
//    public String getPublicKeysStorePath() {
//        return this.resolveLicensePath(this.publicKeysStorePath);
//    }
//
//    public String getPrivateKeysStorePath() {
//        return this.resolveLicensePath(this.privateKeysStorePath);
//    }
//
//    private String resolveLicensePath(String licensePath) {
//        if (licensePath != null && !licensePath.isEmpty()) {
//            if (licensePath.contains("classpath:")) {
//                DefaultResourceLoader var2 = new DefaultResourceLoader();
//
//                try {
//                    if (!this.isValidPath(licensePath)) {
//                        log.error("Invalid license path detected: {}", licensePath);
//                        throw new IllegalArgumentException("Invalid license path");
//                    } else {
//                        Resource var3 = var2.getResource(licensePath);
//                        return var3.getFile().getPath();
//                    }
//                } catch (Exception var4) {
//                    log.error("Error resolving license path: {}", var4.getMessage(), var4);
//                    return licensePath;
//                }
//            } else {
//                return licensePath;
//            }
//        } else {
//            log.warn("License path is null or empty, returning original path.");
//            return licensePath;
//        }
//    }
//
//    private boolean isValidPath(String path) {
//        return !path.contains("../") && !path.contains("..\\");
//    }
//
//    public LicenseConfig() {
//    }
//
//    public String getSubject() {
//        return this.subject;
//    }
//
//    public String getCode() {
//        return this.code;
//    }
//
//    public Long getType() {
//        return this.type;
//    }
//
//    public String getRegion() {
//        return this.region;
//    }
//
//    public String getCompany() {
//        return this.company;
//    }
//
//    public String getPublicAlias() {
//        return this.publicAlias;
//    }
//
//    public String getPrivateAlias() {
//        return this.privateAlias;
//    }
//
//    public String getKeyPass() {
//        return this.keyPass;
//    }
//
//    public String getStorePass() {
//        return this.storePass;
//    }
//
//    public void setSubject(String subject) {
//        this.subject = subject;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public void setType(Long type) {
//        this.type = type;
//    }
//
//    public void setRegion(String region) {
//        this.region = region;
//    }
//
//    public void setCompany(String company) {
//        this.company = company;
//    }
//
//    public void setPublicAlias(String publicAlias) {
//        this.publicAlias = publicAlias;
//    }
//
//    public void setPrivateAlias(String privateAlias) {
//        this.privateAlias = privateAlias;
//    }
//
//    public void setKeyPass(String keyPass) {
//        this.keyPass = keyPass;
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
//    public void setPrivateKeysStorePath(String privateKeysStorePath) {
//        this.privateKeysStorePath = privateKeysStorePath;
//    }
//
//    public boolean equals(Object o) {
//        if (o == this) {
//            return true;
//        } else if (!(o instanceof LicenseConfig)) {
//            return false;
//        } else {
//            LicenseConfig var2 = (LicenseConfig)o;
//            if (!var2.canEqual(this)) {
//                return false;
//            } else {
//                label155: {
//                    Long var3 = this.getType();
//                    Long var4 = var2.getType();
//                    if (var3 == null) {
//                        if (var4 == null) {
//                            break label155;
//                        }
//                    } else if (var3.equals(var4)) {
//                        break label155;
//                    }
//
//                    return false;
//                }
//
//                String var5 = this.getSubject();
//                String var6 = var2.getSubject();
//                if (var5 == null) {
//                    if (var6 != null) {
//                        return false;
//                    }
//                } else if (!var5.equals(var6)) {
//                    return false;
//                }
//
//                String var7 = this.getCode();
//                String var8 = var2.getCode();
//                if (var7 == null) {
//                    if (var8 != null) {
//                        return false;
//                    }
//                } else if (!var7.equals(var8)) {
//                    return false;
//                }
//
//                label134: {
//                    String var9 = this.getRegion();
//                    String var10 = var2.getRegion();
//                    if (var9 == null) {
//                        if (var10 == null) {
//                            break label134;
//                        }
//                    } else if (var9.equals(var10)) {
//                        break label134;
//                    }
//
//                    return false;
//                }
//
//                label127: {
//                    String var11 = this.getCompany();
//                    String var12 = var2.getCompany();
//                    if (var11 == null) {
//                        if (var12 == null) {
//                            break label127;
//                        }
//                    } else if (var11.equals(var12)) {
//                        break label127;
//                    }
//
//                    return false;
//                }
//
//                label120: {
//                    String var13 = this.getPublicAlias();
//                    String var14 = var2.getPublicAlias();
//                    if (var13 == null) {
//                        if (var14 == null) {
//                            break label120;
//                        }
//                    } else if (var13.equals(var14)) {
//                        break label120;
//                    }
//
//                    return false;
//                }
//
//                String var15 = this.getPrivateAlias();
//                String var16 = var2.getPrivateAlias();
//                if (var15 == null) {
//                    if (var16 != null) {
//                        return false;
//                    }
//                } else if (!var15.equals(var16)) {
//                    return false;
//                }
//
//                label106: {
//                    String var17 = this.getKeyPass();
//                    String var18 = var2.getKeyPass();
//                    if (var17 == null) {
//                        if (var18 == null) {
//                            break label106;
//                        }
//                    } else if (var17.equals(var18)) {
//                        break label106;
//                    }
//
//                    return false;
//                }
//
//                String var19 = this.getStorePass();
//                String var20 = var2.getStorePass();
//                if (var19 == null) {
//                    if (var20 != null) {
//                        return false;
//                    }
//                } else if (!var19.equals(var20)) {
//                    return false;
//                }
//
//                label92: {
//                    String var21 = this.getLicensePath();
//                    String var22 = var2.getLicensePath();
//                    if (var21 == null) {
//                        if (var22 == null) {
//                            break label92;
//                        }
//                    } else if (var21.equals(var22)) {
//                        break label92;
//                    }
//
//                    return false;
//                }
//
//                String var23 = this.getPublicKeysStorePath();
//                String var24 = var2.getPublicKeysStorePath();
//                if (var23 == null) {
//                    if (var24 != null) {
//                        return false;
//                    }
//                } else if (!var23.equals(var24)) {
//                    return false;
//                }
//
//                String var25 = this.getPrivateKeysStorePath();
//                String var26 = var2.getPrivateKeysStorePath();
//                if (var25 == null) {
//                    if (var26 != null) {
//                        return false;
//                    }
//                } else if (!var25.equals(var26)) {
//                    return false;
//                }
//
//                return true;
//            }
//        }
//    }
//
//    protected boolean canEqual(Object other) {
//        return other instanceof LicenseConfig;
//    }
//
//    public int hashCode() {
//        boolean var1 = true;
//        int var2 = 1;
//        Long var3 = this.getType();
//        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
//        String var4 = this.getSubject();
//        var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
//        String var5 = this.getCode();
//        var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
//        String var6 = this.getRegion();
//        var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
//        String var7 = this.getCompany();
//        var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
//        String var8 = this.getPublicAlias();
//        var2 = var2 * 59 + (var8 == null ? 43 : var8.hashCode());
//        String var9 = this.getPrivateAlias();
//        var2 = var2 * 59 + (var9 == null ? 43 : var9.hashCode());
//        String var10 = this.getKeyPass();
//        var2 = var2 * 59 + (var10 == null ? 43 : var10.hashCode());
//        String var11 = this.getStorePass();
//        var2 = var2 * 59 + (var11 == null ? 43 : var11.hashCode());
//        String var12 = this.getLicensePath();
//        var2 = var2 * 59 + (var12 == null ? 43 : var12.hashCode());
//        String var13 = this.getPublicKeysStorePath();
//        var2 = var2 * 59 + (var13 == null ? 43 : var13.hashCode());
//        String var14 = this.getPrivateKeysStorePath();
//        var2 = var2 * 59 + (var14 == null ? 43 : var14.hashCode());
//        return var2;
//    }
//
//    public String toString() {
//        return "LicenseConfig(subject=" + this.getSubject() + ", code=" + this.getCode() + ", type=" + this.getType() + ", region=" + this.getRegion() + ", company=" + this.getCompany() + ", publicAlias=" + this.getPublicAlias() + ", privateAlias=" + this.getPrivateAlias() + ", keyPass=" + this.getKeyPass() + ", storePass=" + this.getStorePass() + ", licensePath=" + this.getLicensePath() + ", publicKeysStorePath=" + this.getPublicKeysStorePath() + ", privateKeysStorePath=" + this.getPrivateKeysStorePath() + ")";
//    }
//}
