package com.sydh.common.event;
//
//import com.sydh.common.config.LicenseConfig;
//import com.sydh.common.constant.Constants;
//import com.sydh.common.core.domain.LVParam;
//import com.sydh.common.core.domain.LVerify;
//import com.sydh.common.core.redis.RedisCache;
//import de.schlichtherle.license.LicenseContentException;
//
//import javax.annotation.Resource;
//
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.ApplicationEvent;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.stereotype.Component;
//
////@Component
//public class LicenseCheckListener implements ApplicationListener<ContextRefreshedEvent> {
//    private static final Logger u = LoggerFactory.getLogger(LicenseCheckListener.class);
//
//    @Resource
//    LicenseConfig v;
//
//    @Resource
//    private RedisCache w;
//
//    public static boolean isStart = true;
//
//    public void onApplicationEvent(ContextRefreshedEvent event) {
//        if (isStart) {
//            LicenseConfig licenseConfig = (LicenseConfig) this.w.getCacheObject("sys_config:sys.license");
//            if (licenseConfig == null) {
//                u.info("++++++++ 证书未安装 ++++++++");
//                return;
//            }
////            this.v.setSubject(licenseConfig.getSubject());
////            this.v.setType(licenseConfig.getType());
////            this.v.setRegion(licenseConfig.getRegion());
////            this.v.setCompany(licenseConfig.getCompany());
////
////            if (StringUtils.isNotBlank(this.v.getLicensePath())) {
////
////                try {
////                    u.info("++++++++ 开始安装证书 ++++++++");
////                    LVParam lVParam = new LVParam();
////                    lVParam.setSubject(this.v.getSubject());
////                    lVParam.setPublicAlias(this.v.getPublicAlias());
////                    lVParam.setStorePass(this.v.getStorePass());
////                    lVParam.setLicensePath(this.v.getLicensePath());
////                    lVParam.setPublicKeysStorePath(this.v.getPublicKeysStorePath());
////                    LVerify lVerify = new LVerify();
////                    lVerify.install(lVParam);
////                    u.info("++++++++ 证书安装成功 ++++++++");
////                } catch (LicenseContentException licenseContentException) {
////                    if (Constants.EXC_LICENSE_HAS_EXPIRED.equals(licenseContentException.getMessage())) {
////                        u.error("证书安装失败，证书已过期！");
////                    } else {
////                        u.error("++++++++ 证书安装失败 ++++++++" + licenseContentException.getMessage());
////                    }
////                } catch (Exception exception) {
////                    u.error("++++++++ 证书安装失败 ++++++++" + exception.getMessage());
////                }
////            }
////            isStart = false;
//        }
//    }
//}
