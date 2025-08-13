package pl.com.api.util;

import pl.com.api.model.AccessLevel;
import pl.com.api.model.MedicalDataType;
import pl.com.api.model.UserRole;

import java.util.List;
import java.util.stream.Collectors;

public class EnumUtils {

    // -------- UserRole --------

    public static List<UserRole> userRolesFromCodes(List<Integer> codes) {
        return codes.stream()
                .map(UserRole::fromCode)
                .collect(Collectors.toList());
    }

    public static List<String> userRoleDisplayNamesFromCodes(List<Integer> codes) {
        return userRolesFromCodes(codes).stream()
                .map(UserRole::getDisplayName)
                .collect(Collectors.toList());
    }

    // -------- AccessLevel --------

    public static List<AccessLevel> accessLevelsFromCodes(List<Integer> codes) {
        return codes.stream()
                .map(AccessLevel::fromCode)
                .collect(Collectors.toList());
    }

    public static List<String> accessLevelDisplayNamesFromCodes(List<Integer> codes) {
        return accessLevelsFromCodes(codes).stream()
                .map(AccessLevel::getDisplayName)
                .collect(Collectors.toList());
    }

    // -------- MedicalDataType --------

    public static List<MedicalDataType> medicalDataTypesFromCodes(List<Integer> codes) {
        return codes.stream()
                .map(MedicalDataType::fromCode)
                .collect(Collectors.toList());
    }

    public static List<String> medicalDataTypeDisplayNamesFromCodes(List<Integer> codes) {
        return medicalDataTypesFromCodes(codes).stream()
                .map(MedicalDataType::getDisplayName)
                .collect(Collectors.toList());
    }

    public static byte parseIntToByte(int value) {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
            throw new IllegalArgumentException("Value out of range for byte: " + value);
        }
        return (byte) value;
    }

    public static MedicalDataType getMedicalDataTypeFromInt(int value) {
        byte code = parseIntToByte(value);
        return MedicalDataType.fromCode(code);
    }

    public static byte toByte(MedicalDataType type) {
        return parseIntToByte(type.getCode());
    }

    public static byte toByte(AccessLevel accessLevel) {
        return parseIntToByte(accessLevel.getCode());
    }

    public static byte toByte(UserRole role) {
        return parseIntToByte(role.getCode());
    }


}

