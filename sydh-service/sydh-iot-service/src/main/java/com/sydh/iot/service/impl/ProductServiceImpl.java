package com.sydh.iot.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.annotation.DataScope;
import com.sydh.common.config.RuoYiConfig;
import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.core.redis.RedisKeyBuilder;
import com.sydh.common.extend.aspectj.DataScopeAspect;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.extend.core.domin.model.LoginUser;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.file.FileUploadUtils;
import com.sydh.common.utils.file.FileUtils;
import com.sydh.framework.config.ServerConfig;
import com.sydh.iot.cache.ITSLCache;
import com.sydh.iot.cache.ITSLValueCache;
import com.sydh.iot.convert.ProductConvert;
import com.sydh.iot.convert.ThingsModelConvert;
import com.sydh.iot.domain.*;
import com.sydh.iot.enums.DeviceType;
import com.sydh.iot.mapper.*;
import com.sydh.iot.model.ChangeProductStatusModel;
import com.sydh.iot.model.IdAndName;
import com.sydh.iot.model.vo.ProductVO;
import com.sydh.iot.model.vo.SceneDeviceBindVO;
import com.sydh.iot.model.vo.ThingsModelVO;
import com.sydh.iot.service.IModbusConfigService;
import com.sydh.iot.service.IProductService;
import com.sydh.system.service.ISysDeptService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sydh.common.extend.utils.SecurityUtils.getLoginUser;


/**
 * 产品Service业务层处理
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper,Product> implements IProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private ProductAuthorizeMapper productAuthorizeMapper;
    @Resource
    private RedisCache redisCache;
    @Resource
    private ToolServiceImpl toolService;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private SceneDeviceMapper sceneDeviceMapper;
    @Resource
    private ITSLValueCache thingModelCache;
    @Resource
    private ITSLCache itslCache;
    @Resource
    private ProductSubGatewayMapper productSubGatewayMapper;
    @Resource
    private ProductModbusJobMapper productModbusJobMapper;
    @Resource
    private ThingsModelMapper thingsModelMapper;
    @Resource
    private ModbusParamsMapper modbusParamsMapper;
    @Resource
    private ModbusConfigMapper modbusConfigMapper;

    @Resource
    private IModbusConfigService modbusConfigService;

    @Resource
    private DeviceRecordMapper deviceRecordMapper;

    @Resource
    private ISysDeptService deptService;

    @Resource
    private FirmwareMapper firmwareMapper;

    @Resource
    private SubGatewayMapper subGatewayMapper;

    @Resource
    private ServerConfig serverConfig;
    @Resource
    private ThingsModelServiceImpl thingsModelServiceImpl;


    // select cache

    /**
     * 查询产品
     *
     * @param productId 产品主键
     * @return 产品
     */
    @Cacheable(cacheNames = "product", key = "#root.methodName + ':' + #productId", unless = "#result == null")
    @Override
    public Product selectProductByProductId(Long productId) {
        return productMapper.selectById(productId);
    }

    /**
     * 获取产品下面的设备数量
     *
     * @param productId 产品ID
     * @return 结果
     */
    @Cacheable(cacheNames = "product", key = "#root.methodName + ':' + #productId", unless = "#result == null")
    @Override
    public int selectDeviceCountByProductId(Long productId) {
        return deviceMapper.selectDeviceCountByProductId(productId);
    }


    /**
     * 根据产品id获取协议编号
     *
     * @param productId 产品id
     * @return 协议编号
     */
    @Cacheable(cacheNames = "product", key = "#root.methodName + ':' + #productId", unless = "#result == null")
    @Override
    public String getProtocolByProductId(Long productId) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getProductId, productId);
        queryWrapper.select(Product::getProtocolCode);
        return productMapper.selectOne(queryWrapper) == null ? null : productMapper.selectById(productId).getProtocolCode();
    }


    /**
     * 修改产品
     *
     * @param product 产品
     * @return 结果
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = "product", key = "'selectProductByProductId:' + #product.productId"),
            @CacheEvict(cacheNames = "product", key = "'selectDeviceCountByProductId:' + #product.productId"),
            @CacheEvict(cacheNames = "product", key = "'getProtocolByProductId:' + #product.productId")
    })
    @Override
    public int updateProduct(Product product) {
        product.setUpdateTime(DateUtils.getNowDate());
        int i = productMapper.updateById(product);
        if (i > 0 && DeviceType.GATEWAY.getCode() == product.getDeviceType()) {
            Product oldProduct = productMapper.selectById(product.getProductId());
            if (oldProduct.getTransport().equals(product.getTransport())) {
                return i;
            }
            LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(Device::getDeviceId, Device::getProductId, Device::getSerialNumber);
            queryWrapper.eq(Device::getProductId, product.getProductId());
            List<Device> deviceList = deviceMapper.selectList(queryWrapper);
            for (Device device : deviceList) {
                // redis中删除设备协议缓存信息
                String cacheKey = RedisKeyBuilder.buildDeviceMsgCacheKey(device.getSerialNumber());
                redisCache.deleteObject(cacheKey);
            }
            LambdaQueryWrapper<SubGateway> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.select(SubGateway::getSubClientId);
            queryWrapper1.eq(SubGateway::getParentProductId, product.getProductId());
            List<SubGateway> subGatewayList = subGatewayMapper.selectList(queryWrapper1);
            for (SubGateway subGateway : subGatewayList) {
                // redis中删除设备协议缓存信息
                String cacheKey = RedisKeyBuilder.buildDeviceMsgCacheKey(subGateway.getSubClientId());
                redisCache.deleteObject(cacheKey);
            }
        }
        return i;
    }

    /**
     * 批量删除产品
     *
     * @param productIds 需要删除的产品主键
     * @return 结果
     */
    @CacheEvict(cacheNames = "product", allEntries = true)
    @Override
    @Transactional
    public AjaxResult deleteProductByProductIds(Long[] productIds) {
        List<Long> productIdList = Arrays.asList(productIds);
        // 删除物模型JSON缓存
        for (int i = 0; i < productIds.length; i++) {
            String key = RedisKeyBuilder.buildTSLCacheKey(productIds[i]);
            redisCache.deleteObject(key);
        }
        // 产品下不能有固件
        LambdaQueryWrapper<Firmware> firmwareWrapper = new LambdaQueryWrapper<>();
        firmwareWrapper.in(Firmware::getProductId, productIdList);
        Long firmwareCount = firmwareMapper.selectCount(firmwareWrapper);
        if (firmwareCount > 0) {
            return AjaxResult.error(MessageUtils.message("delete.fail.please.delete.firmware"));
        }
        // 产品下不能有设备
        LambdaQueryWrapper<Device> deviceWrapper = new LambdaQueryWrapper<>();
        deviceWrapper.in(Device::getProductId, productIdList);
        Long deviceCount = deviceMapper.selectCount(deviceWrapper);
        if (deviceCount > 0) {
            return AjaxResult.error(MessageUtils.message("delete.fail.please.delete.product.device"));
        }
        // 产品下不能有场景联动
        List<SceneDeviceBindVO> sceneDeviceBindVOList = sceneDeviceMapper.listSceneProductBind(productIds);
        if (CollectionUtils.isNotEmpty(sceneDeviceBindVOList)) {
            String sceneNames = sceneDeviceBindVOList.stream().map(SceneDeviceBindVO::getSceneName).collect(Collectors.joining("，"));
            return AjaxResult.error(StringUtils.format(MessageUtils.message("delete.fail.please.delete.product.scene"), sceneNames));
        }
        // 删除产品物模型
        int deleted = productMapper.deleteBatchIds(productIdList);
        // 删除产品的授权码
        LambdaQueryWrapper<ProductAuthorize> productAuthorizeWrapper = new LambdaQueryWrapper<>();
        productAuthorizeMapper.delete(productAuthorizeWrapper);
        // 删除产品
        if (deleted > 0) {
            productIdList.forEach(productId -> {
                thingsModelServiceImpl.deleteProductThingsModelAndCacheByProductId(productId);
            });
            // 删除产品绑定的子产品关系
            LambdaQueryWrapper<ProductSubGateway> productSubGatewayLambdaQueryWrapper = new LambdaQueryWrapper<>();
            productSubGatewayLambdaQueryWrapper.in(ProductSubGateway::getGwProductId, productIdList);
            productSubGatewayMapper.delete(productSubGatewayLambdaQueryWrapper);
            // 删除产品配置的轮询
            LambdaQueryWrapper<ProductModbusJob> productModbusJobLambdaQueryWrapper = new LambdaQueryWrapper<>();
            productModbusJobLambdaQueryWrapper.in(ProductModbusJob::getProductId, productIdList);
            productModbusJobMapper.delete(productModbusJobLambdaQueryWrapper);
            // 删除设备分配记录
            LambdaQueryWrapper<DeviceRecord> deviceRecordWrapper = new LambdaQueryWrapper<>();
            deviceRecordWrapper.in(DeviceRecord::getProductId, productIdList);
            deviceRecordMapper.delete(deviceRecordWrapper);
            return AjaxResult.success(MessageUtils.message("delete.success"));
        }
        return AjaxResult.error(MessageUtils.message("delete.fail"));
    }


    /**
     * 删除产品信息
     *
     * @param productId 产品主键
     * @return 结果
     */
    @CacheEvict(cacheNames = "product", allEntries = true)
    @Override
    public int deleteProductByProductId(Long productId) {
        // 删除物模型JSON缓存
        redisCache.deleteObject(RedisKeyBuilder.buildTSLCacheKey(productId));
        return productMapper.deleteById(productId);
    }

    /**
     * 查询产品列表
     *
     * @param productVO 产品
     * @return 产品
     */
    @Override
    @DataScope()
    public Page<ProductVO> pageProductVO(ProductVO productVO) {
        LambdaQueryWrapper<Product> queryWrapper = this.getPageQueryWrapper(productVO);
        Page<Product> productPage = this.page(new Page<>(productVO.getPageNum(), productVO.getPageSize()), queryWrapper);
        if (0 == productPage.getTotal()) {
            return new Page<>();
        }
        Page<ProductVO> voPage = ProductConvert.INSTANCE.convertProductVOPage(productPage);
        List<ProductVO> productVOList = voPage.getRecords();
        // 组态不是所有人都买了，故单独查询组态信息
        List<String> guidList = productVOList.stream().map(ProductVO::getGuid).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        Map<String, Long> scadaMap = new HashMap<>(2);
        if (CollectionUtils.isNotEmpty(guidList)) {
            List<ProductVO> scadaList = productMapper.selectListScadaIdByGuidS(guidList);
            scadaMap = scadaList.stream().collect(Collectors.toMap(ProductVO::getGuid, ProductVO::getScadaId));
        }
        List<Long> productIdList = productVOList.stream().map(ProductVO::getProductId).collect(Collectors.toList());
        LambdaQueryWrapper<ModbusParams> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(ModbusParams::getProductId, productIdList);
        List<ModbusParams> modbusParamsList = modbusParamsMapper.selectList(queryWrapper1);
        Map<Long, ModbusParams> modbusParamsMap = new HashMap<>(2);
        if (CollectionUtils.isNotEmpty(modbusParamsList)) {
           modbusParamsMap = modbusParamsList.stream().collect(Collectors.toMap(ModbusParams::getProductId, Function.identity()));
        }
        for (ProductVO product1 : productVOList) {
            Long scadaId = scadaMap.get(product1.getGuid());
            if (Objects.nonNull(scadaId)) {
                product1.setScadaId(scadaId);
            }
            product1.setIsOwner(product1.getTenantId().equals(productVO.getTenantId()) ? 1 : 0);
            // modbusTcp协议判断是否配置了modbus
            if (SYDHConstant.PROTOCOL.JsonGateway.equals(product1.getProtocolCode())) {
                product1.setCanSelect(true);
            } else {
                ModbusParams modbusParams = modbusParamsMap.get(product1.getProductId());
                product1.setCanSelect(!Objects.isNull(modbusParams) || DeviceType.SUB_GATEWAY.getCode() != product1.getDeviceType());
            }
        }
        return voPage;
    }

    private LambdaQueryWrapper<Product> getPageQueryWrapper(ProductVO productVO) {
        LambdaQueryWrapper<Product> queryWrapper = Wrappers.lambdaQuery();
        List<Long> ids = deptService.selectDeptUserIdList(productVO.getDeptId());
        // 构建查询条件
        if (productVO.getDeptId() != null && productVO.getShowSenior() && !productVO.getIsAdmin()) {
            queryWrapper.and(wrapper -> wrapper.eq(Product::getTenantId, productVO.getTenantId()).or().in(Product::getTenantId, ids).eq(Product::getIsSys, 0));
        }
        else {
            // 数据范围过滤
            if (ObjectUtil.isNotEmpty(productVO.getParams().get(DataScopeAspect.DATA_SCOPE))){
                queryWrapper.apply((String) productVO.getParams().get(DataScopeAspect.DATA_SCOPE));
            }
        }

        if (StringUtils.isNotBlank(productVO.getProductName())) {
            queryWrapper.like(Product::getProductName, productVO.getProductName());
        }

        if (StringUtils.isNotBlank(productVO.getCategoryName())) {
            queryWrapper.like(Product::getCategoryName, productVO.getCategoryName());
        }

        if (productVO.getStatus() != null) {
            queryWrapper.eq(Product::getStatus, productVO.getStatus());
        }

        if (productVO.getDeviceType() != null) {
            queryWrapper.eq(Product::getDeviceType, productVO.getDeviceType());
        }
        queryWrapper.orderByDesc(Product::getCreateTime);
        return queryWrapper;
    }

    /**
     * 查询产品列表
     *
     * @param product 产品
     * @return 产品
     */
    @Override
    public List<ProductVO> listProductVO(Product product) {
        LambdaQueryWrapper<Product> lqw = buildQueryWrapper(product);
        List<Product> productList = baseMapper.selectList(lqw);
        return ProductConvert.INSTANCE.convertProductVOList(productList);
    }

    /**
     * 查询产品简短列表
     *
     * @return 产品
     */
    @Override
    @DataScope
    public List<IdAndName> selectProductShortList(ProductVO productVO) {
        LambdaQueryWrapper<Product> queryWrapper = Wrappers.lambdaQuery();
        List<Long> ids = deptService.selectDeptUserIdList(productVO.getDeptId());
        // 构建查询条件
        if (productVO.getDeptId() != null && productVO.getShowSenior() && !productVO.getIsAdmin()) {
            queryWrapper.eq(Product::getTenantId, productVO.getTenantId())
                    .or(wrapper -> wrapper.in(Product::getTenantId, ids).eq(Product::getIsSys, 0));
        } else {
            // 数据范围过滤
            if (ObjectUtil.isNotEmpty(productVO.getParams().get(DataScopeAspect.DATA_SCOPE))){
                queryWrapper.apply((String) productVO.getParams().get(DataScopeAspect.DATA_SCOPE));
            }
        }

        if (productVO.getDeviceType() != null) {
            queryWrapper.eq(Product::getDeviceType, productVO.getDeviceType());
        }

        // 查询产品
        List<Product> productList = productMapper.selectList(queryWrapper);

        // 转换结果
        return productList.stream().map(product -> {
            IdAndName idAndName = new IdAndName();
            idAndName.setId(product.getProductId());
            idAndName.setName(product.getProductName());
            return idAndName;
        }).collect(Collectors.toList());

    }

    /**
     * 根据设备编号查询产品信息
     *
     * @param serialNumber 设备编号
     * @return 结果
     */
    @Override
    public Product getProductBySerialNumber(String serialNumber) {
        Device dev = deviceMapper.selectDeviceBySerialNumber(serialNumber);
        if (dev != null) {
            return this.selectProductByProductId(dev.getProductId());
        }
        return null;
    }

    /**
     * 新增产品
     *
     * @param product 产品
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product insertProduct(Product product) {
        // mqtt账号密码
        if (StringUtils.isEmpty(product.getAccount())) {
            product.setAccount(getLoginUser().getUser().getUserName());
        }
        if (StringUtils.isEmpty(product.getAuthPassword())) {
            product.setAuthPassword("P" + toolService.getStringRandom(15));
        }
        if (StringUtils.isEmpty(product.getSecret())) {
            product.setSecret("K" + toolService.getStringRandom(15));
        }
        product.setStatus(product.getStatus() == null ? 1 : product.getStatus());
        product.setPanelEnable(product.getPanelEnable() == null ? 0 : product.getPanelEnable());
        product.setCreateTime(DateUtils.getNowDate());
        int insert = productMapper.insert(product);
        // 网关设备默认添加modbus配置参数
        boolean modbus = DeviceType.GATEWAY.getCode() == product.getDeviceType() && (
                SYDHConstant.PROTOCOL.ModbusRtu.equals(product.getProtocolCode()) || SYDHConstant.PROTOCOL.ModbusTcp.equals(product.getProtocolCode())
                        || SYDHConstant.PROTOCOL.ModbusToJsonHP.equals(product.getProtocolCode()) || SYDHConstant.PROTOCOL.ModbusToJsonZQWL.equals(product.getProtocolCode()));
        if (modbus && insert > 0) {
            ModbusParams modbusParams = new ModbusParams();
            modbusParams.setProductId(product.getProductId());
            modbusParams.setPollType(0);
            modbusParams.setStatusDeter(2);
            modbusParamsMapper.insert(modbusParams);
        }
        return product;
    }


    /**
     * 更新产品状态,1-未发布，2-已发布
     *
     * @param model
     * @return 结果
     */
    @Override
    @Transactional
    public AjaxResult changeProductStatus(ChangeProductStatusModel model) {
        if (model.getStatus() != 1 && model.getStatus() != 2) {
            return AjaxResult.error(MessageUtils.message("product.status.update.fail.value.fail"));
        }
        if (model.getStatus() == 2) {
            // 不需要一定要有物模型才可以发布
            updateDeviceStatusByProductIdAsync(model.getProductId());
            //更新物模型缓存
            itslCache.setCacheThingsModelByProductId(model.getProductId());
        }
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getProductId, model.getProductId());
        wrapper.set(Product::getStatus, model.getStatus());
        if (this.update(wrapper)) {
            return AjaxResult.success(MessageUtils.message("operate.success"));
        }
        return AjaxResult.error(MessageUtils.message("product.status.update.fail"));
    }

    /***
     * 更新产品下所有设备的物模型值
     * @param productId
     */
    @Async
    public void updateDeviceStatusByProductIdAsync(Long productId) {
        List<String> deviceNumbers = deviceMapper.selectSerialNumberByProductId(productId);
        deviceNumbers.forEach(x -> {
            // 缓存新的物模型值
            thingModelCache.addCacheDeviceStatus(productId, x);
        });
    }

    /**
     * 根据模板id查询所有使用的产品
     *
     * @param templeId 模板id
     * @return
     */
    @Override
    public List<Product> selectByTempleId(Long templeId) {
        return productMapper.selectByTempleId(templeId);
    }

    @Override
    public String selectImgUrlByProductId(Long productId) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getProductId, productId);
        queryWrapper.select(Product::getImgUrl);
        Product product = productMapper.selectOne(queryWrapper);
        return Objects.nonNull(product) ? product.getImgUrl() : null;
    }

    @Override
    public Page<ProductVO> pageTerminalUserProduct(ProductVO productVO) {
        return productMapper.pageTerminalUserProduct(new Page<>(productVO.getPageNum(), productVO.getPageSize()), productVO);
    }

    /**
     * 根据产品id获取关联组态guid
     *
     * @param productId
     * @return guid
     */
    @Override
    public String selectGuidByProductId(Long productId) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getProductId, productId);
        queryWrapper.select(Product::getGuid);
        Product product = productMapper.selectOne(queryWrapper);
        return Objects.nonNull(product) ? product.getGuid() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult copy(Long productId) {
        LoginUser loginUser = getLoginUser();
        // 产品信息
        Date nowDate = DateUtils.getNowDate();
        Product product = this.selectProductByProductId(productId);
        product.setProductId(null);
        product.setTenantId(loginUser.getUser().getDept().getDeptUserId());
        product.setTenantName(loginUser.getUser().getDept().getDeptUserName());
        product.setCreateTime(nowDate);
        product.setUpdateTime(nowDate);
        product.setGuid(null);
        productMapper.insert(product);
        // 物模型
        ThingsModelVO thingsModelVO = new ThingsModelVO();
        thingsModelVO.setProductId(productId);
        thingsModelVO.setLanguage("zh-CN");
        List<ThingsModelVO> thingsModelVOList = thingsModelMapper.selectThingsModelList(thingsModelVO);
        for (ThingsModelVO model : thingsModelVOList) {
            model.setProductId(product.getProductId());
            model.setModelId(null);
            model.setTenantId(loginUser.getUser().getDept().getDeptUserId());
            model.setTenantName(loginUser.getUser().getDept().getDeptUserName());
            model.setCreateTime(nowDate);
            model.setUpdateTime(nowDate);
            ThingsModel thingsModel = ThingsModelConvert.INSTANCE.convertThingsModel(model);
            thingsModelMapper.insert(thingsModel);
        }
        // 更新redis缓存
        itslCache.setCacheThingsModelByProductId(productId);
        // modbus配置
        LambdaQueryWrapper<ModbusParams> modbusParamsWrapper = new LambdaQueryWrapper<>();
        modbusParamsWrapper.eq(ModbusParams::getProductId, productId);
        ModbusParams modbusParams = modbusParamsMapper.selectOne(modbusParamsWrapper);
        if (Objects.nonNull(modbusParams)) {
            modbusParams.setId(null);
            modbusParams.setProductId(product.getProductId());
            modbusParams.setCreateTime(nowDate);
            modbusParams.setUpdateTime(nowDate);
            modbusParamsMapper.insert(modbusParams);
        }
        ModbusConfig modbusConfig = new ModbusConfig();
        modbusConfig.setProductId(productId);
        List<ModbusConfig> modbusConfigList = modbusConfigService.selectModbusConfigList(modbusConfig).getRecords();
        for (ModbusConfig config : modbusConfigList) {
            config.setId(null);
            config.setProductId(product.getProductId());
            config.setCreateTime(nowDate);
            config.setUpdateTime(nowDate);
            modbusConfigMapper.insert(config);
        }
        LambdaQueryWrapper<ProductModbusJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductModbusJob::getProductId, productId);
        List<ProductModbusJob> productModbusJobList = productModbusJobMapper.selectList(queryWrapper);
        for (ProductModbusJob productModbusJob : productModbusJobList) {
            productModbusJob.setTaskId(null);
            productModbusJob.setProductId(product.getProductId());
            productModbusJob.setCreateTime(nowDate);
            productModbusJobMapper.insert(productModbusJob);
        }
        LambdaQueryWrapper<ProductSubGateway> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(ProductSubGateway::getGwProductId, productId);
        List<ProductSubGateway> productSubGatewayList = productSubGatewayMapper.selectList(queryWrapper1);
        for (ProductSubGateway productSubGateway : productSubGatewayList) {
            productSubGateway.setId(null);
            productSubGateway.setGwProductId(product.getProductId());
            productSubGateway.setCreateBy(loginUser.getUsername());
            productSubGateway.setCreateTime(nowDate);
            productSubGateway.setUpdateTime(nowDate);
            productSubGatewayMapper.insert(productSubGateway);
        }
        // 监控信息
        return AjaxResult.success();
    }

    @Override
    public AjaxResult importJson(MultipartFile file) throws IOException {
        SysUser user = getLoginUser().getUser();
        Long deptUserId = user.getDept().getDeptUserId();
        String deptUserName = user.getDept().getDeptUserName();
        InputStream inputStream = file.getInputStream();
        if(file.isEmpty()){
            return AjaxResult.error(MessageUtils.message("file.content.is.empty"));
        }
        if(file.getOriginalFilename().indexOf("json")==-1){
            return AjaxResult.error(MessageUtils.message("file.is.invalid"));
        }
        Product product;
        try {
            product = JSON.parseObject(inputStream, Product.class);
            product.setTenantId(deptUserId);
            product.setTenantName(deptUserName);
            product.setCreateBy(user.getUserName());
            product.setCreateTime(new Date());
            // mqtt账号密码
            if (StringUtils.isEmpty(product.getAccount())) {
                product.setAccount(product.getTenantName());
            }
            if (StringUtils.isEmpty(product.getAuthPassword())) {
                product.setAuthPassword("P" + toolService.getStringRandom(15));
            }
            if (StringUtils.isEmpty(product.getSecret())) {
                product.setSecret("K" + toolService.getStringRandom(15));
            }
            product.setStatus(product.getStatus() == null ? 1 : product.getStatus());
        }catch (Exception e){
            return AjaxResult.error(MessageUtils.message("file.is.invalid"));
        }finally {
            inputStream.close();
        }
        int insert = productMapper.insert(product);
        if (insert > 0 && StringUtils.isNotEmpty(product.getModel())) {
            List<ThingsModel> thingsModelList = JSON.parseArray(product.getModel(), ThingsModel.class);
            for (ThingsModel thingsModel : thingsModelList) {
                thingsModel.setProductId(product.getProductId());
                thingsModel.setProductName(product.getProductName());
                thingsModel.setTenantId(deptUserId);
                thingsModel.setTenantName(deptUserName);
                thingsModel.setCreateBy(user.getUserName());
                thingsModel.setCreateTime(new Date());
            }
            thingsModelMapper.insertBatch(thingsModelList);
            // 更新redis缓存
            itslCache.setCacheThingsModelByProductId(product.getProductId());
        }
        return insert > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    @Override
    public AjaxResult panelBgUpload(MultipartFile file) {
        try {
            // 获取上传路径
            String filePath = RuoYiConfig.getPanelPath();

            // 判断路径是否存在，不存在则创建
            File uploadDir = new File(filePath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }

    }

    private LambdaQueryWrapper<Product> buildQueryWrapper(Product query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getProductId() != null, Product::getProductId, query.getProductId());
        lqw.like(StringUtils.isNotBlank(query.getProductName()), Product::getProductName, query.getProductName());
        lqw.eq(StringUtils.isNotBlank(query.getProtocolCode()), Product::getProtocolCode, query.getProtocolCode());
        lqw.eq(query.getCategoryId() != null, Product::getCategoryId, query.getCategoryId());
        lqw.like(StringUtils.isNotBlank(query.getCategoryName()), Product::getCategoryName, query.getCategoryName());
        lqw.eq(query.getTenantId() != null, Product::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), Product::getTenantName, query.getTenantName());
        lqw.eq(query.getIsSys() != null, Product::getIsSys, query.getIsSys());
        lqw.eq(query.getIsAuthorize() != null, Product::getIsAuthorize, query.getIsAuthorize());
        lqw.eq(StringUtils.isNotBlank(query.getAccount()), Product::getAccount, query.getAccount());
        lqw.eq(StringUtils.isNotBlank(query.getAuthPassword()), Product::getAuthPassword, query.getAuthPassword());
        lqw.eq(StringUtils.isNotBlank(query.getSecret()), Product::getSecret, query.getSecret());
        lqw.eq(query.getStatus() != null, Product::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getThingsModelsJson()), Product::getThingsModelsJson, query.getThingsModelsJson());
        lqw.eq(query.getDeviceType() != null, Product::getDeviceType, query.getDeviceType());
        lqw.eq(query.getNetworkMethod() != null, Product::getNetworkMethod, query.getNetworkMethod());
        lqw.eq(query.getVertificateMethod() != null, Product::getVertificateMethod, query.getVertificateMethod());
        lqw.eq(StringUtils.isNotBlank(query.getImgUrl()), Product::getImgUrl, query.getImgUrl());
        lqw.eq(StringUtils.isNotBlank(query.getDelFlag()), Product::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), Product::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, Product::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), Product::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, Product::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), Product::getRemark, query.getRemark());
        lqw.eq(StringUtils.isNotBlank(query.getTransport()), Product::getTransport, query.getTransport());
        lqw.eq(query.getLocationWay() != null, Product::getLocationWay, query.getLocationWay());
        lqw.eq(StringUtils.isNotBlank(query.getGuid()), Product::getGuid, query.getGuid());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(Product::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    @Override
    @DataScope()
    public Page<ProductVO> pageDeletedProductVO(Product product) {
        return productMapper.selectDelProductVO(new Page<>(product.getPageNum(),  product.getPageSize()),product);
    }

    @Override
    public AjaxResult restoreProduct(Long productId) {
        // 查询产品是否为逻辑删除状态(del_flag = 1)
        Product product = productMapper.selectProductById(productId);

        if (product != null) {
            // 恢复产品 del_flag = 0
            int i = productMapper.restoreProduct(productId);
            if (i > 0) {
                thingsModelMapper.restoreThingsModelByProductId(product.getProductId());
            }
            return i > 0 ? AjaxResult.success() : AjaxResult.error();
        }
        return AjaxResult.error("restore.product.fail");
    }

    @Override
    public int deleteProductByIds(Long[] productIds) {
        int delete = 0;
        for (Long productId : productIds) {
            delete += productMapper.deleteProductById(productId);
            thingsModelMapper.deleteThingsModelByModelIds(productId);
        }
        return delete;
    }
}
