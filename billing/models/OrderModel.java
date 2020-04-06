package edu.uci.ics.binghal.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class OrderModel {
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "movieId", required = true)
    private String movieId;
    @JsonProperty(value = "quantity", required = true)
    private int quantity;
    @JsonProperty(value = "unit_price", required = true)
    private float unit_price;
    @JsonProperty(value = "discount", required = true)
    private float discount;
    @JsonProperty(value = "saleDate", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date saleDate;

    @JsonCreator
    public OrderModel(){}

    @JsonCreator
    public OrderModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "movieId", required = true) String movieId,
            @JsonProperty(value = "quantity", required = true) int quantity,
            @JsonProperty(value = "unit_price", required = true) float unit_price,
            @JsonProperty(value = "discount", required = true) float discount,
            @JsonProperty(value = "saleDate", required = true) Date saleDate) {
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
        this.unit_price = unit_price;
        this.discount = discount;
        this.saleDate = saleDate;
    }

    @JsonProperty(value = "email")
    public String getEmail() {
        return email;
    }

    @JsonProperty(value = "movieId")
    public String getMovieId() {
        return movieId;
    }

    @JsonProperty(value = "quantity")
    public int getQuantity() {
        return quantity;
    }

    @JsonProperty(value = "saleDate")
    public Date getSaleDate() {
        return saleDate;
    }

    @JsonProperty(value = "unit_price")
    public float getUnit_price() {
        return unit_price;
    }

    @JsonProperty(value = "discount")
    public float getDiscount() {
        return discount;
    }
}
