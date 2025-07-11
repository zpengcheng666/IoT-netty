package com.sydh.http.ruleEngine;

import com.sydh.http.client.Amap;
import com.sydh.http.model.Location;
import com.sydh.http.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@Component
public class HttpclientNode {
    @Resource
    private Amap amap;

    public Result<Location> amapLocation(BigDecimal longitude, BigDecimal latitude) {
        Result<Location> result = amap.getLocation(longitude.toEngineeringString(), latitude.toEngineeringString());
        return result;
    }
}
