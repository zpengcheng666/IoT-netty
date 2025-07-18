package com.sydh.controller.media;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.annotation.Log;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.page.TableDataInfo;
import com.sydh.common.enums.BusinessType;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.common.extend.utils.poi.ExcelUtil;
import com.sydh.common.utils.MessageUtils;
import com.sydh.sip.domain.MediaServer;
import com.sydh.sip.model.vo.MediaServerVO;
import com.sydh.sip.service.IMediaServerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 流媒体服务器配置Controller
 *
 * @author zhuangpeng.li
 * @date 2022-11-30
 */
@Api(tags = "流媒体服务器配置")
@RestController
@RequestMapping("/sip/mediaserver")
public class MediaServerController extends BaseController
{
    @Autowired
    private IMediaServerService mediaServerService;

    /**
     * 查询流媒体服务器配置列表
     */
    @ApiOperation("查询流媒体服务器配置列表")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @GetMapping("/list")
    public TableDataInfo list(MediaServer mediaServer)
    {
        Page<MediaServerVO> mediaServerPage = mediaServerService.pageMediaServerVO(mediaServer);
        return getDataTable(mediaServerPage.getRecords(), mediaServerPage.getTotal());
    }

    /**
     * 导出流媒体服务器配置列表
     */
    @ApiOperation("导出流媒体服务器配置列表")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @Log(title = "流媒体服务器配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MediaServer mediaServer)
    {
//        List<MediaServer> list = mediaServerService.selectMediaServerList(mediaServer);
        Page<MediaServerVO> mediaServerPage = mediaServerService.pageMediaServerVO(mediaServer);
        ExcelUtil<MediaServerVO> util = new ExcelUtil<MediaServerVO>(MediaServerVO.class);
        util.exportExcel(response, mediaServerPage.getRecords(), "流媒体服务器配置数据");
    }

    /**
     * 获取流媒体服务器配置详细信息,只获取第一条
     */
    @ApiOperation(value = "获取流媒体服务器配置详细信息", notes = "只获取第一条")
    @PreAuthorize("@ss.hasPermi('iot:video:query')")
    @GetMapping()
    public AjaxResult getInfo()
    {
        List<MediaServer> list=mediaServerService.selectMediaServer();
        if(list==null || list.size()==0){
            MediaServer mediaServer=new  MediaServer();
            // 设置默认值
            mediaServer.setEnabled(1L);
            mediaServer.setDomainAlias("");
            mediaServer.setIp("");
            mediaServer.setPortHttp(8082L);
            mediaServer.setPortHttps(8443L);
            mediaServer.setPortRtmp(1935L);
            mediaServer.setPortRtsp(554L);
            mediaServer.setProtocol("HTTP");
            mediaServer.setSecret("035c73f7-bb6b-4889-a715-d9eb2d192xxx");
            mediaServer.setRtpPortRange("30000,30500");
            list=new ArrayList<>();
            list.add(mediaServer);
        }
        return AjaxResult.success(list.get(0));
    }

    /**
     * 新增流媒体服务器配置
     */
    @ApiOperation("新增流媒体服务器配置")
    @PreAuthorize("@ss.hasPermi('iot:video:add')")
    @Log(title = "流媒体服务器配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MediaServer mediaServer)
    {
        return toAjax(mediaServerService.insertMediaServer(mediaServer));
    }

    /**
     * 修改流媒体服务器配置
     */
    @ApiOperation("修改流媒体服务器配置")
    @PreAuthorize("@ss.hasPermi('iot:video:edit')")
    @Log(title = "流媒体服务器配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MediaServer mediaServer)
    {
        return toAjax(mediaServerService.updateMediaServer(mediaServer));
    }

    /**
     * 删除流媒体服务器配置
     */
    @ApiOperation("删除流媒体服务器配置")
    @PreAuthorize("@ss.hasPermi('iot:video:remove')")
    @Log(title = "流媒体服务器配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(mediaServerService.deleteMediaServerByIds(ids));
    }

    @ApiOperation("获取流媒体服务器视频流信息列表")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @GetMapping("/mediaList/{schema}/{stream}")
    public AjaxResult getMediaList(@PathVariable String schema,
                                   @PathVariable String stream)
    {
        return AjaxResult.success(MessageUtils.message("success"), mediaServerService.getMediaList(schema,stream));
    }

    @ApiOperation("获取rtp推流端口列表")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @GetMapping("/listRtpServer")
    public AjaxResult listRtpServer()
    {
        return AjaxResult.success(MessageUtils.message("success"), mediaServerService.listRtpServer());
    }

    @ApiOperation("检验流媒体服务")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @GetMapping(value = "/check")
    public AjaxResult checkMediaServer(@RequestParam String ip, @RequestParam Long port, @RequestParam String secret) {
        return AjaxResult.success(MessageUtils.message("success"), mediaServerService.checkMediaServer(ip, port, secret));
    }
}
