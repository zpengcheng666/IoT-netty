package com.sydh.common.core.domain;

import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.enums.LCType;
import com.sydh.common.utils.license.os.AbstractServerInfo;
import com.sydh.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class LCModel implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(LCModel.class);

    private Long type;
    private List<String> region;
    private String company;
    private List<String> ip;
    private List<String> mac;
    private String cpuSerial;
    private String boardSerial;

    // Getter and Setter
    public Long getType() { return type; }
    public void setType(Long type) { this.type = type; }

    public List<String> getRegion() { return region; }
    public void setRegion(List<String> region) { this.region = region; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public List<String> getIp() { return ip; }
    public void setIp(List<String> ip) { this.ip = ip; }

    public List<String> getMac() { return mac; }
    public void setMac(List<String> mac) { this.mac = mac; }

    public String getCpuSerial() { return cpuSerial; }
    public void setCpuSerial(String cpuSerial) { this.cpuSerial = cpuSerial; }

    public String getBoardSerial() { return boardSerial; }
    public void setBoardSerial(String boardSerial) { this.boardSerial = boardSerial; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LCModel)) return false;

        LCModel that = (LCModel) o;

        if (!getType().equals(that.getType())) return false;
        if (getRegion() != null ? !getRegion().equals(that.getRegion()) : that.getRegion() != null) return false;
        if (getCompany() != null ? !getCompany().equals(that.getCompany()) : that.getCompany() != null) return false;
        if (getIp() != null ? !getIp().equals(that.getIp()) : that.getIp() != null) return false;
        if (getMac() != null ? !getMac().equals(that.getMac()) : that.getMac() != null) return false;
        if (getCpuSerial() != null ? !getCpuSerial().equals(that.getCpuSerial()) : that.getCpuSerial() != null)
            return false;
        return getBoardSerial() != null ? getBoardSerial().equals(that.getBoardSerial()) : that.getBoardSerial() == null;
    }

    @Override
    public int hashCode() {
        int result = getType().hashCode();
        result = 31 * result + (getRegion() != null ? getRegion().hashCode() : 0);
        result = 31 * result + (getCompany() != null ? getCompany().hashCode() : 0);
        result = 31 * result + (getIp() != null ? getIp().hashCode() : 0);
        result = 31 * result + (getMac() != null ? getMac().hashCode() : 0);
        result = 31 * result + (getCpuSerial() != null ? getCpuSerial().hashCode() : 0);
        result = 31 * result + (getBoardSerial() != null ? getBoardSerial().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LCModel{" +
                "type=" + type +
                ", region=" + region +
                ", company='" + company + '\'' +
                ", ip=" + ip +
                ", mac=" + mac +
                ", cpuSerial='" + cpuSerial + '\'' +
                ", boardSerial='" + boardSerial + '\'' +
                '}';
    }

    public static LCModel getServerInfo(AbstractServerInfo serverInfo) {
        LCModel model = new LCModel();
        try {
            List<String> ips = serverInfo.getIpAddress();
            if (StringUtils.isNotEmpty(ips)) {
                model.setIp(ips);
            }

            List<String> macs = serverInfo.getMacAddress();
            if (StringUtils.isNotEmpty(macs)) {
                model.setMac(macs);
            }

            List<String> regions = serverInfo.getCityCode();
            if (StringUtils.isNotEmpty(regions)) {
                model.setRegion(regions);
            }
        } catch (Exception e) {
            log.error("获取服务器硬件信息失败", e);
        }
        return model;
    }

    public static String getServerInfo(AbstractServerInfo serverInfo, Long type) {
        try {
            if (Objects.equals(type, LCType.ENTERPRISE.getType())) {
                return serverInfo.getServerRegion();
            }

            if (Objects.equals(type, LCType.PERSON.getType()) || Objects.equals(type, LCType.TRIAL.getType())) {
                JSONObject json = new JSONObject();
                json.put("ip", serverInfo.getIpAddress());
                json.put("mac", serverInfo.getMacAddress());
                return json.toJSONString();
            }

            return "";
        } catch (Exception e) {
            log.error("获取服务器硬件信息失败", e);
            return "";
        }
    }
}
