package enums;

/**
 * Months enum class, mainly as a helper for dealing with FE calendar
 */

public enum MonthsEnum {
    JANUARY("január"),
    FEBRUARY("február"),
    MARCH("marec"),
    APRIL("apríl"),
    MAY("máj"),
    JUNE("jún"),
    JULY("júl"),
    AUGUST("august"),
    SEPTEMBER("september"),
    OCTOBER("október"),
    NOVEMBER("november"),
    DECEMBER("december");

    /**
     * Name of the month in SK lover case
     */
    private final String monthName;

    MonthsEnum(String monthName) {
        this.monthName = monthName;
    }

    /**
     * returns name of the month for calendar selection purposes
     *
     * @return int
     */
    public String getMonthName() {
        return this.monthName;
    }
}
