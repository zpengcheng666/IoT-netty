package com.sydh.common.controller;
//
//import com.sydh.common.config.LicenseConfig;
//import com.sydh.common.core.domain.AjaxResult;
//import com.sydh.common.service.ILService;
//import com.sydh.common.utils.MessageUtils;
//import com.sydh.common.utils.StringUtils;
//import io.swagger.annotations.Api;
//
//import java.util.Objects;
//import javax.annotation.Resource;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//@Api(tags = {"证书授权客户端"})
//@RestController
//@RequestMapping({"/license"})
//public class licIdxController {
//    @PostMapping({"/install"})
//    public AjaxResult install(@RequestBody LicenseConfig config) {
//        String str = this.a.install(config);
//        if (Objects.equals(str, "success")) {
//            return AjaxResult.success(MessageUtils.message("certificate.install.success", new Object[0]));
//        }
//        return AjaxResult.error(StringUtils.format(MessageUtils.message("certificate.install.fail", new Object[0]), new Object[]{str}));
//    }
//
//    @Resource
//    ILService a;
//
//    @GetMapping({"/getTime"})
//    public AjaxResult getTime() {
//        return AjaxResult.success(this.a.getTime());
//    }
//
//    @GetMapping({"/getInfo"})
//    public AjaxResult getInfo() {
//        return AjaxResult.success(this.a.getInfo());
//    }
//
//    @GetMapping({"/getServerInfo"})
//    public AjaxResult getServerInfo(@RequestParam("type") Long type) {
//        return AjaxResult.success(this.a.getServerInfo(type));
//    }
//
//    @GetMapping({"/validate"})
//    public AjaxResult validate() {
//        return AjaxResult.success(this.a.validate());
//    }
//
//    @PostMapping({"/upload"})
//    public AjaxResult handleFileUpload(@RequestParam("file") MultipartFile file) {
//        String str = this.a.upload(file);
//        if (Objects.equals(str, "success")) {
//            return AjaxResult.success(MessageUtils.message("certificate.upload.success", new Object[0]));
//        }
//        return AjaxResult.error(StringUtils.format(MessageUtils.message("certificate.upload.fail", new Object[0]), new Object[]{str}));
//    }
//}
