package com.golle.datedimension.beans;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DateDimension {

    @Id
    private String dateKey;

    @Column
    private LocalDate date;

    @Column
    private String fullDateDescription;

    @Column
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column
    @Enumerated(EnumType.STRING)
    private Month calendarMonth;

    @Column
    private String calendarQuarter;

    @Column
    private int calendarYear;

    @Column
    private String fiscalYearMonth;

    @Column
    private String holidayIndicator;

    @Column
    private String weekdayIndicator;
}
