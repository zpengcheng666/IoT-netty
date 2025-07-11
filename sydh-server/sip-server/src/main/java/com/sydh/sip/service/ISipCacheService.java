package com.sydh.sip.service;

import com.sydh.sip.model.RecordList;
import com.sydh.sip.model.ZlmMediaServer;

public interface ISipCacheService {
    Long getCSEQ(String serverSipId);

    void updateMediaInfo(ZlmMediaServer mediaServerConfig);

    void setRecordList(String key, RecordList recordList);
}
