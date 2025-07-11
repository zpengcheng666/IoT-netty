package com.sydh.common.utils.license.os;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinuxServerInfo
        extends AbstractServerInfo {
    private static final Logger cs = LoggerFactory.getLogger(LinuxServerInfo.class);


    @Override
    public List<String> getIpAddress() {
        List<String> list = new ArrayList();

        try {
            List<InetAddress> list1 = getLocalAllInetAddress();

            if (list1 != null && !list1.isEmpty()) {
                list = (List) list1.stream().map(InetAddress::getHostAddress).distinct().map(String::toLowerCase).collect(Collectors.toList());
            }
        } catch (Exception exception) {
            cs.error("获取ip地址异常 {}", exception.getMessage());
        }

        return list;
    }

    @Override
    public List<String> getMacAddress() {
        ArrayList<String> arrayList = new ArrayList();

        try {
            List<InetAddress> list = getLocalAllInetAddress();
            if (list != null && !list.isEmpty()) {
                for (InetAddress inetAddress : list) {
                    String str = getMacByInetAddress(inetAddress);
                    if (str != null) {
                        arrayList.add(str);
                    }
                }
            }
        } catch (Exception exception) {
            cs.error("获取Mac地址异常 {}", exception.getMessage());
        }
        return arrayList;
    }

    @Override
    public String getCPUSerial() throws Exception {
        String str1 = "";


        String[] arrayOfString = {"/bin/bash", "-c", "dmidecode -t processor | grep 'ID' | awk -F ':' '{print $2}' | head -n 1"};
        Process process = Runtime.getRuntime().exec(arrayOfString);
        process.getOutputStream().close();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String str2 = bufferedReader.readLine().trim();
        if (StringUtils.isNotBlank(str2)) {
            str1 = str2;
        }

        bufferedReader.close();
        return str1;
    }

    @Override
    public String getMainBoardSerial() throws Exception {
        String str1 = "";


        String[] arrayOfString = {"/bin/bash", "-c", "dmidecode | grep 'Serial Number' | awk -F ':' '{print $2}' | head -n 1"};
        Process process = Runtime.getRuntime().exec(arrayOfString);
        process.getOutputStream().close();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String str2 = bufferedReader.readLine().trim();
        if (StringUtils.isNotBlank(str2)) {
            str1 = str2;
        }

        bufferedReader.close();
        return str1;
    }
}
