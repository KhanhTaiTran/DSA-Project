package enums;

public enum numsOfGrid {
    FOUR_BY_FOUR, SIX_BY_SIX, EIGHT_BY_EIGHT;

    public static numsOfGrid fromString(String str) {
        switch (str.toUpperCase()) {
            case "FOUR_BY_FOUR":
                return FOUR_BY_FOUR;
            case "SIX_BY_SIX":
                return SIX_BY_SIX;
            case "EIGHT_BY_EIGHT":
                return EIGHT_BY_EIGHT;
            default:
                throw new IllegalArgumentException("Invalid grid size: " + str);
        }
    }
}
