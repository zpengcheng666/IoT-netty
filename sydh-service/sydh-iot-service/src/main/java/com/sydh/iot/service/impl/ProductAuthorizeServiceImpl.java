package com.sydh.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.uuid.IdUtils;
import com.sydh.iot.convert.ProductAuthorizeConvert;
import com.sydh.iot.domain.ProductAuthorize;
import com.sydh.iot.mapper.ProductAuthorizeMapper;
import com.sydh.iot.model.vo.ProductAuthorizeVO;
import com.sydh.iot.service.IProductAuthorizeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static com.sydh.common.extend.utils.SecurityUtils.getLoginUser;
import static com.sydh.common.extend.utils.SecurityUtils.getUsername;


/**
 * 产品授权码Service业务层处理
 *
 * @author kami
 * @date 2022-04-11
 */
@Service
public class ProductAuthorizeServiceImpl extends ServiceImpl<ProductAuthorizeMapper,ProductAuthorize> implements IProductAuthorizeService {
    @Resource
    private ProductAuthorizeMapper productAuthorizeMapper;

    /**
     * 查询产品授权码
     *
     * @param authorizeId 产品授权码主键
     * @return 产品授权码
     */
    @Override
    public ProductAuthorize selectProductAuthorizeByAuthorizeId(Long authorizeId) {
        return productAuthorizeMapper.selectById(authorizeId);
    }


    /**
     * 查询产品授权码分页列表
     *
     * @param productAuthorize 产品授权码
     * @return 产品授权码
     */
    @Override
    public Page<ProductAuthorizeVO> pageProductAuthorizeVO(ProductAuthorize productAuthorize) {
        LambdaQueryWrapper<ProductAuthorize> lqw = buildQueryWrapper(productAuthorize);
        lqw.orderByDesc(ProductAuthorize::getCreateTime);
        Page<ProductAuthorize> productAuthorizePage = baseMapper.selectPage(new Page<>(productAuthorize.getPageNum(), productAuthorize.getPageSize()), lqw);
        return ProductAuthorizeConvert.INSTANCE.convertProductAuthorizeVOPage(productAuthorizePage);
    }

    private LambdaQueryWrapper<ProductAuthorize> buildQueryWrapper(ProductAuthorize query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ProductAuthorize> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getAuthorizeId() != null, ProductAuthorize::getAuthorizeId, query.getAuthorizeId());
        lqw.eq(StringUtils.isNotBlank(query.getAuthorizeCode()), ProductAuthorize::getAuthorizeCode, query.getAuthorizeCode());
        lqw.eq(query.getProductId() != null, ProductAuthorize::getProductId, query.getProductId());
        lqw.eq(query.getDeviceId() != null, ProductAuthorize::getDeviceId, query.getDeviceId());
        lqw.eq(StringUtils.isNotBlank(query.getSerialNumber()), ProductAuthorize::getSerialNumber, query.getSerialNumber());
        lqw.eq(query.getUserId() != null, ProductAuthorize::getUserId, query.getUserId());
        lqw.like(StringUtils.isNotBlank(query.getUserName()), ProductAuthorize::getUserName, query.getUserName());
        lqw.eq(query.getStatus() != null, ProductAuthorize::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getDelFlag()), ProductAuthorize::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), ProductAuthorize::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, ProductAuthorize::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), ProductAuthorize::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, ProductAuthorize::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), ProductAuthorize::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(ProductAuthorize::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增产品授权码
     *
     * @param productAuthorize 产品授权码
     * @return 结果
     */
    @Override
    public int insertProductAuthorize(ProductAuthorize productAuthorize) {
        // 1=未使用，2=使用中
        productAuthorize.setStatus(1);
        productAuthorize.setCreateTime(DateUtils.getNowDate());
        productAuthorize.setCreateBy(getUsername());
        return productAuthorizeMapper.insert(productAuthorize);
    }

    /**
     * 修改产品授权码
     *
     * @param productAuthorize 产品授权码
     * @return 结果
     */
    @Override
    public int updateProductAuthorize(ProductAuthorize productAuthorize) {
        if(productAuthorize.getDeviceId()!=null && productAuthorize.getDeviceId()!=0){
            // 1=未使用，2=使用中
            productAuthorize.setStatus(2);
            productAuthorize.setUpdateTime(DateUtils.getNowDate());
        }
        productAuthorize.setUpdateBy(getUsername());
        return productAuthorizeMapper.updateById(productAuthorize);
    }

    /**
     * 批量删除产品授权码
     *
     * @param authorizeIds 需要删除的产品授权码主键
     * @return 结果
     */
    @Override
    public int deleteProductAuthorizeByAuthorizeIds(Long[] authorizeIds) {
        return productAuthorizeMapper.deleteBatchIds(Arrays.asList(authorizeIds));
    }

    /**
     * 删除产品授权码信息
     *
     * @param authorizeId 产品授权码主键
     * @return 结果
     */
    @Override
    public int deleteProductAuthorizeByAuthorizeId(Long authorizeId) {
        return productAuthorizeMapper.deleteById(authorizeId);
    }

    /**
     * 根据数量批量新增产品授权码
     *
     * @param productAuthorizeVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addProductAuthorizeByNum(ProductAuthorizeVO productAuthorizeVO) {
        Long productId = productAuthorizeVO.getProductId();
        int createNum = productAuthorizeVO.getCreateNum();
        List<ProductAuthorize> list = new ArrayList<>(createNum);
        SysUser user = getLoginUser().getUser();
        for (int i = 0; i < createNum; i++) {
            ProductAuthorize authorize = new ProductAuthorize();
            // 1=未使用，2=使用中
            authorize.setStatus(1);
            authorize.setProductId(productId);
            authorize.setCreateBy(user.getUserName());
            authorize.setCreateTime(DateUtils.getNowDate());
            authorize.setAuthorizeCode(IdUtils.fastSimpleUUID().toUpperCase());
            list.add(authorize);
        }
        return productAuthorizeMapper.insertBatch(list);
    }

    /**
     * 根据产品id查询产品授权码
     * @param productId 产品id
     * @return
     */
    @Override
    public List<ProductAuthorize> listByProductId(Long productId) {
        ProductAuthorize authorize = new ProductAuthorize();
        authorize.setProductId(productId);
        authorize.setDelFlag("0");
        LambdaQueryWrapper<ProductAuthorize> wrapper = buildQueryWrapper(authorize);
        wrapper.orderByDesc(ProductAuthorize::getCreateTime);
        return productAuthorizeMapper.selectList(wrapper);
    }

}
