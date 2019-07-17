package it.disco.unimib.suggester.model;

public enum LanguageType {
    EN("en"),
    FR("fr"),
    IT("it"),
    DE("de"),
    ES("es"),
    si("si");

    private String language;

    LanguageType(String lang) {
        this.language = lang;
    }

    public static LanguageType fromName(String x) throws Exception {
        for (LanguageType currentType : LanguageType.values()) {
            if (x.equals(currentType.getLanguage())) {
                return currentType;
            }
        }
        throw new Exception("Unmatched Type: " + x);
    }

    public static void main(String[] args) throws Exception {
        LanguageType lang = fromName("en");
        System.out.println(lang);

        System.out.println(LanguageType.valueOf("EN"));

    }

    public String getLanguage() {
        return language;
    }


}
