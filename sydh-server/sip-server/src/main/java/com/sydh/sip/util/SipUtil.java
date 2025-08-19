package com.sydh.sip.util;

import cn.hutool.json.JSONUtil;
import com.sydh.sip.model.GbSdp;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.Subject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import javax.sdp.Origin;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sip.SipFactory;
import javax.sip.address.SipURI;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.message.Request;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class SipUtil {
    private static final String date_format_T = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String date_format = "yyyy-MM-dd HH:mm:ss";
    public static final Long DEFAULT_EXEC_TIMEOUT = 20000L;
    private static String ssrcPrefix;

    private static List<String> isUsed;

    private static List<String> notUsed;

    public final static String BUSINESS_GROUP = "215";
    public final static String VIRTUAL_ORGANIZATION = "216";
    public static final String TREETYPE_BUSINESS_GROUP = "BusinessGroup";
    public static final String TREETYPE_CIVIL_CODE = "CivilCode";

    public static final String PREFIX_PLAYRECORD = "gb_playrecord";
    public static final String PREFIX_PLAY = "gb_play";
    public static final String PREFIX_TALK = "gb_talk";

    private static void init() {
        isUsed = new ArrayList<String>();
        notUsed = new ArrayList<String>();
        for (int i = 1; i < 10000; i++) {
            if (i < 10) {
                notUsed.add("000" + i);
            } else if (i < 100) {
                notUsed.add("00" + i);
            } else if (i < 1000) {
                notUsed.add("0" + i);
            } else {
                notUsed.add(String.valueOf(i));
            }
        }
    }

    /**
     * 获取视频预览的SSRC值,第一位固定为0
     *
     */
    public static String getPlaySsrc(String domain) {
        return "0" + getSsrcPrefix(domain) + getSN();
    }

    /**
     * 获取录像回放的SSRC值,第一位固定为1
     *
     */
    public static String getPlayBackSsrc(String domain) {
        return "1" + getSsrcPrefix(domain) + getSN();
    }

    /**
     * 释放ssrc，主要用完的ssrc一定要释放，否则会耗尽
     *
     */
    public static void releaseSsrc(String ssrc) {
        if (isUsed == null || notUsed == null) {
            init();
        }
        String sn = ssrc.substring(6);
        isUsed.remove(sn);
        notUsed.add(sn);
    }

    /**
     * 获取后四位数SN,随机数
     *
     */
    private static String getSN() {
        String sn = null;
        int index = 0;
        if (notUsed.size() == 0) {
            throw new RuntimeException("ssrc已经用完");
        } else if (notUsed.size() == 1) {
            sn = notUsed.get(0);
        } else {
            index = new Random().nextInt(notUsed.size() - 1);
            sn = notUsed.get(index);
        }
        notUsed.remove(index);
        isUsed.add(sn);
        return sn;
    }

    private static String getSsrcPrefix(String domain) {
        if (ssrcPrefix == null) {
            ssrcPrefix = domain.substring(3, 8);
            init();
        }
        return ssrcPrefix;
    }

    public static String dateToISO8601(Date date) {
        SimpleDateFormat newsdf = new SimpleDateFormat(date_format_T, Locale.getDefault());
        return newsdf.format(date);
    }

    public static String timestampToISO8601(String formatTime) {
        Date date = new Date(Long.parseLong(formatTime) * 1000);
        SimpleDateFormat newsdf = new SimpleDateFormat(date_format_T, Locale.getDefault());
        return newsdf.format(date);
    }

    public static long ISO8601Totimestamp(String formatTime) {
        SimpleDateFormat oldsdf = new SimpleDateFormat(date_format_T, Locale.getDefault());
        try {
            return oldsdf.parse(formatTime).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Date ISO8601ToDate(String formatTime) {
        SimpleDateFormat oldsdf = new SimpleDateFormat(date_format_T, Locale.getDefault());
        try {
            return oldsdf.parse(formatTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long DateStringToTimestamp(String formatTime) {
        SimpleDateFormat format = new SimpleDateFormat(date_format);
        //设置要读取的时间字符串格式
        Date date;
        try {
            date = format.parse(formatTime);
            //转换为Date类
            return date.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Date timestampToDate(String formatTime) {
        return new Date(Long.parseLong(formatTime) * 1000);
    }

    public static String safeString(Object val) {
        if (val == null) {
            return "";
        }

        return val.toString();
    }
    public static String getUserIdFromFromHeader(Request request) {
        FromHeader fromHeader = (FromHeader)request.getHeader(FromHeader.NAME);
        return getUserIdFromFromHeader(fromHeader);
    }
    public static String getUserIdFromFromHeader(FromHeader fromHeader) {
        AddressImpl address = (AddressImpl)fromHeader.getAddress();
        SipUri uri = (SipUri) address.getURI();
        return uri.getUser();
    }

    public static String getChannelIdFromRequest(Request request) {
        Header subject = request.getHeader("subject");
        if (subject == null) {
            // 如果缺失subject
            return null;
        }
        return ((Subject) subject).getSubject().split(":")[0];
    }

    public static  String getNewViaTag() {
        return "z9hG4bK" + RandomStringUtils.randomNumeric(10);
    }

    public static String getNewFromTag(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getNewTag(){
        return String.valueOf(System.currentTimeMillis());
    }

    public static GbSdp parseSDP(String sdpStr) throws SdpParseException {
        // jainSip不支持y= f=字段， 移除以解析。
        int ssrcIndex = sdpStr.indexOf("y=");
        int mediaDescriptionIndex = sdpStr.indexOf("f=");
        // 检查是否有y字段
        SessionDescription sdp;
        String ssrc = null;
        String mediaDescription = null;
        if (mediaDescriptionIndex == 0 && ssrcIndex == 0) {
            sdp = SdpFactory.getInstance().createSessionDescription(sdpStr);
        }else {
            String lines[] = sdpStr.split("\\r?\\n");
            StringBuilder sdpBuffer = new StringBuilder();
            for (String line : lines) {
                if (line.trim().startsWith("y=")) {
                    ssrc = line.substring(2);
                }else if (line.trim().startsWith("f=")) {
                    mediaDescription = line.substring(2);
                }else {
                    sdpBuffer.append(line.trim()).append("\r\n");
                }
            }
            sdp = SdpFactory.getInstance().createSessionDescription(sdpBuffer.toString());
        }
        return GbSdp.getInstance(sdp, ssrc, mediaDescription);
    }
}
