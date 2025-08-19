package com.sydh.common.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoRespDTO {

    /**
     * 用户编号
     */
    private Long id;

    /**
     * 用户账号
     */
    private String username;

    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 用户性别
     */
    private Integer sex;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 所在部门
     */
    private Dept dept;

    /**
     * 所属岗位数组
     */
    private List<Post> posts;

    private List<Role> roles;

    /**
     * 部门
     */
    @Data
    public static class Dept {

        /**
         * 部门编号
         */
        private Long id;

        /**
         * 部门名称
         */
        private String name;

    }

    /**
     * 岗位
     */
    @Data
    public static class Post {

        /**
         * 岗位编号
         */
        private Long id;

        /**
         * 岗位名称
         */
        private String name;

    }

    @Data
    public static class Role {

        private String code;

        private String name;

    }
}
