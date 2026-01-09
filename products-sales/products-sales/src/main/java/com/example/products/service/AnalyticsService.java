package com.example.products.service;

import com.example.products.data.db.Database;
import com.example.products.domain.dto.*;

import java.sql.*;
import java.util.*;

public class AnalyticsService {

  private final Database db;

  public AnalyticsService(Database db) {
    this.db = db;
  }

  public List<RegionUnitsDto> unitsByRegion() throws SQLException {
    String sql = """
      SELECT r.name, SUM(s.units_sold)
      FROM sales s
      JOIN country c ON s.country_id = c.id
      JOIN region r ON c.region_id = r.id
      GROUP BY r.name
      ORDER BY 2 DESC
    """;

    try (Connection c = db.connect();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

      List<RegionUnitsDto> list = new ArrayList<>();
      while (rs.next()) {
        list.add(new RegionUnitsDto(rs.getString(1), rs.getLong(2)));
      }
      return list;
    }
  }

  public CountryProfitDto topCountryEuropeAsia() throws SQLException {
    String sql = """
      SELECT c.name, SUM(s.total_profit)
      FROM sales s
      JOIN country c ON s.country_id = c.id
      JOIN region r ON c.region_id = r.id
      WHERE r.name IN ('Europe', 'Asia')
      GROUP BY c.name
      ORDER BY 2 DESC
      LIMIT 1
    """;

    try (Connection c = db.connect();
         ResultSet rs = c.createStatement().executeQuery(sql)) {

      rs.next();
      return new CountryProfitDto(rs.getString(1), rs.getDouble(2));
    }
  }

  public CountryRegionProfitDto countryInProfitRange() throws SQLException {
    String sql = """
      SELECT c.name, r.name, SUM(s.total_profit)
      FROM sales s
      JOIN country c ON s.country_id = c.id
      JOIN region r ON c.region_id = r.id
      WHERE r.name IN ('Middle East and North Africa', 'Sub-Saharan Africa')
      GROUP BY c.name, r.name
      HAVING SUM(s.total_profit) BETWEEN 420000 AND 440000
      ORDER BY 3 DESC
      LIMIT 1
    """;

    try (Connection c = db.connect();
         ResultSet rs = c.createStatement().executeQuery(sql)) {

      if (!rs.next()) return null;
      return new CountryRegionProfitDto(rs.getString(1), rs.getString(2), rs.getDouble(3));
    }
  }
}
