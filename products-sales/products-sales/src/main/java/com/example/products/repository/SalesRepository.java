package com.example.products.data.repository;

import com.example.products.data.db.Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SalesRepository {

  private final Database db;

  private Connection conn;
  private final Map<String, Integer> regionCache = new HashMap<>();
  private final Map<String, Integer> countryCache = new HashMap<>();

  public SalesRepository(Database db) {
    this.db = db;
  }

  public void beginTx() throws SQLException {
    conn = db.connect();
    conn.setAutoCommit(false);
  }

  public void commitTx() throws SQLException {
    conn.commit();
    conn.close();
    conn = null;
  }

  public void rollbackTx() {
    try {
      if (conn != null) conn.rollback();
    } catch (SQLException ignored) {}
    try {
      if (conn != null) conn.close();
    } catch (SQLException ignored) {}
    conn = null;
  }

  public void insertSale(String region, String country, int unitsSold, double totalProfit) throws SQLException {
    int regionId = getOrCreateRegion(region);
    int countryId = getOrCreateCountry(country, regionId);

    try (PreparedStatement ps = conn.prepareStatement("""
        INSERT INTO sales(country_id, units_sold, total_profit)
        VALUES(?, ?, ?)
      """)) {
      ps.setInt(1, countryId);
      ps.setInt(2, unitsSold);
      ps.setDouble(3, totalProfit);
      ps.executeUpdate();
    }
  }

  private int getOrCreateRegion(String name) throws SQLException {
    if (regionCache.containsKey(name)) return regionCache.get(name);

    try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM region WHERE name = ?")) {
      ps.setString(1, name);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          int id = rs.getInt(1);
          regionCache.put(name, id);
          return id;
        }
      }
    }

    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO region(name) VALUES(?)",
        Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, name);
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) {
        keys.next();
        int id = keys.getInt(1);
        regionCache.put(name, id);
        return id;
      }
    }
  }

  private int getOrCreateCountry(String name, int regionId) throws SQLException {
    String key = name + "|" + regionId;
    if (countryCache.containsKey(key)) return countryCache.get(key);

    try (PreparedStatement ps = conn.prepareStatement("""
        SELECT id FROM country WHERE name = ? AND region_id = ?
      """)) {
      ps.setString(1, name);
      ps.setInt(2, regionId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          int id = rs.getInt(1);
          countryCache.put(key, id);
          return id;
        }
      }
    }

    try (PreparedStatement ps = conn.prepareStatement("""
        INSERT INTO country(name, region_id) VALUES(?, ?)
      """, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, name);
      ps.setInt(2, regionId);
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) {
        keys.next();
        int id = keys.getInt(1);
        countryCache.put(key, id);
        return id;
      }
    }
  }
}
