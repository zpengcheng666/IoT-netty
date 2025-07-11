package com.sydh.common.mybatis.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.sydh.common.core.domain.PageParam;
import com.sydh.common.core.domain.PageResult;
import com.sydh.common.mybatis.utils.MyBatisUtils;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;


public interface BaseMapperX<T>
        extends BaseMapper<T> {
    default PageResult<T> selectPage(PageParam pageParam, @Param("ew") Wrapper<T> queryWrapper) {
        Page page = MyBatisUtils.buildPage(pageParam);
        selectPage((IPage) page, queryWrapper);

        return new PageResult(page.getRecords(), Long.valueOf(page.getTotal()));
    }

    default T selectOne(String field, Object value) {
        return (T) selectOne((Wrapper) (new QueryWrapper()).eq(field, value));
    }

    default T selectOne(SFunction<T, ?> field, Object value) {
        return (T) selectOne((Wrapper) (new LambdaQueryWrapper()).eq(field, value));
    }

    default T selectOne(String field1, Object value1, String field2, Object value2) {
        return (T) selectOne((Wrapper) ((QueryWrapper) (new QueryWrapper()).eq(field1, value1)).eq(field2, value2));
    }

    default T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return (T) selectOne((Wrapper) ((LambdaQueryWrapper) (new LambdaQueryWrapper()).eq(field1, value1)).eq(field2, value2));
    }

    default Long selectCount() {
        return selectCount((Wrapper) new QueryWrapper());
    }

    default Long selectCount(String field, Object value) {
        return selectCount((Wrapper) (new QueryWrapper()).eq(field, value));
    }

    default Long selectCount(SFunction<T, ?> field, Object value) {
        return selectCount((Wrapper) (new LambdaQueryWrapper()).eq(field, value));
    }

    default List<T> selectList() {
        return selectList((Wrapper) new QueryWrapper());
    }

    default List<T> selectList(String field, Object value) {
        return selectList((Wrapper) (new QueryWrapper()).eq(field, value));
    }

    default List<T> selectList(SFunction<T, ?> field, Object value) {
        return selectList((Wrapper) (new LambdaQueryWrapper()).eq(field, value));
    }

    default List<T> selectList(String field, Collection<?> values) {
        return selectList((Wrapper) (new QueryWrapper()).in(field, values));
    }

    default List<T> selectList(SFunction<T, ?> field, Collection<?> values) {
        return selectList((Wrapper) (new LambdaQueryWrapper()).in(field, values));
    }

    default List<T> selectList(SFunction<T, ?> leField, SFunction<T, ?> geField, Object value) {
        return selectList((Wrapper) ((LambdaQueryWrapper) (new LambdaQueryWrapper()).le(leField, value)).ge(geField, value));
    }


    default boolean insertBatch(Collection<T> entities) {
        return Db.saveBatch(entities);
    }


    default boolean insertBatch(Collection<T> entities, int size) {
        return Db.saveBatch(entities, size);
    }

    default int updateBatch(T update) {
        return update(update, (Wrapper) new QueryWrapper());
    }

    default boolean updateBatch(Collection<T> entities, int size) {
        return Db.updateBatchById(entities, size);
    }
}
