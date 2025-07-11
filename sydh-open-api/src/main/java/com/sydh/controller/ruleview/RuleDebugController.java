package com.sydh.controller.ruleview;

import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.extend.core.controller.BaseController;
import com.sydh.rule.parser.entity.FlowDataVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ruleview/debug")
@Api(tags = "规则调试")
public class RuleDebugController extends BaseController {

    @PostMapping("/startingNodes")
    @PreAuthorize("@ss.hasPermi('rule:debug:edit')")
    @ApiOperation("获取开始节点集合")
    public AjaxResult startingNodes(@RequestBody FlowDataVo data) throws Exception {
        return success();
    }

    @PostMapping("/status")
    @PreAuthorize("@ss.hasPermi('rule:debug:edit')")
    @ApiOperation("规则状态")
    public AjaxResult status(@RequestBody FlowDataVo data) throws Exception {
        return success();
    }

    @PostMapping("/debug")
    @PreAuthorize("@ss.hasPermi('rule:debug:edit')")
    @ApiOperation("规则调试")
    public AjaxResult debug(@RequestBody FlowDataVo data) throws Exception {
        return success();
    }

    @PostMapping("/log")
    @PreAuthorize("@ss.hasPermi('rule:debug:edit')")
    @ApiOperation("调试日志")
    public AjaxResult log(@RequestBody FlowDataVo data) throws Exception {
        return success();
    }

}
