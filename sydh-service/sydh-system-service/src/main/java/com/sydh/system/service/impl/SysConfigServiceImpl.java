package com.sydh.system.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.constant.CacheConstants;
import com.sydh.common.constant.UserConstants;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.core.text.Convert;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.utils.StringUtils;
import com.sydh.system.convert.SysConfigConvert;
import com.sydh.system.domain.SysConfig;
import com.sydh.system.domain.vo.SysConfigVO;
import com.sydh.system.mapper.SysConfigMapper;
import com.sydh.system.service.ISysConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 参数配置 服务层实现
 *
 * @author ruoyi
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper,SysConfig> implements ISysConfigService
{
    @Resource
    private SysConfigMapper configMapper;

    @Resource
    private RedisCache redisCache;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init()
    {
        loadingConfigCache();
    }

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    @DS("master")
    public SysConfig selectConfigById(Long configId)
    {
        return configMapper.selectById(configId);
//        return configMapper.selectConfig(config);
    }

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey)
    {
        String configValue = Convert.toStr(redisCache.getCacheObject(getCacheKey(configKey)));
        if (StringUtils.isNotEmpty(configValue))
        {
            return configValue;
        }
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        SysConfig retConfig = configMapper.selectOne(buildQueryWrapper(config));
        if (StringUtils.isNotNull(retConfig))
        {
            redisCache.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取验证码开关
     *
     * @return true开启，false关闭
     */
    @Override
    public boolean selectCaptchaEnabled()
    {
        // 开源版本：禁用验证码功能，直接返回false
        return false;
        
        /* 原始逻辑 - 已禁用
        String captchaEnabled = selectConfigByKey("sys.account.captchaEnabled");
        if (StringUtils.isEmpty(captchaEnabled))
        {
            return true;
        }
        return Convert.toBool(captchaEnabled);
        */
    }

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config)
    {
        LambdaQueryWrapper<SysConfig> wrapper = buildQueryWrapper(config);
        return configMapper.selectList(wrapper);
    }

    /**
     * 查询参数配置分页列表
     *
     * @param sysConfig 参数配置
     * @return 参数配置
     */
    @Override
    public Page<SysConfigVO> pageSysConfigVO(SysConfig sysConfig) {
        LambdaQueryWrapper<SysConfig> lqw = buildQueryWrapper(sysConfig);
        Page<SysConfig> sysConfigPage = baseMapper.selectPage(new Page<>(sysConfig.getPageNum(), sysConfig.getPageSize()), lqw);
        return SysConfigConvert.INSTANCE.convertSysConfigVOPage(sysConfigPage);
    }

    private LambdaQueryWrapper<SysConfig> buildQueryWrapper(SysConfig query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SysConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getConfigId() != null, SysConfig::getConfigId, query.getConfigId());
        lqw.like(StringUtils.isNotBlank(query.getConfigName()), SysConfig::getConfigName, query.getConfigName());
        lqw.like(StringUtils.isNotBlank(query.getConfigKey()), SysConfig::getConfigKey, query.getConfigKey());
        lqw.eq(StringUtils.isNotBlank(query.getConfigValue()), SysConfig::getConfigValue, query.getConfigValue());
        lqw.eq(StringUtils.isNotBlank(query.getConfigType()), SysConfig::getConfigType, query.getConfigType());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), SysConfig::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, SysConfig::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), SysConfig::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, SysConfig::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), SysConfig::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SysConfig::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config)
    {
        int row = configMapper.insert(config);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config)
    {
        SysConfig temp = configMapper.selectById(config.getConfigId());
        if (!StringUtils.equals(temp.getConfigKey(), config.getConfigKey()))
        {
            redisCache.deleteObject(getCacheKey(temp.getConfigKey()));
        }

        int row = configMapper.updateById(config);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(Long[] configIds)
    {
        for (Long configId : configIds)
        {
            SysConfig config = selectConfigById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType()))
            {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            configMapper.deleteById(configId);
            redisCache.deleteObject(getCacheKey(config.getConfigKey()));
        }
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache()
    {
        List<SysConfig> configsList = this.selectConfigList(new SysConfig());
        for (SysConfig config : configsList)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache()
    {
        Collection<String> keys = redisCache.keys(CacheConstants.SYS_CONFIG_KEY + "*");
        redisCache.deleteObject(keys);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache()
    {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public String checkConfigKeyUnique(SysConfig config)
    {
        Long configId = StringUtils.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        LambdaQueryWrapper<SysConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysConfig::getConfigKey, config.getConfigKey());
        SysConfig info = configMapper.selectOne(lqw);
        if (StringUtils.isNotNull(info) && info.getConfigId().longValue() != configId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey)
    {
        return CacheConstants.SYS_CONFIG_KEY + configKey;
    }
}
