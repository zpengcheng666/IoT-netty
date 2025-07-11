package com.sydh.common.utils.license.os;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WindowsServerInfo
        extends AbstractServerInfo {
    private static final Logger ct = LoggerFactory.getLogger(WindowsServerInfo.class);

    @Override
    public List<String> getIpAddress() {
        List<String> list = null;

        try {
            List<InetAddress> list1 = getLocalAllInetAddress();

            if (list1 != null && !list1.isEmpty()) {
                list = (List) list1.stream().map(InetAddress::getHostAddress).distinct().map(String::toLowerCase).collect(Collectors.toList());
            }
        } catch (Exception exception) {
            ct.error("获取ip地址异常 {}", exception.getMessage());
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
                    ct.info("inetAddress:{},获取mac地址: {}", inetAddress, str);
                    if (str != null) {
                        arrayList.add(str);
                    }
                }
            }
        } catch (Exception exception) {
            ct.error("获取Mac地址异常 {}", exception.getMessage());
        }
        return arrayList;
    }

    @Override
    public String getCPUSerial() throws Exception {
        String str = "";


        Process process = Runtime.getRuntime().exec("wmic cpu get processorid");
        process.getOutputStream().close();
        Scanner scanner = new Scanner(process.getInputStream());

        if (scanner.hasNext()) {
            scanner.next();
        }

        if (scanner.hasNext()) {
            str = scanner.next().trim();
        }

        scanner.close();
        return str;
    }

    @Override
    public String getMainBoardSerial() throws Exception {
        String str = "";

        Process process = Runtime.getRuntime().exec("wmic baseboard get serialnumber");
        process.getOutputStream().close();
        Scanner scanner = new Scanner(process.getInputStream());

        if (scanner.hasNext()) {
            scanner.next();
        }

        if (scanner.hasNext()) {
            str = scanner.next().trim();
        }

        scanner.close();
        return str;
    }
}
