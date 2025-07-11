package com.sydh.sip.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.sydh.sip.model.RecordItem;
import com.sydh.sip.model.RecordList;
import com.sydh.sip.model.Stream;

import java.util.List;

public interface IRecordService {
    RecordList listDevRecord(String deviceId, String channelId, String startTime, String endTime);
    List<RecordItem> listRecord(String channelId, String sn);

    JSONObject listServerRecord(String recordApi, Integer pageNum, Integer pageSize);
    JSONArray listServerRecordByDate(String recordApi, Integer year, Integer month, String app, String stream);
    JSONObject listServerRecordByStream(String recordApi, Integer pageNum, Integer pageSize, String app);
    JSONObject listServerRecordByApp(String recordApi, Integer pageNum, Integer pageSize);
    JSONObject listServerRecordByFile(String recordApi, Integer pageNum, Integer pageSize, String app, String stream, String startTime, String endTime);
    JSONObject listServerRecordByDevice(Integer pageNum, Integer pageSize, String deviceId, String channelId, String startTime, String endTime);
    boolean startRecord(String stream);
    boolean stopRecord(String stream);
    boolean isRecording(String stream);
    JSONObject getMp4RecordFile(String stream,String period);
    Stream download(String deviceId, String channelId,
                    String startTime, String endTime, int downloadSpeed);
    JSONObject upload(String recordApi, String file);
    Stream playRecord(String deviceId, String channelId);
}
