package com.sydh.system.runner;


import com.sydh.common.client.OAuthClient;
import com.sydh.common.client.dto.CommonResult;
import com.sydh.common.client.dto.DeptInfoRespDTO;
import com.sydh.common.client.dto.UserInfoRespDTO;
import com.sydh.common.extend.core.domin.entity.SysDept;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.utils.StringUtils;
import com.sydh.system.service.ISysDeptService;
import com.sydh.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@Slf4j
@Order(5)
public class UserDeptSyncRunner implements CommandLineRunner {


    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private OAuthClient oAuthClient;

    @Value("${sso.sync-user-org-flag}")
    private boolean flag;

    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public void run(String... args) throws Exception {

        if(flag){
            threadPoolTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    //1.同步组织
                    CommonResult<List<DeptInfoRespDTO>> allOrg = oAuthClient.getAllOrg();
                    if(Objects.equals(allOrg.getCode(), 0)){
                        List<DeptInfoRespDTO> data = allOrg.getData();
                        List<SysDept> deptList = new CopyOnWriteArrayList<>();
                        for(DeptInfoRespDTO dto : data){
                            SysDept sysDept = new SysDept();
                            sysDept.setDeptId(dto.getId());
                            sysDept.setDeptName(dto.getName());
                            sysDept.setParentId(dto.getParentId());
                            sysDept.setOrderNum(dto.getSort());
                            sysDept.setStatus(0);
                            sysDept.setCreateTime(new Date());
                            deptList.add(sysDept);
                        }
                        boolean b = deptService.batchInsertDept(deptList);
                        if(b){
                            log.info("同步基础平台组织成功。。。");
                        }

                        //2.根据组织同步人员
                        List<SysUser> userList = new CopyOnWriteArrayList<>();
                        for(DeptInfoRespDTO dto : data){
                            Long deptId = dto.getId();
                            CommonResult<List<UserInfoRespDTO>> userByOrg = oAuthClient.getUserByOrg(deptId);
                            List<UserInfoRespDTO> users = userByOrg.getData();
                            if(users != null && !users.isEmpty()){
//                                List<SysUser> userSubList = new CopyOnWriteArrayList<>();
                                for (UserInfoRespDTO respDTO : users){
                                    SysUser sysUser = new SysUser();
                                    sysUser.setUserId(respDTO.getId());
                                    sysUser.setDeptId(deptId);
                                    sysUser.setUserName(respDTO.getUsername());
                                    sysUser.setNickName(respDTO.getNickname());
                                    sysUser.setSex(respDTO.getSex().toString());
                                    sysUser.setPassword("123456");
                                    sysUser.setEmail(respDTO.getEmail());
                                    sysUser.setPhonenumber(respDTO.getMobile());
                                    sysUser.setStatus(0);
                                    sysUser.setCreateBy("admin");
                                    sysUser.setCreateTime(new Date());
                                    sysUser.setRemark("同步");
                                    userList.add(sysUser);
                                }
                            }
                        }
                        boolean b1 = userService.batchInsertUser(userList);
                        if(b1){
                            log.info("同步基础平台人员成功。。。");
                        }

                    }

                }
            });
        }
    }
}
