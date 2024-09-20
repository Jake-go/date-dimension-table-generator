package com.golle.datedimension.services;

import com.golle.datedimension.beans.DateDimension;
import com.golle.datedimension.repositories.DateDimensionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DateDimensionService {

    private final DateDimensionRepository dateDimensionRepository;

    private static LocalDate calculateEasterSunday(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int n = h + l - 7 * m + 114;
        int month = n / 31;
        int day = (n % 31) + 1;
        return LocalDate.of(year, month, day);
    }



    private boolean isCanadianHoliday(LocalDate date) {
        int year = date.getYear();
        Month month = date.getMonth();
        int day = date.getDayOfMonth();

        // New Year's Day
        if(month.getValue() == 1  && day == 1) return true;

        //Good Friday
        if(date.plusDays(2) == calculateEasterSunday(date.getYear())) return true;

        //Easter Monday
        if(date.minusDays(1) == calculateEasterSunday(date.getYear())) return true;

        //Victoria Day
        LocalDate victoriaDay = LocalDate.of(year, Month.MAY, 25).with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        if(date.equals(victoriaDay)) return true;

        //Canada Day
        LocalDate canadaDay = LocalDate.of(year, Month.JULY, 1);
        if(date.equals(canadaDay)) return true;

        LocalDate civicDay = LocalDate.of(year, Month.AUGUST, 1).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        if(date.equals(civicDay)) return true;

        LocalDate labourDay = LocalDate.of(year, Month.SEPTEMBER, 1).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        if(date.equals(labourDay)) return true;

        LocalDate thanksgiving = LocalDate.of(year, Month.OCTOBER, 8).with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        if(date.equals(thanksgiving)) return true;

        LocalDate remembranceDay = LocalDate.of(year, Month.NOVEMBER, 11);
        if(remembranceDay.getDayOfWeek().equals(DayOfWeek.SATURDAY) || remembranceDay.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            remembranceDay = remembranceDay.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        }
        if(date.equals(remembranceDay)) return true;

        LocalDate christmas = LocalDate.of(year, Month.DECEMBER, 25);
        if (date.equals(christmas)) return true;

        return date.equals(christmas.plusDays(1));
    }

    private void updateHolidayIndicator(LocalDate date) {
        Optional<DateDimension> dateDimension = dateDimensionRepository.findById(convertToPrimaryKey(date));
        if(dateDimension.isPresent()) {
            DateDimension dateDim = dateDimension.get();
            dateDim.setHolidayIndicator("Holiday");
            dateDimensionRepository.save(dateDim);
        }
    }

    private String convertToPrimaryKey(LocalDate date) {
        DateTimeFormatter keyFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        return date.format(keyFormat);
    }

    @Transactional
    public void updateHolidayIndicators(LocalDate start, LocalDate end) {
        for(LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){
            if(isCanadianHoliday(date)) {
                updateHolidayIndicator(date);
            }
        }
    }
}
