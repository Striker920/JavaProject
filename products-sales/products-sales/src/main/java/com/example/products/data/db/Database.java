package com.example.products.data.db;

import java.nio.file.Path;
import java.sql.*;

public class Database {

  private final String url;

  public Database(Path path) {
    this.url = "jdbc:sqlite:" + path.toAbsolutePath();
  }

  public Connection connect() throws SQLException {
    Connection c = DriverManager.getConnection(url);
    try (Statement s = c.createStatement()) {
      s.execute("PRAGMA foreign_keys = ON;");
    }
    return c;
  }

  public void init() throws SQLException {
    try (Connection c = connect();
         Statement s = c.createStatement()) {

      s.execute("""
        CREATE TABLE IF NOT EXISTS region (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          name TEXT NOT NULL UNIQUE
        );
      """);

      s.execute("""
        CREATE TABLE IF NOT EXISTS country (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          name TEXT NOT NULL,
          region_id INTEGER NOT NULL,
          FOREIGN KEY(region_id) REFERENCES region(id)
        );
      """);

      s.execute("""
        CREATE TABLE IF NOT EXISTS sales (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          country_id INTEGER NOT NULL,
          units_sold INTEGER NOT NULL,
          total_profit REAL NOT NULL,
          FOREIGN KEY(country_id) REFERENCES country(id)
        );
      """);
    }
  }
}
