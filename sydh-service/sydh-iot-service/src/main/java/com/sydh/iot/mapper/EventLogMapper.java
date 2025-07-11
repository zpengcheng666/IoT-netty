package com.sydh.iot.mapper;

import java.util.List;
import com.sydh.common.mybatis.mapper.BaseMapperX;
import com.sydh.iot.domain.Device;
import com.sydh.iot.domain.EventLog;
import com.sydh.iot.model.HistoryModel;
import com.sydh.iot.model.vo.EventLogVO;
import org.apache.ibatis.annotations.Param;

/**
 * 事件日志Mapper接口
 *
 * @author kerwincui
 * @date 2024-08-16
 */
public interface EventLogMapper extends BaseMapperX<EventLog>
{

    /**
     * 查询物模型历史数据
     * @param eventLogVO 事件日志
     * @return java.util.List<com.sydh.iot.model.HistoryModel>
     */
    List<HistoryModel> listHistory(EventLogVO eventLogVO);

    Long selectEventLogCount(@Param("device") Device device);

}
