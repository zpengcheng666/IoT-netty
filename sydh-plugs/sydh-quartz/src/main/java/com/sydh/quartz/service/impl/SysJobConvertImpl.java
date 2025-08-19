package com.sydh.quartz.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.quartz.convert.SysJobConvert;
import com.sydh.quartz.domain.SysJob;
import com.sydh.quartz.vo.SysJobVO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SysJobConvertImpl类 - 用于SysJobConvertImpl相关业务逻辑
 *
 * @author Sunlc
 * @date 2025-07-25 14:07
 */
public class SysJobConvertImpl implements SysJobConvert {

    @Override
    public SysJobVO convertSysJobVO(SysJob sysJob) {
        if ( sysJob == null ) {
            return null;
        }

        SysJobVO sysJobVO = new SysJobVO();

        sysJobVO.setPageNum( sysJob.getPageNum() );
        sysJobVO.setPageSize( sysJob.getPageSize() );
        Map<String, Object> map = sysJob.getParams();
        if ( map != null ) {
            sysJobVO.setParams( new LinkedHashMap<String, Object>( map ) );
        }

        return sysJobVO;
    }

    @Override
    public SysJob convertSysJob(SysJobVO sysJobVO) {
        if ( sysJobVO == null ) {
            return null;
        }

        SysJob sysJob = new SysJob();

        sysJob.setPageNum( sysJobVO.getPageNum() );
        sysJob.setPageSize( sysJobVO.getPageSize() );
        Map<String, Object> map = sysJobVO.getParams();
        if ( map != null ) {
            sysJob.setParams( new LinkedHashMap<String, Object>( map ) );
        }

        return sysJob;
    }

    @Override
    public List<SysJobVO> convertSysJobVOList(List<SysJob> sysJobList) {
        if ( sysJobList == null ) {
            return null;
        }

        List<SysJobVO> list = new ArrayList<SysJobVO>( sysJobList.size() );
        for ( SysJob sysJob : sysJobList ) {
            list.add( convertSysJobVO( sysJob ) );
        }

        return list;
    }

    @Override
    public List<SysJob> convertSysJobList(List<SysJobVO> sysJobVOList) {
        if ( sysJobVOList == null ) {
            return null;
        }

        List<SysJob> list = new ArrayList<SysJob>( sysJobVOList.size() );
        for ( SysJobVO sysJobVO : sysJobVOList ) {
            list.add( convertSysJob( sysJobVO ) );
        }

        return list;
    }

    @Override
    public Page<SysJobVO> convertSysJobVOPage(Page<SysJob> sysJobPage) {
        if ( sysJobPage == null ) {
            return null;
        }

        Page<SysJobVO> page = new Page<SysJobVO>();

        page.setPages( sysJobPage.getPages() );
        page.setRecords( convertSysJobVOList( sysJobPage.getRecords() ) );
        page.setTotal( sysJobPage.getTotal() );
        page.setSize( sysJobPage.getSize() );
        page.setCurrent( sysJobPage.getCurrent() );
        page.setSearchCount( sysJobPage.isSearchCount() );
        page.setOptimizeCountSql( sysJobPage.isOptimizeCountSql() );
        List<OrderItem> list1 = sysJobPage.getOrders();
        if ( list1 != null ) {
            page.setOrders( new ArrayList<OrderItem>( list1 ) );
        }
        page.setMaxLimit( sysJobPage.getMaxLimit() );
        page.setCountId( sysJobPage.getCountId() );

        return page;
    }

    @Override
    public Page<SysJob> convertSysJobPage(Page<SysJobVO> sysJobVOPage) {
        if ( sysJobVOPage == null ) {
            return null;
        }

        Page<SysJob> page = new Page<SysJob>();

        page.setPages( sysJobVOPage.getPages() );
        page.setRecords( convertSysJobList( sysJobVOPage.getRecords() ) );
        page.setTotal( sysJobVOPage.getTotal() );
        page.setSize( sysJobVOPage.getSize() );
        page.setCurrent( sysJobVOPage.getCurrent() );
        page.setSearchCount( sysJobVOPage.isSearchCount() );
        page.setOptimizeCountSql( sysJobVOPage.isOptimizeCountSql() );
        List<OrderItem> list1 = sysJobVOPage.getOrders();
        if ( list1 != null ) {
            page.setOrders( new ArrayList<OrderItem>( list1 ) );
        }
        page.setMaxLimit( sysJobVOPage.getMaxLimit() );
        page.setCountId( sysJobVOPage.getCountId() );

        return page;
    }
}