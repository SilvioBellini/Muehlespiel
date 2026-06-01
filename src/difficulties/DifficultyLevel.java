package difficulties;

/**
 * An enumeration representing different levels of difficulty.
 * <p>
 * This enum provides predefined constants for representing
 * various difficulty levels including NONE, EASY, MEDIUM, and HARD.
 * It also includes functionality to map specific strategy classes
 * to their corresponding difficulty level.
 */
public enum DifficultyLevel {
    NONE,
    EASY,
    MEDIUM,
    HARD;

    /**
     * Maps a given strategy class to its corresponding difficulty level.
     * <p>
     * This method evaluates the simple name of the provided class
     * and returns the corresponding {@code DifficultyLevel} constant.
     * If the class does not match any known difficulty, it defaults to {@code NONE}.
     *
     * @param strategyClass the class representing a specific difficulty strategy
     * @return the corresponding {@code DifficultyLevel} constant for the given strategy class,
     * or {@code NONE} if no match is found
     */
    public static DifficultyLevel fromStrategyClass(Class<?> strategyClass) {
        return switch (strategyClass.getSimpleName()) {
            case "EasyDifficulty" -> EASY;
            case "MediumDifficulty" -> MEDIUM;
            case "HardDifficulty" -> HARD;
            default -> NONE;
        };
    }
}
