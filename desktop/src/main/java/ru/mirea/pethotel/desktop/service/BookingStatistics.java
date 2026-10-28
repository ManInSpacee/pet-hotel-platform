package ru.mirea.pethotel.desktop.service;

import ru.mirea.pethotel.desktop.dto.BookingDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class BookingStatistics {
    public int countBookings(List<BookingDto> bookings) {
        int count = 0;
        for (BookingDto b : bookings) {
            count++;
        }
        return count;
    }

    public long countBookingsStream(List<BookingDto> bookings) {
        long count = bookings.stream().count();
        return count;
    }

    public int countBookingsNow (List<BookingDto> bookings) {
        int count = 0;
        for (BookingDto b : bookings) {
            if (b.stage().equals("IN_HOTEL")) {
                count++;
            }
        }
        return count;
    }

    public long countBookingsNowStream (List<BookingDto> bookings) {
        return bookings.stream().filter(b -> b.stage().equals("IN_HOTEL")).count();
    }

    public Map<String, Integer> countBookingsByStatus (List<BookingDto> bookings) {
        Map<String, Integer> map = new HashMap<>();
        for (BookingDto b : bookings) {
            String status = b.status();
            map.put(status, map.getOrDefault(status, 0) + 1);
        }
        return map;
    }
    public Map<String, Long> countBookingsByStatusStream (List<BookingDto> bookings) {
        Map<String, Long> countStatus = bookings.stream().collect(Collectors.groupingBy(BookingDto::status, Collectors.counting()));
        return countStatus;
    }

    public BigDecimal minCost (List<BookingDto> bookings) {
        if(bookings.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal min = bookings.getFirst().totalPrice();
        for(BookingDto b : bookings) {
            if(b.totalPrice().compareTo(min) < 0) {
                min = b.totalPrice();
            }
        }
        return min;
    }
    public BigDecimal maxCost (List<BookingDto> bookings) {
        if(bookings.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal max = bookings.getFirst().totalPrice();
        for(BookingDto b : bookings) {
            if(b.totalPrice().compareTo(max) > 0) {
                max = b.totalPrice();
            }
        }
        return max;
    }
    public BigDecimal minCostStream(List<BookingDto> bookings) {
        BigDecimal minCost = bookings.stream().map(BookingDto::totalPrice).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        return minCost;
    }
    public BigDecimal maxCostStream(List<BookingDto> bookings) {
        BigDecimal maxCost = bookings.stream().map(BookingDto::totalPrice).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        return maxCost;
    }

    public BigDecimal meanCost (List<BookingDto> bookings) {
        if(bookings.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal mean;
        for(BookingDto b : bookings) {
            sum = sum.add(b.totalPrice());

        }
        mean = sum.divide(BigDecimal.valueOf(bookings.size()), 2, RoundingMode.HALF_UP);
        return mean;
    }

    public BigDecimal meanCostStream(List<BookingDto> bookings) {
        if(bookings.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal result = bookings.stream().map(BookingDto::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(bookings.size()), 2, RoundingMode.HALF_UP);
        return result;
    }

    public double specialCareRate(List<BookingDto> bookings) {
        if(bookings.isEmpty()) {
            return 0.0;
        }
        double count = 0;
        for(BookingDto b : bookings) {
            if(b.specialCare()) {
                count++;
            }
        }
        double result = (count * 100.0) / bookings.size();
        return result;
    }

    public double specialCareRateStream (List<BookingDto> bookings) {
        if(bookings.isEmpty()) {
            return 0.0;
        }
        double count = bookings.stream().filter(BookingDto::specialCare).count();
        double result = (count * 100.0) / bookings.size();
        return result;
    }
}
