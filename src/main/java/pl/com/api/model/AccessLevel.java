package pl.com.api.model;

public enum AccessLevel {
    READ(0, "Odczyt"),
    WRITE(1, "Zapis"),
    UPDATE(2, "Aktualizacja"),
    DELETE(3, "Usuwanie"),
    GRANT_ACCESS(4, "Nadawanie uprawnień");

    private final int code;
    private final String displayName;

    AccessLevel(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AccessLevel fromCode(int code) {
        for (AccessLevel level : values()) {
            if (level.code == code) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown AccessLevel code: " + code);
    }
}

