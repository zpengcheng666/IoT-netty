package com.sydh.common.service.impl;
//
//import com.sydh.common.config.LicenseConfig;
//import com.sydh.common.core.domain.LCModel;
//import com.sydh.common.core.domain.LVParam;
//import com.sydh.common.core.domain.LVerify;
//import com.sydh.common.core.redis.RedisCache;
//import com.sydh.common.enums.LCType;
//import com.sydh.common.service.ILService;
//import com.sydh.common.utils.license.LicenseManagerHolder;
//import com.sydh.common.utils.license.os.AbstractServerInfo;
//import com.sydh.common.utils.license.os.LinuxServerInfo;
//import com.sydh.common.utils.license.os.WindowsServerInfo;
//import de.schlichtherle.license.LicenseContent;
//import de.schlichtherle.license.LicenseManager;
//import de.schlichtherle.license.LicenseParam;
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import javax.annotation.Resource;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//@Service
//public class LServiceImpl implements ILService {
//    private static final Logger ak = LoggerFactory.getLogger(LServiceImpl.class);
//    @Resource
//    LicenseConfig v;
//    @Resource
//    private RedisCache w;
//
//    public LServiceImpl() {
//    }
//
//    public String install(LicenseConfig config) {
////        return "success";
//        if (StringUtils.isNotBlank(this.v.getLicensePath())) {
//            ak.info("++++++++ 开始安装证书 ++++++++");
//            LVParam var2 = new LVParam();
//            var2.setSubject(config.getSubject());
//            var2.setPublicAlias(this.v.getPublicAlias());
//            var2.setStorePass(this.v.getStorePass());
//            var2.setLicensePath(this.v.getLicensePath());
//            var2.setPublicKeysStorePath(this.v.getPublicKeysStorePath());
//            this.v.setSubject(config.getSubject());
//            this.v.setType(config.getType());
//            if (config.getType().equals(LCType.ENTERPRISE.getType())) {
//                this.v.setRegion(config.getRegion());
//                this.v.setCompany(config.getCompany());
//            }
//
//            LVerify var3 = new LVerify();
//
//            try {
//                var3.install(var2);
//                ak.info("++++++++ 证书安装成功 ++++++++");
//                this.w.setCacheObject("sys_config:sys.license", config);
//                return "success";
//            } catch (Exception var5) {
//                ak.error("证书安装失败: {}", var5.getMessage());
//                return var5.getMessage();
//            }
//        } else {
//            return "error";
//        }
//    }
//
//    public String getTime() {
//        LicenseManager var1 = LicenseManagerHolder.getInstance((LicenseParam)null);
//        SimpleDateFormat var2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        try {
//            LicenseContent var3 = var1.verify();
//            Date var4 = var3.getNotAfter();
//            return var2.format(var4);
//        } catch (Exception var5) {
//            var5.printStackTrace();
//            return "";
//        }
//    }
//
//    public Boolean validate() {
//        LVerify var1 = new LVerify();
//        return var1.verify();
//    }
//
//    public LicenseContent getInfo() {
//        LicenseManager var1 = LicenseManagerHolder.getInstance((LicenseParam)null);
//
//        try {
//            return var1.verify();
//        } catch (Exception var3) {
//            var3.printStackTrace();
//            return null;
//        }
//    }
//
//    public String getServerInfo(Long type) {
//        String var2 = System.getProperty("os.name").toLowerCase();
//        Object var3 = null;
//        if (var2.startsWith("windows")) {
//            var3 = new WindowsServerInfo();
//        } else if (var2.startsWith("linux")) {
//            var3 = new LinuxServerInfo();
//        } else {
//            var3 = new LinuxServerInfo();
//        }
//
//        return LCModel.getServerInfo((AbstractServerInfo)var3, type);
//    }
//
//    public String upload(MultipartFile file) {
//        if (file.isEmpty()) {
//            ak.error("++++++++ 文件为空 ++++++++");
//        }
//
//        File var2 = new File(this.v.getLicensePath());
//        if (!var2.getParentFile().exists()) {
//            var2.getParentFile().mkdirs();
//        }
//
//        try {
//            file.transferTo(var2);
//            ak.info("++++++++ 上传成功 ++++++++");
//            return "success";
//        } catch (IllegalStateException var4) {
//            var4.printStackTrace();
//        } catch (IOException var5) {
//            var5.printStackTrace();
//        }
//
//        ak.error("++++++++ 上传失败 ++++++++");
//        return "error";
//    }
//}
