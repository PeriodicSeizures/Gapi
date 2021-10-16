package com.crazicrafter1.gapi;

public enum EnumResult {
    GRAB_ITEM,
    CLOSE,
    BACK,
    REFRESH,
    //TEXT, // TextMenu only
    OK;

    public static Object OPEN(AbstractMenu.Builder builder) {
        return builder;
    }

    public static Object TEXT(String text) {
        return text;
    }
}
