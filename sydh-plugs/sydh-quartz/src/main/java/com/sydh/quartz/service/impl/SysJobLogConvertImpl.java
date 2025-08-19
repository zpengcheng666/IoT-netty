package com.sydh.quartz.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.quartz.convert.SysJobLogConvert;
import com.sydh.quartz.domain.SysJobLog;
import com.sydh.quartz.vo.SysJobLogVO;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-25T14:03:18+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 1.8.0_221 (Oracle Corporation)"
)
public class SysJobLogConvertImpl implements SysJobLogConvert {

    @Override
    public SysJobLogVO convertSysJobLogVO(SysJobLog sysJobLog) {
        if ( sysJobLog == null ) {
            return null;
        }

        SysJobLogVO sysJobLogVO = new SysJobLogVO();

        sysJobLogVO.setPageNum( sysJobLog.getPageNum() );
        sysJobLogVO.setPageSize( sysJobLog.getPageSize() );
        Map<String, Object> map = sysJobLog.getParams();
        if ( map != null ) {
            sysJobLogVO.setParams( new LinkedHashMap<String, Object>( map ) );
        }

        return sysJobLogVO;
    }

    @Override
    public SysJobLog convertSysJobLog(SysJobLogVO sysJobLogVO) {
        if ( sysJobLogVO == null ) {
            return null;
        }

        SysJobLog sysJobLog = new SysJobLog();

        sysJobLog.setPageNum( sysJobLogVO.getPageNum() );
        sysJobLog.setPageSize( sysJobLogVO.getPageSize() );
        Map<String, Object> map = sysJobLogVO.getParams();
        if ( map != null ) {
            sysJobLog.setParams( new LinkedHashMap<String, Object>( map ) );
        }

        return sysJobLog;
    }

    @Override
    public List<SysJobLogVO> convertSysJobLogVOList(List<SysJobLog> sysJobLogList) {
        if ( sysJobLogList == null ) {
            return null;
        }

        List<SysJobLogVO> list = new ArrayList<SysJobLogVO>( sysJobLogList.size() );
        for ( SysJobLog sysJobLog : sysJobLogList ) {
            list.add( convertSysJobLogVO( sysJobLog ) );
        }

        return list;
    }

    @Override
    public List<SysJobLog> convertSysJobLogList(List<SysJobLogVO> sysJobLogVOList) {
        if ( sysJobLogVOList == null ) {
            return null;
        }

        List<SysJobLog> list = new ArrayList<SysJobLog>( sysJobLogVOList.size() );
        for ( SysJobLogVO sysJobLogVO : sysJobLogVOList ) {
            list.add( convertSysJobLog( sysJobLogVO ) );
        }

        return list;
    }

    @Override
    public Page<SysJobLogVO> convertSysJobLogVOPage(Page<SysJobLog> sysJobLogPage) {
        if ( sysJobLogPage == null ) {
            return null;
        }

        Page<SysJobLogVO> page = new Page<SysJobLogVO>();

        page.setPages( sysJobLogPage.getPages() );
        page.setRecords( convertSysJobLogVOList( sysJobLogPage.getRecords() ) );
        page.setTotal( sysJobLogPage.getTotal() );
        page.setSize( sysJobLogPage.getSize() );
        page.setCurrent( sysJobLogPage.getCurrent() );
        page.setSearchCount( sysJobLogPage.isSearchCount() );
        page.setOptimizeCountSql( sysJobLogPage.isOptimizeCountSql() );
        List<OrderItem> list1 = sysJobLogPage.getOrders();
        if ( list1 != null ) {
            page.setOrders( new ArrayList<OrderItem>( list1 ) );
        }
        page.setMaxLimit( sysJobLogPage.getMaxLimit() );
        page.setCountId( sysJobLogPage.getCountId() );

        return page;
    }

    @Override
    public Page<SysJobLog> convertSysJobLogPage(Page<SysJobLogVO> sysJobLogVOPage) {
        if ( sysJobLogVOPage == null ) {
            return null;
        }

        Page<SysJobLog> page = new Page<SysJobLog>();

        page.setPages( sysJobLogVOPage.getPages() );
        page.setRecords( convertSysJobLogList( sysJobLogVOPage.getRecords() ) );
        page.setTotal( sysJobLogVOPage.getTotal() );
        page.setSize( sysJobLogVOPage.getSize() );
        page.setCurrent( sysJobLogVOPage.getCurrent() );
        page.setSearchCount( sysJobLogVOPage.isSearchCount() );
        page.setOptimizeCountSql( sysJobLogVOPage.isOptimizeCountSql() );
        List<OrderItem> list1 = sysJobLogVOPage.getOrders();
        if ( list1 != null ) {
            page.setOrders( new ArrayList<OrderItem>( list1 ) );
        }
        page.setMaxLimit( sysJobLogVOPage.getMaxLimit() );
        page.setCountId( sysJobLogVOPage.getCountId() );

        return page;
    }
}
