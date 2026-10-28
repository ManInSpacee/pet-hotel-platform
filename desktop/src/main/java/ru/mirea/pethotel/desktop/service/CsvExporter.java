package ru.mirea.pethotel.desktop.service;

import ru.mirea.pethotel.desktop.dto.BookingDto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvExporter {

    private static final String SEPARATOR = ";";
    private static final String BOM = "﻿";

    public void export(List<BookingDto> bookings, Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(BOM + String.join(SEPARATOR, "Код", "Питомец", "Владелец", "Вольер", "Начало", "Окончание",
                "Статус", "Стадия", "Стоимость", "Особый уход"));
        for (BookingDto b : bookings) {
            lines.add(String.join(SEPARATOR,
                    escape(b.code()),
                    escape(b.petName()),
                    escape(b.ownerName()),
                    String.valueOf(b.enclosureNumber()),
                    b.startDate().toString(),
                    b.endDate().toString(),
                    escape(b.status()),
                    escape(b.stage()),
                    b.totalPrice().toPlainString(),
                    b.specialCare() ? "да" : "нет"));
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(SEPARATOR) || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
