package com.sydh.iot.model;

import com.sydh.iot.domain.DeviceLog;
import lombok.Data;

import java.util.List;

@Data
public class DeviceReport {
    String serialNumber;
    List<DeviceLog> reportList;
    List<String> unReportList;
}
