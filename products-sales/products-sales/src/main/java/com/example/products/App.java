package com.example.products;

import com.example.products.data.db.Database;
import com.example.products.data.repository.SalesRepository;
import com.example.products.service.*;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

public class App {

  public static void main(String[] args) throws Exception {

    Path csvPath = Path.of("products.csv");
    Path dbPath = Path.of("products.db");
    Path resultFile = Path.of("results.txt");

    Database db = new Database(dbPath);
    db.init();

    SalesRepository repo = new SalesRepository(db);
    new CsvImportService(repo).importCsv(csvPath);

    AnalyticsService analytics = new AnalyticsService(db);

    StringBuilder out = new StringBuilder();

    out.append("Задание 1: Продажи по регионам\n");

    var t1 = analytics.unitsByRegion();
    for (var x : t1) {
      out.append(String.format("%-35s %10d%n", x.region(), x.totalUnits()));
    }

    new ChartService().saveChart(t1, Path.of("task1_units_by_region.png"));
    out.append("\nГрафик сохранен: task1_units_by_region.png\n\n");
    out.append("----------------------------------------\n");

    out.append("Задание 2: Топ стран по доходу Европа и Азия\n");

    var t2 = analytics.topCountryEuropeAsia();
    out.append(String.format("%s : %.2f%n%n", t2.country(), t2.totalProfit()));
    out.append("----------------------------------------\n");

    out.append("Задание 3: Доход 420k–440k\n");
    out.append("Регионы: Ближний Восток и Северная Африка, Субсахарская Африка\n");

    var t3 = analytics.countryInProfitRange();
    if (t3 == null) {
      out.append("Ответ: Страны не найдены\n");
    } else {
      out.append(String.format("%s (%s) : %.2f%n",
          t3.country(), t3.region(), t3.totalProfit()));
    }

    out.append("\nВсе задания выполнены успешно\n");

    Files.writeString(resultFile, out.toString(), StandardCharsets.UTF_8);
  }
}
