package com.asv.constant;

public enum DeviceStatus {
    IN_STORE(0, "在库"),
    USING(1, "使用中"),
    SCRAPING(2, "报废中"),
    SCRAPPED(3, "已报废"),
    DELETING(4, "删除中");

    private final int value;
    private final String desc;

    DeviceStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    // 根据value获取对应的description
    public static String getDescriptionByValue(int value) {
        for (DeviceStatus status : DeviceStatus.values()) {
            if (status.getValue() == value) {
                return status.getDesc();
            }
        }
        return null; // 如果找不到对应的value，则返回null或者其他合适的默认值
    }
}
