package com.sydh.system.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.system.domain.SysNotice;
import com.sydh.system.domain.vo.SysNoticeVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 通知公告Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-26
 */
@Mapper
public interface SysNoticeConvert
{

    SysNoticeConvert INSTANCE = Mappers.getMapper(SysNoticeConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sysNotice
     * @return 通知公告集合
     */
    SysNoticeVO convertSysNoticeVO(SysNotice sysNotice);

    /**
     * VO类转换为实体类集合
     *
     * @param sysNoticeVO
     * @return 通知公告集合
     */
    SysNotice convertSysNotice(SysNoticeVO sysNoticeVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sysNoticeList
     * @return 通知公告集合
     */
    List<SysNoticeVO> convertSysNoticeVOList(List<SysNotice> sysNoticeList);

    /**
     * VO类转换为实体类
     *
     * @param sysNoticeVOList
     * @return 通知公告集合
     */
    List<SysNotice> convertSysNoticeList(List<SysNoticeVO> sysNoticeVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sysNoticePage
     * @return 通知公告分页
     */
    Page<SysNoticeVO> convertSysNoticeVOPage(Page<SysNotice> sysNoticePage);

    /**
     * VO类转换为实体类
     *
     * @param sysNoticeVOPage
     * @return 通知公告分页
     */
    Page<SysNotice> convertSysNoticePage(Page<SysNoticeVO> sysNoticeVOPage);
}
