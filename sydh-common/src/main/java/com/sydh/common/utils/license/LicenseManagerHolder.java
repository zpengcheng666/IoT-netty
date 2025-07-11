package com.sydh.common.utils.license;
//
//import de.schlichtherle.license.LicenseManager;
//import de.schlichtherle.license.LicenseParam;
//
//
//public class LicenseManagerHolder {
//    private static volatile LicenseManager cm;
//
//    public static LicenseManager getInstance(LicenseParam param) {
//        if (cm == null) {
//            synchronized (LicenseManagerHolder.class) {
//                if (cm == null) {
//                    cm = new CustomLicenseManager(param);
//                }
//            }
//        }
//        return cm;
//    }
//}
