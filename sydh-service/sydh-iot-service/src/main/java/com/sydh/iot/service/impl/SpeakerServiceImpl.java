package com.sydh.iot.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.http.HttpUtils;
import com.sydh.iot.domain.Device;
import com.sydh.iot.mapper.SpeakerMapper;
import com.sydh.iot.model.speaker.DuerosReportVO;
import com.sydh.iot.model.speaker.OauthAccessTokenReportVO;
import com.sydh.iot.model.speaker.OauthClientDetailsReportVO;
import com.sydh.iot.service.IDeviceService;
import com.sydh.iot.service.SpeakerService;
import com.sydh.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author fastb
 * @date 2023-12-07 9:47
 */
@Service
@Slf4j
public class SpeakerServiceImpl implements SpeakerService {

    @Resource
    private SpeakerMapper speakerMapper;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private IDeviceService deviceService;

    /**
     * 上报物模型值给小度
     * @param serialNumber 设备
     * @param identifierList 物模型标识
     */
    @Override
    public void reportDuerosAttribute(String serialNumber, List<String> identifierList) throws IOException {
        OauthClientDetailsReportVO oauthClientDetailsReportVO = speakerMapper.selectOauthClientDetailsByType(1);
        if (oauthClientDetailsReportVO == null || StringUtils.isEmpty(oauthClientDetailsReportVO.getCloudSkillId())) {
            return;
        }
        Device device = deviceService.selectDeviceBySerialNumber(serialNumber);
        if (device == null) {
            return;
        }
        // 获取用户id
        SysUser sysUser = sysUserService.selectUserById(device.getTenantId());
        if (sysUser == null) {
            return;
        }
        // 获取openId
        OauthAccessTokenReportVO oauthAccessToken = speakerMapper.selectOauthAccessTokenByUserNameAndClientId(sysUser.getUserName(), oauthClientDetailsReportVO.getClientId());
        if (oauthAccessToken == null || StringUtils.isEmpty(oauthAccessToken.getOpenId())) {
            return;
        }
        // 获取关联属性
        Long relatedId;
        relatedId = speakerMapper.selectRelatedIdByProductIdAndUserId(device.getProductId(), device.getTenantId());
        if (relatedId == null) {
            relatedId = speakerMapper.selectRelatedIdByProductIdAndUserId(device.getProductId(), device.getTenantId());
        }
        if (relatedId == null) {
            return;
        }
        List<String> attributes = speakerMapper.listAttributesByRelatedIdAndIdentifier(relatedId, identifierList);
        // 上报
        for (String attribute : attributes) {
            DuerosReportVO.DiscoveryHeader header = new DuerosReportVO.DiscoveryHeader();
            header.setNamespace("DuerOS.ConnectedHome.Control");
            header.setName("ChangeReportRequest");
            DuerosReportVO.DiscoveryPayload payload = new DuerosReportVO.DiscoveryPayload();
            payload.setBotId(oauthClientDetailsReportVO.getCloudSkillId());
            payload.setOpenUid(oauthAccessToken.getOpenId());
            DuerosReportVO.DiscoveryPayload.DiscoveredAppliance discoveredAppliance = new DuerosReportVO.DiscoveryPayload.DiscoveredAppliance();
            discoveredAppliance.setApplianceId(device.getDeviceId().toString());
            discoveredAppliance.setAttributeName(attribute);
            payload.setAppliance(discoveredAppliance);
            DuerosReportVO reportVO = DuerosReportVO.builder()
                    .header(header)
                    .payload(payload).build();
            String s = HttpUtils.sendJsonPost("https://xiaodu.baidu.com/saiya/smarthome/changereport" , JSON.toJSONString(reportVO));
            JSONObject jsonObject = JSONObject.parseObject(s);
            if (!"0".equals(jsonObject.get("status").toString())) {
                log.info("上报属性给小度音箱出错：{}", s);
            }
            System.out.println(s);
        }
    }
}
