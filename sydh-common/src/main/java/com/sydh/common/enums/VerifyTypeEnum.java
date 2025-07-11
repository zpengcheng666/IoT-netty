package com.sydh.common.enums;

/**
 * 验证类型枚举，用于区分不同的验证方式
 */
public enum VerifyTypeEnum {
    /**
     * 账号密码验证
     */
    PASSWORD(1, "账号密码验证"),

    /**
     * 短信验证码验证
     */
    SMS(2, "短信验证");

    private final int verifyType;
    private final String desc;

    VerifyTypeEnum(int verifyType, String desc) {
        if (desc == null) {
            throw new IllegalArgumentException("描述信息不能为 null");
        }
        this.verifyType = verifyType;
        this.desc = desc;
    }

    public int getVerifyType() {
        return verifyType;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据 verifyType 获取对应的枚举值
     * @param verifyType 验证类型编号
     * @return 对应的枚举值，若未找到则返回 null
     */
    public static VerifyTypeEnum fromVerifyType(int verifyType) {
        for (VerifyTypeEnum type : values()) {
            if (type.verifyType == verifyType) {
                return type;
            }
        }
        return null;
    }
}
