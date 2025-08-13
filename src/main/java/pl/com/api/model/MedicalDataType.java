package pl.com.api.model;

public enum MedicalDataType {
    ALL(0, "Wszystkie dane"),
    GENERAL_HEALTH(1, "Ogólny stan zdrowia"),
    BLOOD_TEST(2, "Badania krwi"),
    IMAGING(3, "Badania obrazowe"),
    ECG(4, "EKG"),
    PRESCRIPTIONS(5, "Recepty"),
    VACCINATIONS(6, "Szczepienia"),
    ALLERGIES(7, "Alergie"),
    GENETIC_DATA(8, "Dane genetyczne"),
    PSYCHOLOGICAL_DATA(9, "Dane psychologiczne");

    private final int code;
    private final String displayName;

    MedicalDataType(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static MedicalDataType fromCode(int code) {
        for (MedicalDataType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown MedicalDataType code: " + code);
    }
}
