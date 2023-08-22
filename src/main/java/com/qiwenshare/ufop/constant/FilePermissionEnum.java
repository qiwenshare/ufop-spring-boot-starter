package com.qiwenshare.ufop.constant;

/**
 * @author MAC
 * @version 1.0
 */
public enum FilePermissionEnum {
    NO("0", "无权限"),
    READ("1", "读取"),
    READ_WRITE("2", "读取/写入"),
    OWNER("3", "所有者");

    private final String type;
    private final String desc;
    FilePermissionEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
