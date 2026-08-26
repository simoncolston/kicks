package org.colston.kicks.render;

public class RendererResources {
    private static final String[][] VALUES =
            {
                    {"◯", "", "", "", "", "", "", "", ""},
                    {"合", "乙", "老", "下老", "ﾛ上", "ﾛ中", "ﾛ尺", "ｲ合", "ｲ乙"},
                    {"四", "上", "中", "尺", "下尺", "ﾛ五", "ｲ老", "ｲ四", "ｲ上"},
                    {"工", "五", "六", "七", "八", "九", "ｲ尺", "ｲ工", "ｲ五"}
            };
    private static final String[] FINGER_VALUES = {"", "①", "②", "③", "④"};

    public static String getNoteText(int string, int placement) {
        return VALUES[string][placement];
    }

    public static String getNoteFingerText(int finger) {
        return FINGER_VALUES[finger];
    }

}
