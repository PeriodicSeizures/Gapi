package com.crazicrafter1.gapi;

public enum EnumResult {
    GRAB_ITEM,
    CLOSE,
    BACK,
    REFRESH,
    OK;

    public static Object OPEN(AbstractMenu.Builder builder) {
        return builder;
    }
}
