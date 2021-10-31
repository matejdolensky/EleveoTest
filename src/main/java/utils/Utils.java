package utils;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

import static enums.MonthsEnum.*;


/**
 * Helper utility class
 */
@UtilityClass
@Slf4j
public class Utils {


    public static String connectionDate = findNearestMondayDay() + "." + getNearestMondayMonthValue() + ".";
    public static String departureCityName;
    public static String arrivalCityName;

    private static String getMonthName(int monthNumber) {
        return switch (monthNumber) {
            case 1 -> JANUARY.getMonthName();
            case 2 -> FEBRUARY.getMonthName();
            case 3 -> MARCH.getMonthName();
            case 4 -> APRIL.getMonthName();
            case 5 -> MAY.getMonthName();
            case 6 -> JUNE.getMonthName();
            case 7 -> JULY.getMonthName();
            case 8 -> AUGUST.getMonthName();
            case 9 -> SEPTEMBER.getMonthName();
            case 10 -> OCTOBER.getMonthName();
            case 11 -> NOVEMBER.getMonthName();
            case 12 -> DECEMBER.getMonthName();
            default -> "Invalid month selector!";
        };
    }

    /**
     * @return nearest Monday in Month and year format
     */
    public static String findNearestMondayMonthAndYear() {
        int monthNumber = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).getMonthValue();
        return getMonthName(monthNumber) + " " + LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).getYear();
    }

    /**
     * @return nearest Monday in day format
     */
    public static String findNearestMondayDay() {
        return String.valueOf(LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).getDayOfMonth());
    }

    private static String getNearestMondayMonthValue() {
        return String.valueOf(LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).getMonthValue());
    }


    /**
     * Helper method which waits for 4s for visibility of desired elements and won't throw element not found exception
     *
     * @param element - element which visibility should be wait for
     * @return - elements visibility on page
     */
    public boolean waitForVisibilityOfElement(SelenideElement element) {
        try {
            element.shouldBe(Condition.visible);
            return true;
        } catch (com.codeborne.selenide.ex.ElementNotFound e) {
            log.debug("Element is not visible on the page.");
            log.debug(e.getMessage(), e);
            return false;
        }
    }

    /**
     * @return nearest Monday in format for BE tests
     */
    public static String findNearestMondayForBE() {
        return LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
