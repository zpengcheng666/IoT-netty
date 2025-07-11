package com.sydh.sip.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.core.redis.RedisKeyBuilder;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.oss.domain.OssDetail;
import com.sydh.oss.service.IOssDetailService;
import com.sydh.sip.domain.MediaServer;
import com.sydh.sip.domain.SipDevice;
import com.sydh.sip.model.RecordItem;
import com.sydh.sip.model.RecordList;
import com.sydh.sip.model.Stream;
import com.sydh.sip.model.VideoSessionInfo;
import com.sydh.sip.server.ISipCmd;
import com.sydh.sip.server.MessageInvoker;
import com.sydh.sip.server.RecordCacheManager;
import com.sydh.sip.service.*;
import com.sydh.sip.util.RecordApiUtils;
import com.sydh.sip.util.SipUtil;
import com.sydh.sip.util.ZlmApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.sydh.common.extend.utils.SecurityUtils.getLoginUser;


@Slf4j
@Service
public class RecordServiceImpl implements IRecordService {

    @Autowired
    private MessageInvoker messageInvoker;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private RecordCacheManager recordCacheManager;

    @Autowired
    private ISipDeviceService sipDeviceService;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private IZmlHookService zmlHookService;

    @Autowired
    private ZlmApiUtils zlmApiUtils;

    @Autowired
    private RecordApiUtils recordApiUtils;

    @Autowired
    private ISipCmd sipCmd;

    @Autowired
    private IPlayService playService;

    @Autowired
    private IOssDetailService ossDetailService;

    @Override
    public RecordList listDevRecord(String deviceId, String channelId, String start, String end) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        if (dev != null) {
            String sn = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
            String recordkey = channelId + ":" + sn;
            recordCacheManager.addlock(recordkey);
            messageInvoker.recordInfoQuery(dev, sn, channelId, SipUtil.timestampToDate(start), SipUtil.timestampToDate(end));
            String catchkey = RedisKeyBuilder.buildSipRecordinfoCacheKey(recordkey);
            return (RecordList) messageInvoker.getExecResult(catchkey, SipUtil.DEFAULT_EXEC_TIMEOUT);
        }
        return null;
    }

    @Override
    public List<RecordItem> listRecord(String channelId, String sn) {
        String recordkey = channelId + ":" + sn;
        String catchkey = RedisKeyBuilder.buildSipRecordinfoCacheKey(recordkey);
        List<RecordItem> items = redisCache.getCacheList(catchkey);
        if (items.size() > 1) {
            items.sort(Comparator.naturalOrder());
        }
        return items;
    }

    @Override
    public JSONObject listServerRecord(String recordApi, Integer pageNum, Integer pageSize) {
        return recordApiUtils.getRecordlist(recordApi, pageNum, pageSize, null).getJSONObject("data");
    }

    @Override
    public JSONArray listServerRecordByDate(String recordApi, Integer year, Integer month, String app, String stream) {
        return recordApiUtils.getRecordDatelist(recordApi, year, month, app, stream, null).getJSONArray("data");
    }

    @Override
    public JSONObject listServerRecordByStream(String recordApi, Integer pageNum, Integer pageSize, String app) {
        return recordApiUtils.getRecordStreamlist(recordApi, pageNum, pageSize, app, null).getJSONObject("data");
    }

    @Override
    public JSONObject listServerRecordByApp(String recordApi, Integer pageNum, Integer pageSize) {
        return recordApiUtils.getRecordApplist(recordApi, pageNum, pageSize, null).getJSONObject("data");
    }

    @Override
    public JSONObject listServerRecordByFile(String recordApi, Integer pageNum, Integer pageSize, String app, String stream, String startTime, String endTime) {
        return recordApiUtils.getRecordFilelist(recordApi, pageNum, pageSize, app, stream, startTime, endTime, null).getJSONObject("data");
    }

    @Override
    public JSONObject listServerRecordByDevice(Integer pageNum, Integer pageSize, String deviceId, String channelId, String startTime, String endTime) {
        String playrsid = String.format("%s_%s_%s", SipUtil.PREFIX_PLAYRECORD, deviceId, channelId);
        MediaServer mediaServer = mediaServerService.selectMediaServerBydeviceSipId(deviceId);
        String recordApi = "";
        if (mediaServer != null && Objects.equals(mediaServer.getProtocol(), "http")) {
            recordApi = "http://" + mediaServer.getIp() + ":" + mediaServer.getRecordPort();
        } else if (mediaServer != null && Objects.equals(mediaServer.getProtocol(), "https")) {
            recordApi = "https://" + mediaServer.getDomainAlias() + ":" + mediaServer.getRecordPort();
        }
        JSONObject obj = recordApiUtils.getRecordFilelist(recordApi, pageNum, pageSize, "rtp",
                playrsid, startTime, endTime, null);
        if (obj != null) {
            obj = obj.getJSONObject("data");
            obj.put("recordApi", recordApi);
            log.info("obj:{}", obj);
        }
        return obj;
    }

    @Override
    public boolean startRecord(String stream) {
        SysUser user = getLoginUser().getUser();
        //缓存zlm服务器配置
        MediaServer media = mediaServerService.selectMediaServerBytenantId(user.getDept().getDeptUserId());
        if (media != null) {
            return zlmApiUtils.startRecord(media, "1", "live", stream).getBoolean("result");
        }
        return false;
    }

    @Override
    public boolean stopRecord(String stream) {
        SysUser user = getLoginUser().getUser();
        MediaServer media = mediaServerService.selectMediaServerBytenantId(user.getDept().getDeptUserId());
        ;
        if (media != null) {
            return zlmApiUtils.stopRecord(media, "1", "live", stream).getBoolean("result");
        }
        return false;
    }

    @Override
    public boolean isRecording(String stream) {
        SysUser user = getLoginUser().getUser();
        MediaServer media = mediaServerService.selectMediaServerBytenantId(user.getDept().getDeptUserId());
        ;
        if (media != null) {
            return zlmApiUtils.isRecording(media, "1", "live", stream).getBoolean("status");
        }
        return false;
    }

    @Override
    public JSONObject getMp4RecordFile(String stream, String period) {
        SysUser user = getLoginUser().getUser();
        MediaServer media = mediaServerService.selectMediaServerBytenantId(user.getDept().getDeptUserId());
        if (media != null) {
            return zlmApiUtils.getMp4RecordFile(media, period, "live", stream).getJSONObject("data");
        }
        return null;
    }

    @Override
    public Stream download(String deviceId, String channelId, String startTime, String endTime, int downloadSpeed) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        VideoSessionInfo info = sipCmd.downloadStreamCmd(dev, channelId, startTime, endTime, downloadSpeed);
        return zmlHookService.updateStream(info);
    }

    @Override
    public JSONObject upload(String recordApi, String file) {
        JSONObject obj = recordApiUtils.uploadOss(recordApi, file, null).getJSONObject("data");
        if (obj != null) {
            JSONObject ossObj = obj.getJSONObject("data");
            if (ossObj != null) {
                OssDetail ossDetail = OssDetail.builder()
                        .fileName(ossObj.getString("fileName"))
                        .fileSuffix(ossObj.getString("fileSuffix"))
                        .url(ossObj.getString("url"))
                        .originalName(ossObj.getString("originalName"))
                        .service(ossObj.getString("service"))
                        .build();
                ossDetailService.insertOssDetail(ossDetail);
            }
        }
        return obj;
    }

    @Override
    public Stream playRecord(String deviceId, String channelId) {
        return playService.play(deviceId, channelId, true);
    }
}
