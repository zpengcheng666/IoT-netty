package com.sydh.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.system.convert.SysNoticeConvert;
import com.sydh.system.domain.SysNotice;
import com.sydh.system.domain.vo.SysNoticeVO;
import com.sydh.system.mapper.SysNoticeMapper;
import com.sydh.system.service.ISysNoticeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 公告 服务层实现
 *
 * @author ruoyi
 */
@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper,SysNotice> implements ISysNoticeService
{
    @Resource
    private SysNoticeMapper noticeMapper;

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId)
    {
        return noticeMapper.selectById(noticeId);
    }

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNoticeVO> selectNoticeList(SysNotice notice)
    {
        LambdaQueryWrapper<SysNotice> lqw = buildQueryWrapper(notice);
        List<SysNotice> sysNoticeList = noticeMapper.selectList(lqw);
        return SysNoticeConvert.INSTANCE.convertSysNoticeVOList(sysNoticeList);
    }

    /**
     * 查询通知公告分页列表
     *
     * @param sysNotice 通知公告
     * @return 通知公告
     */
    @Override
    public Page<SysNoticeVO> pageSysNoticeVO(SysNotice sysNotice) {
        LambdaQueryWrapper<SysNotice> lqw = buildQueryWrapper(sysNotice);
        lqw.orderByDesc(SysNotice::getCreateTime);
        Page<SysNotice> sysNoticePage = baseMapper.selectPage(new Page<>(sysNotice.getPageNum(), sysNotice.getPageSize()), lqw);
        return SysNoticeConvert.INSTANCE.convertSysNoticeVOPage(sysNoticePage);
    }

    private LambdaQueryWrapper<SysNotice> buildQueryWrapper(SysNotice query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SysNotice> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(query.getNoticeTitle()), SysNotice::getNoticeTitle, query.getNoticeTitle());
        lqw.eq(StringUtils.isNotBlank(query.getNoticeType()), SysNotice::getNoticeType, query.getNoticeType());
        lqw.eq(StringUtils.isNotBlank(query.getNoticeContent()), SysNotice::getNoticeContent, query.getNoticeContent());
        lqw.eq(query.getStatus() != null, SysNotice::getStatus, query.getStatus());
        lqw.like(StringUtils.isNotBlank(query.getCreateBy()), SysNotice::getCreateBy, query.getCreateBy());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SysNotice::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNotice notice)
    {
        return noticeMapper.insert(notice);
    }

    /**
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNotice notice)
    {
        notice.setUpdateTime(DateUtils.getNowDate());
        return noticeMapper.updateById(notice);
    }

    /**
     * 删除公告对象
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long noticeId)
    {
        return noticeMapper.deleteById(noticeId);
    }

    /**
     * 批量删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds)
    {
        return noticeMapper.deleteBatchIds(Arrays.asList(noticeIds));
    }
}
