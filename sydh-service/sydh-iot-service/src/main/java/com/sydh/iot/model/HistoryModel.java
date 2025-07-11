package com.sydh.iot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author bill
 */
@Data
@AllArgsConstructor
public class HistoryModel {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    private String value;

    private String identify;

    private String moderName;
    public HistoryModel() {}
}
