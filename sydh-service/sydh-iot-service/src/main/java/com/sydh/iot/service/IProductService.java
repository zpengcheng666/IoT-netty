package com.sydh.iot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.iot.domain.Product;
import com.sydh.iot.model.ChangeProductStatusModel;
import com.sydh.iot.model.IdAndName;
import com.sydh.iot.model.vo.ProductVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 产品Service接口
 *
 * @author kerwincui
 * @date 2021-12-16
 */
public interface IProductService extends IService<Product>
{
    /**
     * 查询产品
     *
     * @param productId 产品主键
     * @return 产品
     */
    public Product selectProductByProductId(Long productId);

    /**
     * 查询产品列表
     *
     * @param productVO 产品
     * @return 产品分页集合
     */
    Page<ProductVO> pageProductVO(ProductVO productVO);

    /**
     * 查询产品列表
     *
     * @param product 产品
     * @return 产品集合
     */
    List<ProductVO> listProductVO(Product product);

    /**
     * 查询产品简短列表
     *
     * @return 产品集合
     */
    public List<IdAndName> selectProductShortList(ProductVO productVO);

    /**
     * 根据设备编号查询产品信息
     * @param serialNumber 设备编号
     * @return 结果
     */
    public Product getProductBySerialNumber(String serialNumber);

    /**
     * 新增产品
     *
     * @param product 产品
     * @return 结果
     */
    public Product insertProduct(Product product);

    /**
     * 修改产品
     *
     * @param product 产品
     * @return 结果
     */
    public int updateProduct(Product product);

    /**
     * 获取产品下面的设备数量
     *
     * @param productId 产品ID
     * @return 结果
     */
    public int selectDeviceCountByProductId(Long productId);

    /**
     * 更新产品状态，1-未发布，2-已发布
     *
     * @param model
     * @return 结果
     */
    public AjaxResult changeProductStatus(ChangeProductStatusModel model);

    /**
     * 批量删除产品
     *
     * @param productIds 需要删除的产品主键集合
     * @return 结果
     */
    public AjaxResult deleteProductByProductIds(Long[] productIds);

    /**
     * 删除产品信息
     *
     * @param productId 产品主键
     * @return 结果
     */
    public int deleteProductByProductId(Long productId);


    /**
     * 根据产品id获取协议编号
     * @param productId 产品id
     * @return 协议编号
     */
    public String getProtocolByProductId(Long productId);


    /**
     * 根据模板id查询所有使用的产品
     * @param templeId 模板id
     * @return
     */
    public List<Product> selectByTempleId(Long templeId);

    /**
     * 查询产品图片
     * @param productId 产品id
     * @return java.lang.String
     */
    String selectImgUrlByProductId(Long productId);

    /**
     * 查询终端用户产品
     * @param productVO
     * @return java.util.List<com.sydh.iot.domain.Product>
     */
    Page<ProductVO> pageTerminalUserProduct(ProductVO productVO);

    /**
     * 根据产品id获取关联组态guid
     * @param productId
     * @return guid
     */
    String selectGuidByProductId(Long productId);

    /**
     * 复制产品
     * @param productId
     * @return
     */
    AjaxResult copy(Long productId);

    /**
     * 产品导入json文件
     * @param file 文件
     * @return
     */
    AjaxResult importJson(MultipartFile file) throws IOException;

    AjaxResult panelBgUpload(MultipartFile file);

    Page<ProductVO> pageDeletedProductVO(Product product);

    AjaxResult restoreProduct(Long productId);

    int deleteProductByIds(Long[] productIds);
}
