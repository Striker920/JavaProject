package com.example.products.service;

import com.example.products.data.repository.SalesRepository;
import com.opencsv.CSVReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class CsvImportService {

  private final SalesRepository repo;

  public CsvImportService(SalesRepository repo) {
    this.repo = repo;
  }

  public void importCsv(Path csvPath) throws Exception {

    try (InputStream is = CsvImportService.class.getClassLoader().getResourceAsStream(csvPath.getFileName().toString())) {
      if (is == null) {
        throw new FileNotFoundException("CSV not found: " + csvPath +
            ". Положи файл products.csv в src/main/resources/");
      }

      try (Reader rr = new InputStreamReader(is, StandardCharsets.UTF_8);
           CSVReader r = new CSVReader(rr)) {
        importFromReader(r);
      }
    }
  }

  private void importFromReader(CSVReader r) throws Exception {
    r.readNext();

    repo.beginTx();
    try {
      String[] row;
      while ((row = r.readNext()) != null) {

        if (row.length == 1 && row[0].contains("\t")) row = row[0].split("\t");

        String region = row[0].trim();
        String country = row[1].trim();
        int unitsSold = Integer.parseInt(row[6].trim());
        double totalProfit = Double.parseDouble(row[7].trim());

        repo.insertSale(region, country, unitsSold, totalProfit);
      }
      repo.commitTx();
    } catch (Exception ex) {
      repo.rollbackTx();
      throw ex;
    }
  }
}
