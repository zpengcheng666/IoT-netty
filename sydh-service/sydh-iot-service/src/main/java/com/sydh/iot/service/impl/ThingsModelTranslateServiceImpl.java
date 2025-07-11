package com.sydh.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.domain.ThingsModelTranslate;
import com.sydh.iot.mapper.ThingsModelTranslateMapper;
import com.sydh.iot.service.IThingsModelTranslateService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

/**
 * 物模型翻译Service业务层处理
 *
 * @author zhuangpeng.li
 * @date 2024-12-24
 */
@Service
public class ThingsModelTranslateServiceImpl extends ServiceImpl<ThingsModelTranslateMapper,ThingsModelTranslate> implements IThingsModelTranslateService {

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询物模型翻译
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 物模型翻译
     */
    @Override
    @Cacheable(cacheNames = "ThingsModelTranslate", key = "#id")
    public ThingsModelTranslate queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询物模型翻译
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 物模型翻译
     */
    @Override
    @Cacheable(cacheNames = "ThingsModelTranslate", key = "#id")
    public ThingsModelTranslate selectThingsModelTranslateById(Long id){
        return this.getById(id);
    }


    private LambdaQueryWrapper<ThingsModelTranslate> buildQueryWrapper(ThingsModelTranslate query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ThingsModelTranslate> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getId() != null, ThingsModelTranslate::getId, query.getId());
                    lqw.eq(StringUtils.isNotBlank(query.getZhCn()), ThingsModelTranslate::getZhCn, query.getZhCn());
                    lqw.eq(StringUtils.isNotBlank(query.getEnUs()), ThingsModelTranslate::getEnUs, query.getEnUs());
                    lqw.eq(query.getProductId() != null, ThingsModelTranslate::getProductId, query.getProductId());
        return lqw;
    }

    /**
     * 新增物模型翻译
     *
     * @param add 物模型翻译
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(ThingsModelTranslate add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改物模型翻译
     *
     * @param update 物模型翻译
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "ThingsModelTranslate", key = "#update.id")
    public Boolean updateWithCache(ThingsModelTranslate update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ThingsModelTranslate entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除物模型翻译信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "ThingsModelTranslate", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
