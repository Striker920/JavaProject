package com.example.products.service;

import com.example.products.domain.dto.RegionUnitsDto;
import org.knowm.xchart.*;

import java.nio.file.Path;
import java.util.List;

public class ChartService {

  public void saveChart(List<RegionUnitsDto> data, Path file) throws Exception {
    CategoryChart chart = new CategoryChartBuilder()
        .width(1000).height(600)
        .title("Units Sold by Region")
        .xAxisTitle("Region")
        .yAxisTitle("Units")
        .build();

    chart.addSeries(
        "Units",
        data.stream().map(RegionUnitsDto::region).toList(),
        data.stream().map(d -> (Number) d.totalUnits()).toList()
    );

    BitmapEncoder.saveBitmap(chart, file.toString(), BitmapEncoder.BitmapFormat.PNG);
  }
}
