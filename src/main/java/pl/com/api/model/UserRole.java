package pl.com.api.model;

public enum UserRole {
    ADMIN(0, "Administrator"),
    DOCTOR(1, "Lekarz"),
    NURSE(2, "Pielęgniarka"),
    PATIENT(3, "Pacjent"),
    RESEARCHER(4, "Badacz");

    private final int code;
    private final String displayName;

    UserRole(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserRole fromCode(int code) {
        for (UserRole role : values()) {
            if (role.code == code) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown UserRole code: " + code);
    }
}
