package com.example.demoelastic.model;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@CsvRecord(separator = ",", skipFirstLine = true,generateHeaderColumns = true)
public class Product {
    @DataField(pos = 1, columnName = "id")
    private String id;
    @DataField(pos = 2, columnName = "productName")
    private String productName;
    @DataField(pos = 3, columnName = "colour")
    private String colour;
    @DataField(pos = 4, columnName = "price")
    private String price;
}
