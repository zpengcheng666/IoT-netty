package com.sydh.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.annotation.DataScope;
import com.sydh.common.extend.core.domin.entity.SysDept;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.extend.enums.scenemodel.SceneModelVariableTypeEnum;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.convert.SceneModelConvert;
import com.sydh.iot.domain.*;
import com.sydh.iot.mapper.SceneModelDataMapper;
import com.sydh.iot.mapper.SceneModelMapper;
import com.sydh.iot.mapper.SceneModelTagMapper;
import com.sydh.iot.mapper.SceneTagPointsMapper;
import com.sydh.iot.model.scenemodel.CusDeviceVO;
import com.sydh.iot.model.vo.SceneModelDeviceVO;
import com.sydh.iot.model.vo.SceneModelVO;
import com.sydh.iot.model.vo.SipRelationVO;
import com.sydh.iot.service.ISceneModelDeviceService;
import com.sydh.iot.service.ISceneModelService;
import com.sydh.iot.service.ISipRelationService;
import com.sydh.system.mapper.SysDeptMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.sydh.common.extend.utils.SecurityUtils.getLoginUser;


/**
 * 场景管理Service业务层处理
 *
 * @author kerwincui
 * @date 2024-05-20
 */
@Service
public class SceneModelServiceImpl extends ServiceImpl<SceneModelMapper,SceneModel> implements ISceneModelService
{
    @Resource
    private SceneModelMapper sceneModelMapper;

    @Resource
    private ISceneModelDeviceService sceneModelDeviceService;


    @Resource
    private SceneModelDataMapper sceneModelDataMapper;

    @Resource
    private SceneModelTagMapper sceneModelTagMapper;

    @Resource
    private SceneTagPointsMapper sceneTagPointsMapper;
    @Resource
    private ISipRelationService sipRelationService;
    @Resource
    private SysDeptMapper sysDeptMapper;

    /**
     * 查询场景管理
     *
     * @param sceneModelId 场景管理主键
     * @return 场景管理
     */
    @Override
    public SceneModelVO selectSceneModelBySceneModelId(Long sceneModelId)
    {
        SceneModelVO sceneModelVO = sceneModelMapper.selectSceneModelBySceneModelId(sceneModelId);
        if (null == sceneModelVO) {
            return null;
        }
        //查询关联的监控设备
        SipRelation sipRelation = new SipRelation();
        sipRelation.setReSceneModelId(sceneModelId);
        List<SipRelationVO> sipRelationVOList = sipRelationService.selectSipRelationList(sipRelation).getRecords();
        sceneModelVO.setSipRelationVOList(sipRelationVOList);
        SceneModelDevice sceneModelDevice = new SceneModelDevice();
        sceneModelDevice.setSceneModelId(sceneModelId);
        List<SceneModelDeviceVO> sceneModelDeviceVOList = sceneModelDeviceService.listSceneModelDeviceVO(sceneModelDevice);
        if (CollectionUtils.isEmpty(sceneModelDeviceVOList)) {
            return sceneModelVO;
        }
        List<CusDeviceVO> cusDeviceVOList = new ArrayList<>();
        for (SceneModelDeviceVO modelDevice : sceneModelDeviceVOList) {
            if (SceneModelVariableTypeEnum.THINGS_MODEL.getType().equals(modelDevice.getVariableType())) {
                CusDeviceVO cusDeviceVO = new CusDeviceVO();
                cusDeviceVO.setName(modelDevice.getName());
                cusDeviceVO.setProductId(modelDevice.getProductId());
                cusDeviceVO.setSerialNumber(modelDevice.getSerialNumber());
                cusDeviceVOList.add(cusDeviceVO);
            }
        }
        sceneModelVO.setCusDeviceList(cusDeviceVOList);
        sceneModelVO.setSceneModelDeviceVOList(CollectionUtils.isNotEmpty(sceneModelDeviceVOList) ? sceneModelDeviceVOList : new ArrayList<>());
        return sceneModelVO;
    }

    /**
     * 查询场景管理列表
     *
     * @param sceneModelVO 场景管理
     * @return 场景管理
     */
    @Override
    public List<SceneModelVO> selectSceneModelList(SceneModelVO sceneModelVO)
    {
        SysUser user = getLoginUser().getUser();
        if (null == sceneModelVO.getTenantId()) {
            sceneModelVO.setTenantId(user.getDept().getDeptUserId());
        }
        if (null != sceneModelVO.getDeptId()) {
            SysDept sysDept = sysDeptMapper.selectDeptById(sceneModelVO.getDeptId());
            sceneModelVO.setTenantId(sysDept.getDeptUserId());
        }
        SceneModel sceneModel = SceneModelConvert.INSTANCE.convertSceneModel(sceneModelVO);
        LambdaQueryWrapper<SceneModel> queryWrapper = this.buildQueryWrapper(sceneModel);
        List<SceneModel> modelList = baseMapper.selectList(queryWrapper);
        return SceneModelConvert.INSTANCE.convertSceneModelVOList(modelList);
    }

    /**
     * 查询场景管理分页列表
     *
     * @param sceneModelVO 场景管理
     * @return 场景管理
     */
    @Override
    @DataScope(deptAlias = "sm", userAlias = "sm")
    public Page<SceneModelVO> pageSceneModelVO(SceneModelVO sceneModelVO) {
        if (null != sceneModelVO.getDeptId()) {
            SysDept sysDept = sysDeptMapper.selectDeptById(sceneModelVO.getDeptId());
            sceneModelVO.setTenantId(sysDept.getDeptUserId());
            sceneModelVO.setParams(null);
        }
        Page<SceneModelVO> voPage = sceneModelMapper.selectSceneModelVoPage(new Page<>(sceneModelVO.getPageNum(), sceneModelVO.getPageSize()), sceneModelVO);
        if (0 == voPage.getTotal()) {
            return new Page<>();
        }
        List<SceneModelVO> modelVOList = voPage.getRecords();
        List<String> guidList = modelVOList.stream().map(SceneModelVO::getGuid).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(guidList)) {
            List<SceneModelVO> scadaList = sceneModelMapper.selectListScadaIdByGuidS(guidList);
            Map<String, Long> map = scadaList.stream().collect(Collectors.toMap(SceneModelVO::getGuid, SceneModelVO::getScadaId));
            for (SceneModelVO sceneModel1 : modelVOList) {
                Long scadaId = map.get(sceneModel1.getGuid());
                sceneModel1.setScadaId(scadaId);
            }
        }
        return voPage;
    }

    private LambdaQueryWrapper<SceneModel> buildQueryWrapper(SceneModel query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SceneModel> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getTenantId() != null, SceneModel::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getSceneModelName()), SceneModel::getSceneModelName, query.getSceneModelName());
        lqw.eq(query.getStatus() != null, SceneModel::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getGuid()), SceneModel::getGuid, query.getGuid());
        lqw.eq(StringUtils.isNotBlank(query.getSceneDesc()), SceneModel::getSceneDesc, query.getSceneDesc());
        lqw.eq(StringUtils.isNotBlank(query.getImgUrl()), SceneModel::getImgUrl, query.getImgUrl());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SceneModel::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增场景管理
     *
     * @param sceneModel 场景管理
     * @return 结果
     */
    @Override
    public int insertSceneModel(SceneModel sceneModel)
    {
        SysUser user = getLoginUser().getUser();
        if (null != user.getDeptId()) {
            sceneModel.setTenantId(user.getDept().getDeptUserId());
        } else {
            sceneModel.setTenantId(user.getUserId());
        }
        sceneModel.setCreateBy(user.getUserName());
        sceneModel.setUpdateBy(user.getUserName());
        int result = sceneModelMapper.insert(sceneModel);
        if (result > 0) {
            for (SceneModelVariableTypeEnum sceneModelVariableTypeEnum : SceneModelVariableTypeEnum.ADD_LIST) {
                SceneModelDevice sceneModelDevice = new SceneModelDevice();
                sceneModelDevice.setSceneModelId(sceneModel.getSceneModelId());
                sceneModelDevice.setVariableType(sceneModelVariableTypeEnum.getType());
                sceneModelDevice.setName(sceneModelVariableTypeEnum.getName());
                sceneModelDevice.setAllEnable(1);
                sceneModelDevice.setSort(sceneModelVariableTypeEnum.getType());
                sceneModelDevice.setCreateBy(user.getUserName());
                sceneModelDevice.setCreateBy(user.getUserName());
                sceneModelDeviceService.save(sceneModelDevice);
            }
        }
        return result;
    }

    /**
     * 修改场景管理
     *
     * @param sceneModel 场景管理
     * @return 结果
     */
    @Override
    public int updateSceneModel(SceneModelVO sceneModel)
    {
        SysUser user = getLoginUser().getUser();
        sceneModel.setUpdateBy(user.getUserName());
        SceneModel oldSceneModel = sceneModelMapper.selectById(sceneModel.getSceneModelId());
        SysDept sysDept = sysDeptMapper.selectDeptById(sceneModel.getDeptId());
        if (!oldSceneModel.getTenantId().equals(sysDept.getDeptUserId())) {
            sceneModel.setTenantId(sysDept.getDeptUserId());
        }
        SceneModel model = SceneModelConvert.INSTANCE.convertSceneModel(sceneModel);
        return sceneModelMapper.updateById(model);
    }

    /**
     * 批量删除场景管理
     *
     * @param sceneModelIds 需要删除的场景管理主键
     * @return 结果
     */
    @Override
    public int deleteSceneModelBySceneModelIds(Long[] sceneModelIds)
    {
        List<Long> idList = Arrays.asList(sceneModelIds);
        int i = sceneModelMapper.deleteBatchIds(idList);
        if (i > 0) {
            sceneModelDeviceService.deleteBySceneModelIds(idList);
            LambdaQueryWrapper<SceneModelData> sceneModelDataWrapper = new LambdaQueryWrapper<>();
            sceneModelDataWrapper.in(SceneModelData::getSceneModelId, idList);
            sceneModelDataMapper.delete(sceneModelDataWrapper);
            sceneTagPointsMapper.deleteBySceneModelIds(sceneModelIds);
            LambdaQueryWrapper<SceneModelTag> sceneModelTagWrapper = new LambdaQueryWrapper<>();
            sceneModelTagWrapper.in(SceneModelTag::getSceneModelId, idList);
            sceneModelTagMapper.delete(sceneModelTagWrapper);
        }
        return i;
    }

    /**
     * 删除场景管理信息
     *
     * @param sceneModelId 场景管理主键
     * @return 结果
     */
    @Override
    public int deleteSceneModelBySceneModelId(Long sceneModelId)
    {
        return sceneModelMapper.deleteById(sceneModelId);
    }

}
