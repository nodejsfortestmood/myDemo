package com.strategy.enums;

public enum MarketType {
    SZ(0, "深市"),
    SH(1, "沪市"),
    BJ(2, "北交所");

    private final int code;
    private final String desc;

    MarketType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static MarketType of(String code) {
        if (code.contains(SZ.name())) {
            return SZ;
        } else if (code.contains(SH.name())) {
            return SH;
        } else {
            return BJ;
        }
    }
}
