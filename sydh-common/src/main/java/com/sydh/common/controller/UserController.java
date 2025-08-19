package com.sydh.common.controller;


import com.sydh.common.client.UserClient;
import com.sydh.common.client.dto.CommonResult;
import com.sydh.common.client.dto.UserInfoRespDTO;
import com.sydh.common.client.dto.UserUpdateReqDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/sso-user")
@Api(tags = "统一平台用户")
public class UserController {
    @Resource
    private UserClient userClient;

    /**
     * 获得当前登录用户的基本信息
     *
     * @return 用户信息；注意，实际项目中，最好创建对应的 ResponseVO 类，只返回必要的字段
     */
    @GetMapping("/get")
    @ApiOperation("获得当前登录用户的基本信息")
    public CommonResult<UserInfoRespDTO> getUser() {
        return userClient.getUser();
    }

    /**
     * 更新当前登录用户的昵称
     *
     * @param nickname 昵称
     * @return 成功
     */
    @PutMapping("/update")
    public CommonResult<Boolean> updateUser(@RequestParam("nickname") String nickname) {
        UserUpdateReqDTO updateReqDTO = new UserUpdateReqDTO(nickname, null, null, null);
        return userClient.updateUser(updateReqDTO);
    }

//    @GetMapping("/test")
//    public CommonResult<String> test() {
//        System.out.println("----test");
//        CommonResult<String> aa = userClient.test();
//        System.out.println(aa.getData());
//        return aa;
//    }

//    @GetMapping("/sync-role")
//    public CommonResult<String> syncRole() {
//        CommonResult<Object> aa = userClient.syncRole();
//        return new CommonResult<String>().setData("aaaa");
//    }

//    /**
//     * 获取当前应用的所有角色列表
//     *
//     * @return
//     */
//    @GetMapping("/getRoleList")
//    public CommonResult<List<UserInfoRespDTO.Role>> getRoleList() {
//        CommonResult<List<UserInfoRespDTO.Role>> aa = userClient.getRoleList();
//        return aa;
//    }
}
