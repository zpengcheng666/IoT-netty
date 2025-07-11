package com.sydh.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.iot.domain.ProductModbusJob;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sydh.iot.model.modbus.ProductModbusJobVO;

/**
 * 产品轮训任务列Service接口
 *
 * @author zhuangpeng.li
 * @date 2024-09-04
 */
public interface IProductModbusJobService extends IService<ProductModbusJob>
{

    /**
     * 分页查询产品轮训任务列列表
     * @param productModbusJob 产品轮训任务列
     * @return 分页产品轮训任务列
     */
    Page<ProductModbusJobVO> pageProductModbusJobVO(ProductModbusJob productModbusJob);

    /**
     * 查询产品轮训任务列列表
     *
     * @param productModbusJob 产品轮训任务列
     * @return 产品轮训任务列集合
     */
     List<ProductModbusJobVO> selectProductModbusJobVOList(ProductModbusJob productModbusJob);

    /**
     * 查询产品轮训任务列
     *
     * @param taskId 主键
     * @return 产品轮训任务列
     */
     ProductModbusJob selectProductModbusJobById(Long taskId);

    /**
     * 查询产品轮训任务列
     *
     * @param taskId 主键
     * @return 产品轮训任务列
     */
    ProductModbusJob queryByIdWithCache(Long taskId);

    /**
     * 新增产品轮训任务列
     *
     * @param productModbusJob 产品轮训任务列
     * @return 是否新增成功
     */
    Boolean insertWithCache(ProductModbusJob productModbusJob);

    /**
     * 修改产品轮训任务列
     *
     * @param productModbusJob 产品轮训任务列
     * @return 是否修改成功
     */
    Boolean updateWithCache(ProductModbusJob productModbusJob);

    /**
     * 校验并批量删除产品轮训任务列信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);

    /**
     * @description: 查询从机id
     * @param: productId  产品id
     * @return: java.lang.Integer
     */
    List<String> getAddress(Long productId, String serialNumber, Integer deviceType);
}
