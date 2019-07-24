package it.disco.unimib.suggester.model.translation;

public enum LanguageType {
    EN("en"),
    FR("fr"),
    IT("it"),
    DE("de"),
    ES("es"),
    SI("si"),
    UNKNOWN("unknown");


    private String language;

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

    public static void main(String[] args) {
        LanguageType lang = fromName("en");
        System.out.println(lang);

        System.out.println(LanguageType.valueOf("EN"));

    }

    public String getLanguage() {
        return language;
    }


}
