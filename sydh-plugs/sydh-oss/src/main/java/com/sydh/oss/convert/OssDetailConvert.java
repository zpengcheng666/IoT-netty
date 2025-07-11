package com.sydh.oss.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.oss.domain.OssDetail;
import com.sydh.oss.vo.OssDetailVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 文件记录Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-19
 */
@Mapper
public interface OssDetailConvert
{

    OssDetailConvert INSTANCE = Mappers.getMapper(OssDetailConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param ossDetail
     * @return 文件记录集合
     */
    OssDetailVO convertOssDetailVO(OssDetail ossDetail);

    /**
     * VO类转换为实体类集合
     *
     * @param ossDetailVO
     * @return 文件记录集合
     */
    OssDetail convertOssDetail(OssDetailVO ossDetailVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param ossDetailList
     * @return 文件记录集合
     */
    List<OssDetailVO> convertOssDetailVOList(List<OssDetail> ossDetailList);

    /**
     * VO类转换为实体类
     *
     * @param ossDetailVOList
     * @return 文件记录集合
     */
    List<OssDetail> convertOssDetailList(List<OssDetailVO> ossDetailVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param ossDetailPage
     * @return 文件记录分页
     */
    Page<OssDetailVO> convertOssDetailVOPage(Page<OssDetail> ossDetailPage);

    /**
     * VO类转换为实体类
     *
     * @param ossDetailVOPage
     * @return 文件记录分页
     */
    Page<OssDetail> convertOssDetailPage(Page<OssDetailVO> ossDetailVOPage);
}
