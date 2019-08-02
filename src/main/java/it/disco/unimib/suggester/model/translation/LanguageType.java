package it.disco.unimib.suggester.model.translation;

import java.util.Arrays;

public enum LanguageType {
    EN("en"),
    FR("fr"),
    IT("it"),
    DE("de"),
    ES("es"),
    SI("si"),
    UNKNOWN("unknown");


    private final String language;

    LanguageType(String lang) {
        this.language = lang;
    }

    public static LanguageType fromName(String x) {
        for (LanguageType currentType : LanguageType.values()) {
            if (x.equals(currentType.getLanguage())) {
                return currentType;
            }
        }
        return LanguageType.UNKNOWN;
    }

    public static boolean checkSupportedLanguage(LanguageType type) {
        if (type == null) return false;
        return Arrays.asList(LanguageType.values()).contains(type);
    }

    public static void main(String[] args) {
        LanguageType lang = fromName("en");
        System.out.println(lang);

        System.out.println(LanguageType.valueOf("EN"));

    }

    private String getLanguage() {
        return language;
    }


}
