package com.sydh.iot.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.annotation.DataScope;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.extend.aspectj.DataScopeAspect;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.convert.CategoryConvert;
import com.sydh.iot.domain.Category;
import com.sydh.iot.mapper.CategoryMapper;
import com.sydh.iot.model.IdAndName;
import com.sydh.iot.model.vo.CategoryVO;
import com.sydh.iot.service.ICategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.sydh.common.extend.utils.SecurityUtils.getLoginUser;
import static com.sydh.common.extend.utils.SecurityUtils.isAdmin;


/**
 * 产品分类Service业务层处理
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements ICategoryService
{
    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 查询产品分类
     *
     * @param categoryId 产品分类主键
     * @return 产品分类
     */
    @Override
    public Category selectCategoryByCategoryId(Long categoryId)
    {
        return categoryMapper.selectById(categoryId);
    }

    /**
     * 查询产品分简短类列表
     *
     * @return 产品分类
     */
    @Override
    @DataScope()
    public Page<IdAndName> selectCategoryShortList(CategoryVO categoryVO)
    {
        Category category = CategoryConvert.INSTANCE.convertCategory(categoryVO);
        LambdaQueryWrapper<Category> queryWrapper = this.buildQueryWrapper(category);
        queryWrapper.select(Category::getCategoryId, Category::getCategoryName);
        Page<Category> categoryPage = categoryMapper.selectPage(new Page<>(categoryVO.getPageNum(), categoryVO.getPageSize()), queryWrapper);
        if (0 == categoryPage.getTotal()) {
            return new Page<>();
        }
        Page<IdAndName> idAndNamePage = CategoryConvert.INSTANCE.convertIdAndNamePage(categoryPage);
        List<Category> records = categoryPage.getRecords();
        List<IdAndName> list = new ArrayList<>();
        for (Category record : records) {
            IdAndName idAndName = new IdAndName();
            idAndName.setId(record.getCategoryId());
            idAndName.setName(record.getCategoryName());
            list.add(idAndName);
        }
        idAndNamePage.setRecords(list);
        return idAndNamePage;
    }

    /**
     * 查询产品分类分页列表
     *
     * @param categoryVO 产品分类
     * @return 产品分类
     */
    @Override
    @DataScope()
    public Page<CategoryVO> pageCategoryVO(CategoryVO categoryVO) {
        Category category = CategoryConvert.INSTANCE.convertCategory(categoryVO);
        LambdaQueryWrapper<Category> queryWrapper = this.buildQueryWrapper(category);
        queryWrapper.orderByDesc(Category::getOrderNum).orderByDesc(Category::getCategoryId);
        Page<Category> categoryPage = categoryMapper.selectPage(new Page<>(categoryVO.getPageNum(), categoryVO.getPageSize()), queryWrapper);
        return CategoryConvert.INSTANCE.convertCategoryVOPage(categoryPage);
    }

    /**
     * 查询产品分类列表
     *
     * @param category 产品分类
     * @return 产品分类
     */
    @Override
    public List<CategoryVO> listCategoryVO(Category category) {
        LambdaQueryWrapper<Category> lqw = buildQueryWrapper(category);
        List<Category> categoryList = baseMapper.selectList(lqw);
        return CategoryConvert.INSTANCE.convertCategoryVOList(categoryList);
    }

    /**
     * 新增产品分类
     *
     * @param category 产品分类
     * @return 结果
     */
    @Override
    public int insertCategory(Category category)
    {
        // 判断是否为管理员
        category.setIsSys(1);
        SysUser user = getLoginUser().getUser();
        if (null != user.getDeptId()) {
            category.setTenantId(user.getDept().getDeptUserId());
            category.setTenantName(user.getDept().getDeptName());
        } else {
            category.setTenantId(user.getUserId());
            category.setTenantName(user.getUserName());
        }
        if (!isAdmin(user.getUserId())) {
            category.setIsSys(0);
        }
        category.setCreateTime(DateUtils.getNowDate());
        category.setCreateBy(user.getUserName());
        return categoryMapper.insert(category);
    }

    /**
     * 修改产品分类
     *
     * @param category 产品分类
     * @return 结果
     */
    @Override
    public int updateCategory(Category category)
    {
        category.setUpdateTime(DateUtils.getNowDate());
        return categoryMapper.updateById(category);
    }

    /**
     * 批量删除产品分类
     *
     * @param categoryIds 需要删除的产品分类主键
     * @return 结果
     */
    @Override
    public AjaxResult deleteCategoryByCategoryIds(Long[] categoryIds)
    {
        int productCount=categoryMapper.productCountInCategorys(categoryIds);
        if(productCount>0){
            return AjaxResult.error(MessageUtils.message("delete.fail.please.delete.category.product"));
        }
        if(categoryMapper.deleteBatchIds(Arrays.asList(categoryIds))>0){
            return AjaxResult.success(MessageUtils.message("delete.success"));
        }
        return AjaxResult.error(MessageUtils.message("delete.fail"));
    }

    /**
     * 删除产品分类信息
     *
     * @param categoryId 产品分类主键
     * @return 结果
     */
    @Override
    public int deleteCategoryByCategoryId(Long categoryId)
    {
        return categoryMapper.deleteById(categoryId);
    }

    private LambdaQueryWrapper<Category> buildQueryWrapper(Category query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<Category> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getCategoryId() != null, Category::getCategoryId, query.getCategoryId());
        lqw.like(StringUtils.isNotBlank(query.getCategoryName()), Category::getCategoryName, query.getCategoryName());
        lqw.eq(query.getTenantId() != null, Category::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), Category::getTenantName, query.getTenantName());
        lqw.eq(query.getIsSys() != null, Category::getIsSys, query.getIsSys());
        lqw.eq(query.getParentId() != null, Category::getParentId, query.getParentId());
        lqw.eq(query.getOrderNum() != null, Category::getOrderNum, query.getOrderNum());
        lqw.eq(StringUtils.isNotBlank(query.getDelFlag()), Category::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), Category::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, Category::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), Category::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, Category::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), Category::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(Category::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }

        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.and(wq -> wq.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE)).or().eq(Category::getIsSys, 1));
        }

        return lqw;
    }
}
