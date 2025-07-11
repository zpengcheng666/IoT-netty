package com.sydh.common.core.domain;
//
//import com.sydh.common.enums.LCType;
//import com.sydh.common.utils.license.LicenseManagerHolder;
//import de.schlichtherle.license.DefaultCipherParam;
//import de.schlichtherle.license.DefaultLicenseParam;
//import de.schlichtherle.license.LicenseContent;
//import de.schlichtherle.license.LicenseContentException;
//import de.schlichtherle.license.LicenseManager;
//import de.schlichtherle.license.LicenseParam;
//import java.io.File;
//import java.text.DateFormat;
//import java.text.MessageFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Objects;
//import java.util.prefs.Preferences;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class LVerify {
//    private static final Logger log = LoggerFactory.getLogger(LVerify.class);
//    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    private static final String VERSION = "2.6.0";
//    private static final String BUILD_TIME = "2025-02-28 00:00:00";
//
//    public LVerify() {
//    }
//
//    public synchronized LicenseContent install(LVParam param) throws Exception {
//        LicenseContent var2 = null;
//        LicenseParam var3 = this.initLicenseParam(param);
//
//        try {
//            LicenseManager var4 = LicenseManagerHolder.getInstance(var3);
//            var4.uninstall();
//            var4.setLicenseParam(var3);
//            var2 = var4.install(new File(param.getLicensePath()));
//            log.info(MessageFormat.format("证书安装成功，证书有效期：{0} - {1}", this.format.format(var2.getNotBefore()), this.format.format(var2.getNotAfter())));
//            return var2;
//        } catch (LicenseContentException var5) {
//            throw var5;
//        } catch (Exception var6) {
//            log.error("证书安装失败！" + var6);
//            throw var6;
//        }
//    }
//
//    public boolean verify() {
//        LicenseManager var1 = LicenseManagerHolder.getInstance((LicenseParam)null);
//        log.info("licenseManager：{}！", var1);
//
//        try {
//            LicenseContent var2 = var1.verify();
//            LCModel var3 = (LCModel)var2.getExtra();
//            if (this.checkLicenseVerify(var2.getNotBefore(), var2.getNotAfter(), var3)) {
//                log.error("证书有效期超过版本编译时间！版本编译时间：{}，过期时间：{}", "2025-02-28 00:00:00", var2.getNotAfter());
//                return false;
//            } else {
//                log.info(MessageFormat.format("证书校验通过，证书有效期：{0} - {1}", this.format.format(var2.getNotBefore()), this.format.format(var2.getNotAfter())));
//                return true;
//            }
//        } catch (Exception var4) {
//            log.error("证书校验失败！" + var4);
//            return false;
//        }
//    }
//
//    private boolean checkLicenseVerify(Date startDate, Date endDate, LCModel model) {
//        if (startDate != null && endDate != null) {
//            Date var4 = new Date();
//
//            try {
//                Date var5 = this.format.parse("2025-02-28 00:00:00");
//                Long var6 = model.getType();
//                if (Objects.equals(var6, LCType.TRIAL.getType())) {
//                    if (endDate.before(var4)) {
//                        log.info(MessageFormat.format("证书校验失败，证书有效期：{0} - {1}，当前时间为 {2}", this.format.format(startDate), this.format.format(endDate), this.format.format(var4)));
//                        return true;
//                    }
//                } else {
//                    if (!Objects.equals(var6, LCType.PERSON.getType()) && !Objects.equals(var6, LCType.ENTERPRISE.getType())) {
//                        return true;
//                    }
//
//                    if (endDate.before(var4) && !var5.before(endDate)) {
//                        log.info(MessageFormat.format("证书校验失败，证书有效期：{0} - {1}，当前时间为 {2} 编译时间为 {3}", this.format.format(startDate), this.format.format(endDate), this.format.format(var4), this.format.format(var5)));
//                        return true;
//                    }
//                }
//
//                return false;
//            } catch (ParseException var7) {
//                log.error("编译时间格式错误: " + var7.getMessage());
//                return true;
//            }
//        } else {
//            log.info("证书校验失败，开始日期或结束日期为空");
//            return true;
//        }
//    }
//
//    private LicenseParam initLicenseParam(LVParam param) {
//        Preferences var2 = Preferences.userNodeForPackage(LVerify.class);
//        DefaultCipherParam var3 = new DefaultCipherParam(param.getStorePass());
//        CKStoreParam var4 = new CKStoreParam(LVerify.class, param.getPublicKeysStorePath(), param.getPublicAlias(), param.getStorePass(), (String)null);
//        return new DefaultLicenseParam(param.getSubject(), var2, var4, var3);
//    }
//
//    public static String getVersion() {
//        return "2.6.0";
//    }
//
//    public static String getBuildTime() {
//        return "2025-02-28 00:00:00";
//    }
//}
