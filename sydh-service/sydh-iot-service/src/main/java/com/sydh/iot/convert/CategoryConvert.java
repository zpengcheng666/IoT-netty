package com.sydh.iot.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.iot.domain.Category;
import com.sydh.iot.model.IdAndName;
import com.sydh.iot.model.vo.CategoryVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 产品分类Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-31
 */
@Mapper
public interface CategoryConvert
{
    /** 代码生成区域 可直接覆盖**/
    CategoryConvert INSTANCE = Mappers.getMapper(CategoryConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param category
     * @return 产品分类集合
     */
    CategoryVO convertCategoryVO(Category category);

    /**
     * VO类转换为实体类集合
     *
     * @param categoryVO
     * @return 产品分类集合
     */
    Category convertCategory(CategoryVO categoryVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param categoryList
     * @return 产品分类集合
     */
    List<CategoryVO> convertCategoryVOList(List<Category> categoryList);

    /**
     * VO类转换为实体类
     *
     * @param categoryVOList
     * @return 产品分类集合
     */
    List<Category> convertCategoryList(List<CategoryVO> categoryVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param categoryPage
     * @return 产品分类分页
     */
    Page<CategoryVO> convertCategoryVOPage(Page<Category> categoryPage);

    /**
     * VO类转换为实体类
     *
     * @param categoryVOPage
     * @return 产品分类分页
     */
    Page<Category> convertCategoryPage(Page<CategoryVO> categoryVOPage);

    /**
     * 实体类转换为VO类分页
     *
     * @param categoryPage
     * @return 产品分类分页
     */
//    @Mappings({
//            @Mapping(source = "id",target = "categoryId"),
//            @Mapping(source = "name", target = "categoryName")
//    })
    Page<IdAndName> convertIdAndNamePage(Page<Category> categoryPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
