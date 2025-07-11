package com.sydh.common.utils.file;

import java.io.File;


public class FileTypeUtils {
    public static String getFileType(File file) {
        if (null == file) {
            return "";
        }
        return getFileType(file.getName());
    }


    public static String getFileType(String fileName) {
        int i = fileName.lastIndexOf(".");
        if (i < 0) {
            return "";
        }
        return fileName.substring(i + 1).toLowerCase();
    }


    public static String getFileExtendName(byte[] photoByte) {
        String str = "JPG";
        if (photoByte[0] == 71 && photoByte[1] == 73 && photoByte[2] == 70 && photoByte[3] == 56 && (photoByte[4] == 55 || photoByte[4] == 57) && photoByte[5] == 97) {


            str = "GIF";
        } else if (photoByte[6] == 74 && photoByte[7] == 70 && photoByte[8] == 73 && photoByte[9] == 70) {

            str = "JPG";
        } else if (photoByte[0] == 66 && photoByte[1] == 77) {

            str = "BMP";
        } else if (photoByte[1] == 80 && photoByte[2] == 78 && photoByte[3] == 71) {

            str = "PNG";
        }
        return str;
    }
}
