package com.sydh.iot.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.annotation.DataScope;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.aspectj.DataScopeAspect;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.extend.utils.SecurityUtils;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.convert.ThingsModelTemplateConvert;
import com.sydh.iot.domain.ThingsModelTemplate;
import com.sydh.iot.mapper.ThingsModelTemplateMapper;
import com.sydh.iot.model.ThingsModelItem.Datatype;
import com.sydh.iot.model.ThingsModelItem.EnumItem;
import com.sydh.iot.model.varTemp.EnumClass;
import com.sydh.iot.model.vo.ThingsModelTemplateVO;
import com.sydh.iot.service.IThingsModelTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static com.sydh.common.extend.utils.SecurityUtils.*;


/**
 * 通用物模型Service业务层处理
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Service
@Slf4j
public class ThingsModelTemplateServiceImpl extends ServiceImpl<ThingsModelTemplateMapper,ThingsModelTemplate> implements IThingsModelTemplateService
{
    @Resource
    private ThingsModelTemplateMapper thingsModelTemplateMapper;

    @Override
    @DataScope(userAlias = "m", deptAlias = "m")
    public Page<ThingsModelTemplateVO> pageThingsModelTemplateVO(ThingsModelTemplateVO thingsModelTemplateVO) {
        SysUser user = getLoginUser().getUser();
        thingsModelTemplateVO.setLanguage(SecurityUtils.getLanguage());
        Page<ThingsModelTemplate> page = thingsModelTemplateMapper.selectThingsModelTemplateList(new Page<>(thingsModelTemplateVO.getPageNum(), thingsModelTemplateVO.getPageSize()), thingsModelTemplateVO);
        if (0 == page.getTotal()) {
            return new Page<>();
        }
        Page<ThingsModelTemplateVO> voPage = ThingsModelTemplateConvert.INSTANCE.convertThingsModelTemplateVOPage(page);
        for (ThingsModelTemplateVO modelTemplateVO : voPage.getRecords()) {
            if (null != user.getDeptId()) {
                modelTemplateVO.setOwner(user.getDept().getDeptUserId().equals(modelTemplateVO.getTenantId()));
            } else {
                modelTemplateVO.setOwner(user.getUserId().equals(modelTemplateVO.getTenantId()));
            }
        }
        return voPage;
    }

    /**
     * 查询通用物模型
     *
     * @param templateId 通用物模型主键
     * @return 通用物模型
     */
    @Override
    public ThingsModelTemplate selectThingsModelTemplateByTemplateId(Long templateId)
    {
        return thingsModelTemplateMapper.selectThingsModelTemplateByTemplateId(templateId, SecurityUtils.getLanguage());
    }

    /**
     * 查询通用物模型列表
     *
     * @param thingsModelTemplateVO 通用物模型
     * @return 通用物模型
     */
    @Override
    public List<ThingsModelTemplateVO> selectThingsModelTemplateVOList(ThingsModelTemplateVO thingsModelTemplateVO)
    {
        SysUser user = getLoginUser().getUser();
        thingsModelTemplateVO.setLanguage(SecurityUtils.getLanguage());
        List<ThingsModelTemplate> list = thingsModelTemplateMapper.selectThingsModelTemplateList(thingsModelTemplateVO);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<ThingsModelTemplateVO> thingsModelTemplateVOList = ThingsModelTemplateConvert.INSTANCE.convertThingsModelTemplateVOList(list);
        for (ThingsModelTemplateVO modelTemplate : thingsModelTemplateVOList) {
            if (null != user.getDeptId()) {
                modelTemplate.setOwner(user.getDept().getDeptUserId().equals(modelTemplate.getTenantId()));
            } else {
                modelTemplate.setOwner(user.getUserId().equals(modelTemplate.getTenantId()));
            }
        }
        return thingsModelTemplateVOList;
    }

    /**
     * 新增通用物模型
     *
     * @param template 通用物模型
     * @return 结果
     */
    @Override
    public int insertThingsModelTemplate(ThingsModelTemplate template)
    {
        try {
            // 判断是否为管理员
            SysUser user = getLoginUser().getUser();
            if (isAdmin(user.getUserId())) {
                template.setIsSys(1);
            } else {
                template.setIsSys(0);
            }
            if (null != user.getDeptId()) {
                template.setTenantId(user.getDept().getDeptUserId());
                template.setTenantName(user.getDept().getDeptName());
            } else {
                template.setTenantId(user.getUserId());
                template.setTenantName(user.getUserName());
            }
            template.setCreateBy(user.getUserName());
            template.setCreateTime(DateUtils.getNowDate());
            return thingsModelTemplateMapper.insert(template);
        }catch (Exception e){
            if (e.getMessage().contains("iot_things_modes_slaveId_reg")){
                throw new ServiceException(MessageUtils.message("things.model.register.address.repeat"));
            }else {
                throw new ServiceException(e.getMessage());
            }
        }

    }

    /**
     * 修改通用物模型
     *
     * @param template 通用物模型
     * @return 结果
     */
    @Override
    public int updateThingsModelTemplate(ThingsModelTemplate template)
    {
        template.setUpdateTime(DateUtils.getNowDate());
        template.setUpdateBy(getUsername());
        return thingsModelTemplateMapper.updateById(template);
    }


    /**
     * 批量删除通用物模型
     *
     * @param templateIds 需要删除的通用物模型主键
     * @return 结果
     */
    @Override
    public int deleteThingsModelTemplateByTemplateIds(Long[] templateIds)
    {
        return thingsModelTemplateMapper.deleteBatchIds(Arrays.asList(templateIds));
    }

    /**
     * 删除通用物模型信息
     *
     * @param templateId 通用物模型主键
     * @return 结果
     */
    @Override
    public int deleteThingsModelTemplateByTemplateId(Long templateId)
    {
        return thingsModelTemplateMapper.deleteById(templateId);
    }



    /**
     * 导入采集点数据
     *
     * @param lists       数据列表
     * @param tempSlaveId 从机编码
     * @return 结果
     */
    public String importData(List<ThingsModelTemplateVO> lists, String tempSlaveId) {
        if (null == tempSlaveId || CollectionUtils.isEmpty(lists)) {
            throw new ServiceException(MessageUtils.message("things.model.import.data.exception"));
        }
        int success = 0;
        int failure = 0;
        StringBuilder succSb = new StringBuilder();
        StringBuilder failSb = new StringBuilder();

        for (ThingsModelTemplateVO model : lists) {
            try {
                //处理数据定义
                this.parseSpecs(model);
                this.insertThingsModelTemplate(ThingsModelTemplateConvert.INSTANCE.convertThingsModelTemplate(model));
                success++;
                succSb.append("<br/>").append(success).append(",采集点: ").append(model.getTemplateName());
            } catch (Exception e) {
                log.error("导入错误：", e);
                failure++;
                failSb.append("<br/>").append(failure).append(",采集点: ").append(model.getTemplateName()).append("导入失败");
            }
        }
        if (failure > 0) {
            throw new ServiceException(failSb.toString());
        }
        return succSb.toString();
    }

    private void parseSpecs(ThingsModelTemplateVO model) {
        JSONObject specs = new JSONObject();
        String datatype = model.getDatatype();
        String limitValue = model.getLimitValue();
        if (limitValue != null && !"".equals(limitValue)) {
            String[] values = limitValue.trim().split("/");
            switch (datatype) {
                case "integer":
                    specs.put("max", new BigDecimal(values[1]));
                    specs.put("min", new BigDecimal(values[0]));
                    specs.put("type", datatype);
                    specs.put("unit", model.getUnit());
                    specs.put("step", 0);
                    break;
                case "bool":
                    specs.put("type",datatype);
                    specs.put("trueText",values[1]);
                    specs.put("falseText",values[0]);
                    break;
                case "enum":
                    List<EnumClass> list = new ArrayList<>();
                    for (String value : values) {
                        String[] params = value.trim().split(":");
                        EnumClass enumCls = new EnumClass();
                        enumCls.setText(params[1]);
                        enumCls.setValue(params[0]);
                        list.add(enumCls);
                    }
                    specs.put("type",datatype);
                    specs.put("enumList",list);
                    break;
            }
            model.setSpecs(specs.toJSONString());
        }
    }

    /**
     * 导出采集点数据
     *
     * @param thingsModelTemplateVO 通用物模型
     * @return 通用物模型集合
     */
    @Override
    public List<ThingsModelTemplateVO> selectThingsModelTemplateExport(ThingsModelTemplateVO thingsModelTemplateVO){
        thingsModelTemplateVO.setLanguage(SecurityUtils.getLanguage());
        List<ThingsModelTemplate> thingsModelTemplates = thingsModelTemplateMapper.selectThingsModelTemplateList(thingsModelTemplateVO);
        if (CollectionUtils.isEmpty(thingsModelTemplates)) {
            return new ArrayList<>();
        }
        List<ThingsModelTemplateVO> thingsModelTemplateVOList = ThingsModelTemplateConvert.INSTANCE.convertThingsModelTemplateVOList(thingsModelTemplates);
        for (ThingsModelTemplateVO template : thingsModelTemplateVOList) {
            Datatype datatype = JSONObject.parseObject(template.getSpecs(), Datatype.class);
            switch (datatype.getType()) {
                case "integer":
                    template.setLimitValue(datatype.getMin()+ "/"+ datatype.getMax());
                    template.setDatatype(datatype.getType());
                    template.setUnit(datatype.getUnit());
                    break;
                case "bool":
                    template.setLimitValue("0:"+datatype.getFalseText()+ "/" + "1:"+datatype.getTrueText());
                    template.setDatatype(datatype.getType());
                    break;
                case "enum":
                    List<EnumItem> enumList = datatype.getEnumList();
                    StringBuilder buffer = new StringBuilder();
                    for (int i = 0; i < enumList.size(); i++) {
                        EnumItem item = enumList.get(i);
                        String s = item.getValue() + ":" + item.getText();
                        buffer.append(s);
                        if (i < enumList.size() -1){
                            buffer.append("/");
                        }
                    }
                    template.setLimitValue(buffer.toString());
                    template.setDatatype(datatype.getType());
                    break;
            }
        }
        return  thingsModelTemplateVOList;
    }

    private LambdaQueryWrapper<ThingsModelTemplate> buildQueryWrapper(ThingsModelTemplate query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ThingsModelTemplate> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(query.getTemplateName()), ThingsModelTemplate::getTemplateName, query.getTemplateName());
        lqw.eq(query.getTenantId() != null, ThingsModelTemplate::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), ThingsModelTemplate::getTenantName, query.getTenantName());
        lqw.eq(StringUtils.isNotBlank(query.getIdentifier()), ThingsModelTemplate::getIdentifier, query.getIdentifier());
        lqw.eq(query.getType() != null, ThingsModelTemplate::getType, query.getType());
        lqw.eq(StringUtils.isNotBlank(query.getDatatype()), ThingsModelTemplate::getDatatype, query.getDatatype());
        lqw.eq(StringUtils.isNotBlank(query.getSpecs()), ThingsModelTemplate::getSpecs, query.getSpecs());
        lqw.eq(query.getIsSys() != null, ThingsModelTemplate::getIsSys, query.getIsSys());
        lqw.eq(query.getIsChart() != null, ThingsModelTemplate::getIsChart, query.getIsChart());
        lqw.eq(query.getIsMonitor() != null, ThingsModelTemplate::getIsMonitor, query.getIsMonitor());
        lqw.eq(query.getIsHistory() != null, ThingsModelTemplate::getIsHistory, query.getIsHistory());
        lqw.eq(query.getIsReadonly() != null, ThingsModelTemplate::getIsReadonly, query.getIsReadonly());
        lqw.eq(query.getIsSharePerm() != null, ThingsModelTemplate::getIsSharePerm, query.getIsSharePerm());
        lqw.eq(query.getModelOrder() != null, ThingsModelTemplate::getModelOrder, query.getModelOrder());
        lqw.eq(StringUtils.isNotBlank(query.getFormula()), ThingsModelTemplate::getFormula, query.getFormula());
        lqw.eq(query.getIsApp() != null, ThingsModelTemplate::getIsApp, query.getIsApp());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(ThingsModelTemplate::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }

        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.and(wq -> wq.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE)).or().eq(ThingsModelTemplate::getIsSys, 1));
        }
        return lqw;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ThingsModelTemplate entity){
        //TODO 做一些数据校验,如唯一约束
    }
}
