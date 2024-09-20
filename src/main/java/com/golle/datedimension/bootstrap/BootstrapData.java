package com.golle.datedimension.bootstrap;

import com.golle.datedimension.beans.DateDimension;
import com.golle.datedimension.repositories.DateDimensionRepository;
import com.golle.datedimension.services.DateDimensionService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;

@Component
@AllArgsConstructor
public class BootstrapData implements CommandLineRunner{

    private final DateDimensionRepository dateDimensionRepository;
    private final DateDimensionService dateDimensionService;

    private String getWeekDayOrWeekend(LocalDate date) {
        return date.getDayOfWeek()
                .query(day -> (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) ? "Weekend" : "Weekday");
    }

    private void insertIntoDB(LocalDate date) {
        DateTimeFormatter keyFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter fullDateFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy");

        DateDimension dateDimension = DateDimension.builder()
                .dateKey(date.format(keyFormat))
                .date(date)
                .fullDateDescription(date.format(fullDateFormat))
                .dayOfWeek(date.getDayOfWeek())
                .calendarMonth(date.getMonth())
                .calendarQuarter("Q" + date.get(IsoFields.QUARTER_OF_YEAR))
                .calendarYear(date.getYear())
                .fiscalYearMonth("F" + date.getYear() + "-" + date.getMonthValue())
                .holidayIndicator("Non-holiday")
                .weekdayIndicator(getWeekDayOrWeekend(date))
                .build();

        dateDimensionRepository.save(dateDimension);
        System.out.println(dateDimension + " Inserted");
    }

    @Override
    public void run(String... args) throws Exception {
        LocalDate startDate = LocalDate.of(2000, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        startDate.datesUntil(endDate).forEach(this::insertIntoDB);
        dateDimensionService.updateHolidayIndicators(startDate, endDate);
    }
}
