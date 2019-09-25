package kata.ex01;

import kata.ex01.model.HighwayDrive;
import kata.ex01.model.RouteType;
import kata.ex01.model.VehicleFamily;
import kata.ex01.util.HolidayUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author kawasima
 */
public class DiscountServiceImpl implements DiscountService {

    // Discount
    private final static int MIDNIGHT_DISCOUNT = 30;
    private final static int HOLIDAY_DISCOUNT = 30;
    private final static int WEEKDAY_DISCOUNT_30 = 30;
    private final static int WEEKDAY_DISCOUNT_50 = 50;

    @Override
    public long calc(HighwayDrive drive) {
        // 平日朝夕割引
        if (isWeekdayDiscountable(drive)) {
            int countPerMonth = drive.getDriver().getCountPerMonth();
            if (countPerMonth >= 10) {
                return WEEKDAY_DISCOUNT_50;
            }
            if (countPerMonth >= 5) {
                return WEEKDAY_DISCOUNT_30;
            }
        }
        // 休日割引
        if (isHolidayDiscount(drive)) {
            return HOLIDAY_DISCOUNT;
        }
        // 深夜割引
        if (isMidnightDiscount(drive)) {
            return MIDNIGHT_DISCOUNT;
        }
        return 0;
    }

    private boolean isMidnightDiscount(HighwayDrive drive) {
        LocalDateTime midnightStart, midnightEnd;
        if (drive.getEnteredAt().getHour() >= 0) {
            midnightStart = LocalDateTime.of(
                    LocalDate.from(drive.getEnteredAt().plusDays(1)),
                    LocalTime.of(0, 0));
            midnightEnd = LocalDateTime.of(
                    LocalDate.from(drive.getExitedAt().plusDays(1)),
                    LocalTime.of(4, 0));
        } else {
            midnightStart = LocalDateTime.of(
                    LocalDate.from(drive.getEnteredAt()),
                    LocalTime.of(0, 0));
            midnightEnd = LocalDateTime.of(
                    LocalDate.from(drive.getExitedAt()),
                    LocalTime.of(4, 0));
        }
        return drive.getEnteredAt().isBefore(midnightEnd) && drive.getEnteredAt().isAfter(midnightStart);
    }

    private boolean isHolidayDiscount(HighwayDrive drive) {
        LocalDate date = LocalDate.of(drive.getEnteredAt().getYear(), drive.getEnteredAt().getMonth(), drive.getEnteredAt().getDayOfMonth());
        return (VehicleFamily.STANDARD == drive.getVehicleFamily()
                        || VehicleFamily.MINI == drive.getVehicleFamily()
                        || VehicleFamily.MOTORCYCLE == drive.getVehicleFamily())
                    && HolidayUtils.isHoliday(date)
                    && RouteType.RURAL == drive.getRouteType();
    }

    private boolean isWeekdayDiscountable(HighwayDrive drive) {
        LocalDateTime morningStart, morningEnd;
        if (drive.getEnteredAt().getHour() >= 9) {
            morningStart = LocalDateTime.of(
                    LocalDate.from(drive.getEnteredAt().plusDays(1)),
                    LocalTime.of(6, 0));
            morningEnd = LocalDateTime.of(
                    LocalDate.from(drive.getExitedAt().plusDays(1)),
                    LocalTime.of(9, 0));
        } else {
            morningStart = LocalDateTime.of(
                    LocalDate.from(drive.getEnteredAt()),
                    LocalTime.of(6, 0));
            morningEnd = LocalDateTime.of(
                    LocalDate.from(drive.getExitedAt()),
                    LocalTime.of(9, 0));
        }

        LocalDateTime eveningStart, eveningEnd;
        if (drive.getEnteredAt().getHour() >= 17) {
            eveningStart = LocalDateTime.of(
                    LocalDate.from(drive.getEnteredAt().plusDays(1)),
                    LocalTime.of(17, 0));
            eveningEnd = LocalDateTime.of(
                    LocalDate.from(drive.getExitedAt().plusDays(1)),
                    LocalTime.of(20, 0));
        } else {
            eveningStart = LocalDateTime.of(
                    LocalDate.from(drive.getEnteredAt()),
                    LocalTime.of(17, 0));
            eveningEnd = LocalDateTime.of(
                    LocalDate.from(drive.getExitedAt()),
                    LocalTime.of(20, 0));
        }
        if (!HolidayUtils.isHoliday(morningStart.toLocalDate())
                && !HolidayUtils.isHoliday(eveningStart.toLocalDate())
                && ((drive.getEnteredAt().isBefore(morningEnd) && drive.getExitedAt().isAfter(morningStart))
                || (drive.getEnteredAt().isBefore(eveningEnd) && drive.getExitedAt().isAfter(eveningStart)))
                && drive.getRouteType() == RouteType.RURAL) {
            return true;
        }
        return false;
    }
}
