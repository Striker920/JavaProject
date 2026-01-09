package com.example.products.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DateParser {
  private static final List<DateTimeFormatter> FMT = List.of(
      DateTimeFormatter.ofPattern("M/d/yyyy"),
      DateTimeFormatter.ofPattern("MM/dd/yyyy"),
      DateTimeFormatter.ofPattern("d.M.yyyy"),
      DateTimeFormatter.ofPattern("dd.MM.yyyy")
  );

  public static LocalDate parse(String raw) {
    String s = raw.trim();
    for (var f : FMT) {
      try { return LocalDate.parse(s, f); }
      catch (DateTimeParseException ignored) {}
    }
    throw new IllegalArgumentException("Неизвестный формат даты: " + raw);
  }
}
