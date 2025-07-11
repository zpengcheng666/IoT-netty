package com.sydh.sip.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.annotation.DataScope;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.core.redis.RedisKeyBuilder;
import com.sydh.common.extend.aspectj.DataScopeAspect;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.utils.DateUtils;
import com.sydh.iot.domain.Device;
import com.sydh.iot.domain.Product;
import com.sydh.iot.domain.SipRelation;
import com.sydh.iot.service.IDeviceService;
import com.sydh.iot.service.IProductService;
import com.sydh.iot.service.impl.SipRelationServiceImpl;
import com.sydh.sip.convert.SipDeviceChannelConvert;
import com.sydh.sip.domain.BindingChannel;
import com.sydh.sip.domain.SipDevice;
import com.sydh.sip.domain.SipDeviceChannel;
import com.sydh.sip.enums.DeviceChannelStatus;
import com.sydh.sip.enums.SessionType;
import com.sydh.sip.mapper.SipDeviceChannelMapper;
import com.sydh.sip.mapper.SipDeviceMapper;
import com.sydh.sip.model.BaseTree;
import com.sydh.sip.model.Stream;
import com.sydh.sip.model.VideoSessionInfo;
import com.sydh.sip.model.vo.SipDeviceChannelVO;
import com.sydh.sip.server.VideoSessionManager;
import com.sydh.sip.service.IPlayService;
import com.sydh.sip.service.ISipDeviceChannelService;
import com.sydh.sip.util.SipUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.sydh.common.extend.utils.SecurityUtils.getLoginUser;

/**
 * 监控设备通道信息Service业务层处理
 *
 * @author zhuangpeng.li
 * @date 2022-10-07
 */
@Slf4j
@Service
public class SipDeviceChannelServiceImpl extends ServiceImpl<SipDeviceChannelMapper, SipDeviceChannel> implements ISipDeviceChannelService {
    @Resource
    private SipDeviceChannelMapper sipDeviceChannelMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private IProductService productService;

    @Resource
    private IDeviceService deviceService;

    @Resource
    private SipDeviceMapper sipDeviceMapper;

    @Resource
    private VideoSessionManager streamSession;

    @Resource
    private IPlayService playService;

    @Resource
    private SipRelationServiceImpl sipRelationService;

    @Override
    public void updateChannel(String deviceId, SipDeviceChannel channel) {
        SipDeviceChannel deviceChannel = sipDeviceChannelMapper.selectSipDeviceChannelByChannelSipId(channel.getChannelSipId());
        if (deviceChannel == null) {
            Device dev = deviceService.selectDeviceBySerialNumber(deviceId);
            if (dev != null) {
                insertChannelByDevice(dev, channel);
            } else {
                log.error("未注册设备：{}", deviceId);
            }
        } else {
            channel.setId(deviceChannel.getId());
            channel.setUpdateTime(DateUtils.getNowDate());
            sipDeviceChannelMapper.updateSipDeviceChannel(channel);
        }
    }

    /**
     * 查询监控设备通道信息
     *
     * @param channelId 监控设备通道信息主键
     * @return 监控设备通道信息
     */
    @Override
    public SipDeviceChannelVO selectSipDeviceChannelByChannelId(Long channelId) {
        SipDeviceChannel channel = sipDeviceChannelMapper.selectById(channelId);
        SipDeviceChannelVO sipDeviceChannelVO = SipDeviceChannelConvert.INSTANCE.convertSipDeviceChannelVO(channel);
        BindingChannel bindingChannel = this.getBindingChannel(sipDeviceChannelVO.getChannelSipId());
        sipDeviceChannelVO.setBindingChannel(bindingChannel);
        return sipDeviceChannelVO;
    }

    @Override
    public SipDeviceChannel selectSipDeviceChannelByChannelSipId(String channelSipId) {
        SipDeviceChannel sipDeviceChannel = new SipDeviceChannel();
        sipDeviceChannel.setChannelSipId(channelSipId);
        return sipDeviceChannelMapper.selectOne(buildQueryWrapper(sipDeviceChannel));
    }

    @Override
    public List<SipDeviceChannel> selectSipDeviceChannelByDeviceSipId(String deviceSipId) {
        SipDeviceChannel sipDeviceChannel = new SipDeviceChannel();
        sipDeviceChannel.setDeviceSipId(deviceSipId);
        return sipDeviceChannelMapper.selectList(buildQueryWrapper(sipDeviceChannel));
    }


    /**
     * 查询监控设备通道信息分页列表
     *
     * @param sipDeviceChannel 监控设备通道信息
     * @return 监控设备通道信息
     */
    @Override
    @DataScope(fieldAlias = "sip")
    public Page<SipDeviceChannelVO> pageSipDeviceChannelVO(SipDeviceChannel sipDeviceChannel) {
        Page<SipDeviceChannel> sipDeviceChannelPage = sipDeviceChannelMapper.selectPage(new Page<>(sipDeviceChannel.getPageNum(), sipDeviceChannel.getPageSize()), buildQueryWrapper(sipDeviceChannel));
        if (0 == sipDeviceChannelPage.getTotal()) {
            return new Page<>();
        }
        Page<SipDeviceChannelVO> sipDeviceChannelVOPage = SipDeviceChannelConvert.INSTANCE.convertSipDeviceChannelVOPage(sipDeviceChannelPage);
        List<SipDeviceChannelVO> sipDeviceChannelVOS = sipDeviceChannelVOPage.getRecords();
        List<SipDeviceChannelVO> newList = new ArrayList<>();
        if (sipDeviceChannelVOS != null && !sipDeviceChannelVOS.isEmpty()) {
            for (SipDeviceChannelVO channel : sipDeviceChannelVOS) {
                // 新增关联数据查询逻辑
                SipRelation relation = sipRelationService.selectByChannelId(channel.getChannelSipId());
                if (relation != null) {
                    channel.setReDeviceId(relation.getReDeviceId());
                    channel.setReSceneModelId(relation.getReSceneModelId());
                }
                newList.add(updateChannelStatus(channel));
            }
        }
        return sipDeviceChannelVOPage;
    }

    private LambdaQueryWrapper<SipDeviceChannel> buildQueryWrapper(SipDeviceChannel query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SipDeviceChannel> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getTenantId() != null, SipDeviceChannel::getTenantId, query.getTenantId());
        lqw.eq(query.getProductId() != null, SipDeviceChannel::getProductId, query.getProductId());
        lqw.like(StringUtils.isNotBlank(query.getProductName()), SipDeviceChannel::getProductName, query.getProductName());
        lqw.eq(query.getDeviceSipId() != null, SipDeviceChannel::getDeviceSipId, query.getDeviceSipId());
        lqw.eq(StringUtils.isNotBlank(query.getChannelSipId()), SipDeviceChannel::getChannelSipId, query.getChannelSipId());
        lqw.like(StringUtils.isNotBlank(query.getChannelName()), SipDeviceChannel::getChannelName, query.getChannelName());
        lqw.eq(query.getRegisterTime() != null, SipDeviceChannel::getRegisterTime, query.getRegisterTime());
        lqw.eq(StringUtils.isNotBlank(query.getDeviceType()), SipDeviceChannel::getDeviceType, query.getDeviceType());
        lqw.eq(StringUtils.isNotBlank(query.getChannelType()), SipDeviceChannel::getChannelType, query.getChannelType());
        lqw.eq(StringUtils.isNotBlank(query.getCityCode()), SipDeviceChannel::getCityCode, query.getCityCode());
        lqw.eq(StringUtils.isNotBlank(query.getCivilCode()), SipDeviceChannel::getCivilCode, query.getCivilCode());
        lqw.eq(StringUtils.isNotBlank(query.getManufacture()), SipDeviceChannel::getManufacture, query.getManufacture());
        lqw.eq(StringUtils.isNotBlank(query.getModel()), SipDeviceChannel::getModel, query.getModel());
        lqw.eq(StringUtils.isNotBlank(query.getOwner()), SipDeviceChannel::getOwner, query.getOwner());
        lqw.eq(StringUtils.isNotBlank(query.getBlock()), SipDeviceChannel::getBlock, query.getBlock());
        lqw.eq(StringUtils.isNotBlank(query.getAddress()), SipDeviceChannel::getAddress, query.getAddress());
        lqw.eq(StringUtils.isNotBlank(query.getParentId()), SipDeviceChannel::getParentId, query.getParentId());
        lqw.eq(StringUtils.isNotBlank(query.getIpAddress()), SipDeviceChannel::getIpAddress, query.getIpAddress());
        lqw.eq(query.getPort() != null, SipDeviceChannel::getPort, query.getPort());
        lqw.eq(StringUtils.isNotBlank(query.getPassword()), SipDeviceChannel::getPassword, query.getPassword());
        lqw.eq(query.getPtzType() != null, SipDeviceChannel::getPtzType, query.getPtzType());
        lqw.eq(StringUtils.isNotBlank(query.getPtzTypeText()), SipDeviceChannel::getPtzTypeText, query.getPtzTypeText());
        lqw.eq(query.getStatus() != null, SipDeviceChannel::getStatus, query.getStatus());
        lqw.eq(query.getLongitude() != null, SipDeviceChannel::getLongitude, query.getLongitude());
        lqw.eq(query.getLatitude() != null, SipDeviceChannel::getLatitude, query.getLatitude());
        lqw.eq(StringUtils.isNotBlank(query.getStreamId()), SipDeviceChannel::getStreamId, query.getStreamId());
        lqw.eq(query.getSubCount() != null, SipDeviceChannel::getSubCount, query.getSubCount());
        lqw.eq(query.getParental() != null, SipDeviceChannel::getParental, query.getParental());
        lqw.eq(query.getHasAudio() != null, SipDeviceChannel::getHasAudio, query.getHasAudio());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SipDeviceChannel::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    public SipDeviceChannelVO updateChannelStatus(SipDeviceChannelVO channel) {
        String playsid = String.format("%s_%s_%s", SipUtil.PREFIX_PLAY, channel.getDeviceSipId(), channel.getChannelSipId());
        String playrsid = String.format("%s_%s_%s", SipUtil.PREFIX_PLAYRECORD, channel.getDeviceSipId(), channel.getChannelSipId());
        VideoSessionInfo pinfo = streamSession.getSessionInfo(channel.getDeviceSipId(),
                channel.getChannelSipId(), playsid, SessionType.play.name());
        if (pinfo != null) {
            channel.setStreamPush(pinfo.isPushing() ? 1 : 0);
        }
        VideoSessionInfo prinfo = streamSession.getSessionInfo(channel.getDeviceSipId(),
                channel.getChannelSipId(), playrsid, SessionType.playrecord.name());
        if (prinfo != null) {
            channel.setStreamRecord(prinfo.isRecording() ? 1 : 0);
        }
        return channel;
    }

    /**
     * 新增监控设备通道信息
     *
     * @param sipDeviceChannel 监控设备通道信息
     * @return 结果
     */
    @Override
    public String insertSipDeviceChannelGen(Long createNum, SipDeviceChannel sipDeviceChannel) {
        String newDevID = sipDeviceChannel.getDeviceSipId() + this.getDeviceSipId(sipDeviceChannel.getDeviceSipId());
        sipDeviceChannel.setDeviceSipId(newDevID);
        sipDeviceChannel.setStatus(DeviceChannelStatus.notused.getValue());
        this.addChannelSipId(createNum, sipDeviceChannel, null);
        return newDevID;
    }

    private String getDeviceSipId(String deviceSipId) {
        Integer maxIndex = 0;
        String cacheKey = RedisKeyBuilder.buildSipDeviceidCacheKey(deviceSipId);
        Set<Integer> indexSet = redisCache.getCacheSet(cacheKey);
        if (indexSet != null && !indexSet.isEmpty()) {
            maxIndex = Collections.max(indexSet);
        } else {
            indexSet = new TreeSet<>();
        }
        // 更新set到缓存
        try {
            maxIndex++;
            indexSet.add(maxIndex);
            redisCache.setCacheSet(cacheKey, indexSet);
        } catch (Exception e) {
            log.warn("Failed to update Redis cache", e);
        }
        return String.format("%06d", maxIndex);
    }

    private int addChannelSipId(Long createNum, SipDeviceChannel sipDeviceChannel, Integer sIndex) {
        // 限制最大添加数量为10
        if (createNum > 10) {
            createNum = 10L;
        }
        int created = 0;
        Set<Integer> indexSet = new TreeSet<>();
        ;
        String baseChannelId = sipDeviceChannel.getChannelSipId();
        String cacheKey = RedisKeyBuilder.buildSipChannelidCacheKey(baseChannelId);
        if (sIndex == null) {
            indexSet = redisCache.getCacheSet(cacheKey);
            if (indexSet != null && !indexSet.isEmpty()) {
                sIndex = Collections.max(indexSet);
            } else {
                sIndex = 0;
            }
        }

        for (int i = 1; created < createNum; i++) {
            Integer cIndex = sIndex + i;
            String newChannelId = baseChannelId + String.format("%06d", cIndex);
            SipDeviceChannel existingChannel = this.selectSipDeviceChannelByChannelSipId(newChannelId);
            if (existingChannel == null) {
                try {
                    sipDeviceChannel.setChannelSipId(newChannelId);
                    int ret = insertSipDeviceChannel(sipDeviceChannel);
                    if (ret > 0) {
                        created++;
                        indexSet.add(cIndex);
                    }
                } catch (Exception e) {
                    // Handle insertion failure
                    log.warn("Failed to insert SipDeviceChannel: {}", e.getMessage());
                }
            }
        }
        redisCache.setCacheSet(cacheKey, indexSet);
        return created;
    }

    @Override
    public int insertSipDeviceChannel(SipDeviceChannel sipDeviceChannel) {
        Product product = productService.getProductBySerialNumber(sipDeviceChannel.getDeviceSipId());
        if (product != null) {
            sipDeviceChannel.setProductId(product.getProductId());
            sipDeviceChannel.setProductName(product.getProductName());
        }
        sipDeviceChannel.setTenantId(getLoginUser().getDeptId());
        SysUser user = getLoginUser().getUser();
        sipDeviceChannel.setTenantId(getLoginUser().getDeptUserId());
        sipDeviceChannel.setTenantName(user.getDept().getDeptName());
        sipDeviceChannel.setCreateBy(user.getUserName());
        sipDeviceChannel.setCreateTime(DateUtils.getNowDate());
        return sipDeviceChannelMapper.insert(sipDeviceChannel);
    }


    private int insertChannelByDevice(Device dev, SipDeviceChannel sipDeviceChannel) {
        Product product = productService.getProductBySerialNumber(sipDeviceChannel.getDeviceSipId());
        if (product != null) {
            sipDeviceChannel.setProductId(product.getProductId());
            sipDeviceChannel.setProductName(product.getProductName());
        }
        sipDeviceChannel.setTenantId(dev.getTenantId());
        sipDeviceChannel.setCreateTime(DateUtils.getNowDate());
        return sipDeviceChannelMapper.insert(sipDeviceChannel);
    }

    /**
     * 修改监控设备通道信息
     *
     * @param sipDeviceChannel 监控设备通道信息
     * @return 结果
     */
    @Override
    public int updateSipDeviceChannel(SipDeviceChannel sipDeviceChannel) {
        return sipDeviceChannelMapper.updateById(sipDeviceChannel);
    }

    @Override
    public int updateSipDeviceChannelStatus(String ChannelId, Integer status) {
        SipDeviceChannel sipDeviceChannel = this.selectSipDeviceChannelByChannelSipId(ChannelId);
        if (sipDeviceChannel != null) {
            if (sipDeviceChannel.getRegisterTime() == null && status == 2) {
                sipDeviceChannel.setRegisterTime(DateUtils.getNowDate());
            }
            sipDeviceChannel.setStatus(status);
            sipDeviceChannel.setUpdateTime(DateUtils.getNowDate());
            return sipDeviceChannelMapper.updateById(sipDeviceChannel);
        }
        return 0;
    }

    /**
     * 批量删除监控设备通道信息
     *
     * @param channelIds 需要删除的监控设备通道信息主键
     * @return 结果
     */
    @Override
    public int deleteSipDeviceChannelByChannelIds(Long[] channelIds) {
        return sipDeviceChannelMapper.deleteBatchIds(Arrays.asList(channelIds));
    }

    /**
     * 删除监控设备通道信息信息
     *
     * @param channelId 监控设备通道信息主键
     * @return 结果
     */
    @Override
    public int deleteSipDeviceChannelByChannelId(Long channelId) {
        return sipDeviceChannelMapper.deleteById(channelId);
    }

    @Override
    public List<BaseTree<SipDeviceChannel>> queryVideoDeviceTree(String deviceId, String parentId, boolean onlyCatalog) {
        SipDevice device = sipDeviceMapper.selectSipDeviceBySipId(deviceId);
        if (device == null) {
            return null;
        }
        if (parentId == null || parentId.equals(deviceId)) {
            // 字根节点开始查询
            List<SipDeviceChannel> rootNodes = getRootNodes(deviceId, true, !onlyCatalog);
            return transportChannelsToTree(rootNodes, "");
        }

        if (parentId.length() % 2 != 0) {
            return null;
        }
        if (parentId.length() == 10) {
            if (onlyCatalog) {
                return null;
            }
            // parentId为行业编码， 其下不会再有行政区划
            List<SipDeviceChannel> channels = sipDeviceChannelMapper.selectChannelByCivilCode(deviceId, parentId);
            return transportChannelsToTree(channels, parentId);
        }
        // 查询其下的行政区划和摄像机
        List<SipDeviceChannel> channelsForCivilCode = sipDeviceChannelMapper.selectChannelWithCivilCodeAndLength(deviceId, parentId, parentId.length() + 2);
        if (!onlyCatalog) {
            List<SipDeviceChannel> channels = sipDeviceChannelMapper.selectChannelByCivilCode(deviceId, parentId);

            for (SipDeviceChannel channel : channels) {
                boolean flag = false;
                for (SipDeviceChannel deviceChannel : channelsForCivilCode) {
                    if (channel.getChannelSipId().equals(deviceChannel.getChannelSipId())) {
                        flag = true;
                    }
                }
                if (!flag) {
                    channelsForCivilCode.add(channel);
                }
            }
        }
        return transportChannelsToTree(channelsForCivilCode, parentId);
    }


    /**
     * 根据channelId获取绑定的设备或场景
     *
     * @param channelId
     * @return
     */
    @Override
    public BindingChannel getBindingChannel(String channelId) {
        return sipDeviceChannelMapper.getBindingChannel(channelId);
    }

    @Override
    public List<SipDeviceChannelVO> listRelDeviceOrScene(String serialNumber, Long sceneModelId) {
        List<SipDeviceChannel> sipDeviceChannelList = new ArrayList<>();
        if (StringUtils.isNotEmpty(serialNumber)) {
            sipDeviceChannelList = sipDeviceChannelMapper.selectDeviceRelSipDeviceChannelList(serialNumber);
        }
        if (null != sceneModelId) {
            sipDeviceChannelList = sipDeviceChannelMapper.selectSceneRelSipDeviceChannelList(sceneModelId);
        }
        List<SipDeviceChannelVO> sipDeviceChannelVOS = SipDeviceChannelConvert.INSTANCE.convertSipDeviceChannelVOList(sipDeviceChannelList);
        for (SipDeviceChannelVO sipDeviceChannel : sipDeviceChannelVOS) {
            Stream play = playService.play(String.valueOf(sipDeviceChannel.getDeviceSipId()), sipDeviceChannel.getChannelSipId(), false);
            if (null != play) {
                sipDeviceChannel.setPlayUrl(play.getPlayurl());
            }
        }
        return sipDeviceChannelVOS;
    }


    private List<SipDeviceChannel> getRootNodes(String deviceId, boolean haveCatalog, boolean haveChannel) {
        if (!haveCatalog && !haveChannel) {
            return null;
        }
        List<SipDeviceChannel> result = new ArrayList<>();
        // 使用行政区划
        Integer length = sipDeviceChannelMapper.getChannelMinLength(deviceId);
        if (length == null) {
            return null;
        }
        if (length <= 10) {
            if (haveCatalog) {
                List<SipDeviceChannel> provinceNode = sipDeviceChannelMapper.selectChannelWithCivilCodeAndLength(deviceId, null, length);
                if (provinceNode != null && !provinceNode.isEmpty()) {
                    result.addAll(provinceNode);
                }
            }
            if (haveChannel) {
                // 查询那些civilCode不在通道中的不规范通道，放置在根目录
                List<SipDeviceChannel> nonstandardNode = sipDeviceChannelMapper.selectChannelWithoutCiviCode(deviceId);
                if (nonstandardNode != null && !nonstandardNode.isEmpty()) {
                    result.addAll(nonstandardNode);
                }
            }
        } else {
            if (haveChannel) {
                List<SipDeviceChannel> deviceChannels = sipDeviceChannelMapper.selectSipDeviceChannelByDeviceSipId(deviceId);
                if (deviceChannels != null && !deviceChannels.isEmpty()) {
                    result.addAll(deviceChannels);
                }
            }
        }
        return result;
    }

    private List<BaseTree<SipDeviceChannel>> transportChannelsToTree(List<SipDeviceChannel> channels, String parentId) {
        if (channels == null) {
            return null;
        }
        List<BaseTree<SipDeviceChannel>> treeNotes = new ArrayList<>();
        if (channels.isEmpty()) {
            return treeNotes;
        }
        for (SipDeviceChannel channel : channels) {
            BaseTree<SipDeviceChannel> node = new BaseTree<>();
            node.setId(channel.getChannelSipId());
            node.setDeviceId(channel.getDeviceSipId());
            node.setName(channel.getChannelName());
            node.setPid(parentId);
            node.setBasicData(channel);
            node.setParent(false);
            if (channel.getChannelSipId().length() > 8) {
                if (channel.getChannelSipId().length() > 13) {
                    String gbCodeType = channel.getChannelSipId().substring(10, 13);
                    node.setParent(gbCodeType.equals(SipUtil.BUSINESS_GROUP) || gbCodeType.equals(SipUtil.VIRTUAL_ORGANIZATION));
                }
            } else {
                node.setParent(true);
            }
            treeNotes.add(node);
        }
        Collections.sort(treeNotes);
        return treeNotes;
    }
}
