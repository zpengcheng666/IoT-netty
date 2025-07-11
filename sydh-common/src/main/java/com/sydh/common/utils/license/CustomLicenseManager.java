package com.sydh.common.utils.license;

//import com.sydh.common.config.LicenseConfig;

//import com.sydh.common.constant.Constants;
//import com.sydh.common.core.domain.LCModel;
//import com.sydh.common.enums.LCType;
//import com.sydh.common.utils.license.os.AbstractServerInfo;
//import com.sydh.common.utils.license.os.LinuxServerInfo;
//import com.sydh.common.utils.license.os.WindowsServerInfo;
//import com.sydh.common.utils.spring.SpringUtils;
//import de.schlichtherle.license.LicenseContent;
//import de.schlichtherle.license.LicenseContentException;
//import de.schlichtherle.license.LicenseManager;
//import de.schlichtherle.license.LicenseNotary;
//import de.schlichtherle.license.LicenseParam;
//import de.schlichtherle.license.NoLicenseInstalledException;
//import de.schlichtherle.xml.GenericCertificate;
//import java.beans.ExceptionListener;
//import java.beans.XMLDecoder;
//import java.io.BufferedInputStream;
//import java.io.ByteArrayInputStream;
//import java.io.UnsupportedEncodingException;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class CustomLicenseManager extends LicenseManager {
//    private static final Logger ci = LoggerFactory.getLogger(CustomLicenseManager.class);
//    private static final String cj = "UTF-8";
//    private static final int ck = 8192;
////    private static LicenseConfig cl = (LicenseConfig)SpringUtils.getBean(LicenseConfig.class);
//
//    public CustomLicenseManager(LicenseParam param) {
//        super(param);
//    }
//
//    protected synchronized byte[] create(LicenseContent content, LicenseNotary notary) throws Exception {
//        this.initialize(content);
//        this.validateCreate(content);
//        GenericCertificate var3 = notary.sign(content);
//        return this.getPrivacyGuard().cert2key(var3);
//    }
//
//    protected synchronized LicenseContent install(byte[] key, LicenseNotary notary) throws Exception {
//        GenericCertificate var3 = this.getPrivacyGuard().key2cert(key);
//        notary.verify(var3);
//        LicenseContent var4 = (LicenseContent)this.o(var3.getEncoded());
//        this.validate(var4);
//        this.setLicenseKey(key);
//        this.setCertificate(var3);
//        return var4;
//    }
//
//    protected synchronized LicenseContent verify(LicenseNotary notary) throws Exception {
//        GenericCertificate var2 = this.getCertificate();
//        ci.info("LicenseParam:{}", this.getLicenseParam());
//        byte[] var3 = this.getLicenseKey();
//        if (null == var3) {
//            throw new NoLicenseInstalledException(this.getLicenseParam().getSubject());
//        } else {
//            var2 = this.getPrivacyGuard().key2cert(var3);
//            notary.verify(var2);
//            LicenseContent var4 = (LicenseContent)this.o(var2.getEncoded());
//            this.validate(var4);
//            this.setCertificate(var2);
//            return var4;
//        }
//    }
//
//    public synchronized void validateCreate(LicenseContent content) throws LicenseContentException {
//        LicenseParam var2 = this.getLicenseParam();
//        Date var3 = new Date();
//        Date var4 = content.getNotBefore();
//        Date var5 = content.getNotAfter();
//        if (null != var5 && var3.after(var5)) {
//            throw new LicenseContentException("证书失效时间不能早于当前时间");
//        } else if (null != var4 && null != var5 && var5.before(var4)) {
//            throw new LicenseContentException("证书生效时间不能晚于证书失效时间");
//        } else {
//            String var6 = content.getConsumerType();
//            if (null == var6) {
//                throw new LicenseContentException("用户类型不能为空");
//            }
//        }
//    }
//
////    public synchronized void validate(LicenseContent content) throws LicenseContentException {
////        try {
////            super.validate(content);
////        } catch (LicenseContentException var7) {
////            if (!Constants.EXC_LICENSE_HAS_EXPIRED.equals(var7.getMessage())) {
////                throw var7;
////            }
////
////            ci.info("证书已过期，不影响旧版本运行。");
////            ci.info("同步新版本功能，请重新授权！");
////        }
////
////        LCModel var2 = (LCModel)content.getExtra();
////        ci.info("{}", var2);
////        LCModel var3 = this.e();
////        if (var2 == null) {
////            throw new LicenseContentException("不能获取服务器硬件信息");
////        } else if (var2.getType() != null) {
////            if (!var2.getType().equals(cl.getType())) {
////                ci.error("当前证书类型不正确，请重新选择！");
////                throw new LicenseContentException("当前证书类型不正确，请重新选择！");
////            } else {
////                if (var2.getType().equals(LCType.ENTERPRISE.getType())) {
////                    ci.info("config.Code: {}", cl.getCode());
////                    ci.info("serverCheckModel Region: {}", var3.getRegion());
////                    if (cl.getCode() != null && var3.getRegion() == null) {
////                        try {
////                            String var4 = cl.getSubject() + "+" + var2.getCompany();
////                            String var5 = AESUtil.decrypt(cl.getCode(), cl.getStorePass());
////                            ci.info("Decrypted: {}", var5);
////                            if (!var5.equals(var4)) {
////                                ci.error("离线授权码不正确，请重新生成！");
////                                throw new LicenseContentException("离线授权码不正确，请重新生成！");
////                            }
////                        } catch (Exception var6) {
////                            ci.error("解析离线授权码失败:{}", var6.getMessage());
////                        }
////                    } else {
////                        if (var2.getCompany() == null || var2.getRegion() == null) {
////                            ci.error("当前证书有误，请联系客服重新生成！");
////                            throw new LicenseContentException("当前证书有误，请联系客服重新生成！");
////                        }
////
////                        if (!var2.getCompany().equals(cl.getCompany())) {
////                            ci.error("当前服务器的公司名:[{}]没在授权范围内", cl.getCompany());
////                            throw new LicenseContentException(String.format("当前服务器的公司名:[%s]没在授权范围内", cl.getCompany()));
////                        }
////
////                        if (!this.a(var2.getRegion(), var3.getRegion())) {
////                            ci.error("当前服务器的区域:[{}]没在授权范围内", var3.getRegion());
////                            throw new LicenseContentException(String.format("当前服务器的区域:[%s]没在授权范围内", var3.getRegion()));
////                        }
////                    }
////                } else if (var2.getType().equals(LCType.PERSON.getType())) {
////                    if (!this.a(var2.getIp(), var3.getIp())) {
////                        ci.error("当前服务器的IP:[{}]没在授权范围内", var3.getIp());
////                        throw new LicenseContentException(String.format("当前服务器的IP:[%s]没在授权范围内", var3.getIp()));
////                    }
////
////                    if (!this.a(var2.getMac(), var3.getMac())) {
////                        ci.error("当前服务器的Mac地址:[{}]没在授权范围内", var3.getMac());
////                        throw new LicenseContentException(String.format("当前服务器的Mac地址:[%s]没在授权范围内", var3.getMac()));
////                    }
////                }
////
////            }
////        } else {
////            throw new LicenseContentException("不能获取授权类型");
////        }
////    }
//
//    private Object o(String var1) {
//        BufferedInputStream var2 = null;
//        XMLDecoder var3 = null;
//
//        try {
//            var2 = new BufferedInputStream(new ByteArrayInputStream(var1.getBytes("UTF-8")));
//            var3 = new XMLDecoder(new BufferedInputStream(var2, 8192), (Object)null, (ExceptionListener)null);
//            Object var4 = var3.readObject();
//            return var4;
//        } catch (UnsupportedEncodingException var14) {
//            var14.printStackTrace();
//            ci.error("XMLDecoder解析XML失败:{}", var14.getMessage());
//        } finally {
//            try {
//                if (var3 != null) {
//                    var3.close();
//                }
//
//                if (var2 != null) {
//                    var2.close();
//                }
//            } catch (Exception var13) {
//                var13.printStackTrace();
//                ci.error("XMLDecoder解析XML失败:{}", var13.getMessage());
//            }
//
//        }
//
//        return null;
//    }
//
//    private LCModel e() {
//        String var1 = System.getProperty("os.name").toLowerCase();
//        Object var2 = null;
//        if (var1.startsWith("windows")) {
//            var2 = new WindowsServerInfo();
//        } else if (var1.startsWith("linux")) {
//            var2 = new LinuxServerInfo();
//        } else {
//            var2 = new LinuxServerInfo();
//        }
//
//        return LCModel.getServerInfo((AbstractServerInfo)var2);
//    }
//
//    private boolean a(List<String> var1, List<String> var2) {
//        if (var1 != null && !var1.isEmpty()) {
//            if (var2 != null && !var2.isEmpty()) {
//                Iterator var3 = var1.iterator();
//
//                while(var3.hasNext()) {
//                    String var4 = (String)var3.next();
//                    if (var2.contains(var4.trim())) {
//                        return true;
//                    }
//                }
//            }
//
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    private boolean c(String var1, String var2) {
//        if (StringUtils.isNotBlank(var1)) {
//            return StringUtils.isNotBlank(var2) && var1.equals(var2);
//        } else {
//            return true;
//        }
//    }
//}