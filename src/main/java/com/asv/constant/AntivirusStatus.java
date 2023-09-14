package com.asv.constant;

public enum AntivirusStatus {
    NOT_SCANNED(0, "未杀毒"),
    SCANNING(1, "已归还待查杀"),
    SCANNED(2, "已杀毒"),
    WARNING_SCANNED(3, "＆已杀毒");

    private final int value;
    private final String description;

    AntivirusStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionByValue(int value) {
        for (AntivirusStatus status : AntivirusStatus.values()) {
            if (status.getValue() == value) {
                return status.getDescription();
            }
        }
        return null; // 如果找不到对应的value，则返回null或者其他合适的默认值
    }
}
