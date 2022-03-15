package util;

import java.time.LocalDate;

public class Util {

    /**
     * Defines the base date to begin choosing daily words from. The number of days from this date gives us the word_id
     * to be retrieved from the database.
     */
    public static final LocalDate BASE_DATE = LocalDate.parse("2022-02-01");

    public static int sumAll(int... nums) {

        int result = 0;
        for (int i = 0; i < nums.length; i++) {
            result += nums[i];
        }
        return result;

    }

    public static int largestInt(int... nums) {

        int result = 0;
        for (int i = 0; i < nums.length; i++) {

            if (nums[i] > result) {
                result = nums[i];
            }

        }
        return result;
    }

}
