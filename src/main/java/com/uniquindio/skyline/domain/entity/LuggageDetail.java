package com.uniquindio.skyline.domain.entity;

import lombok.Getter;

import java.util.List;

@Getter
public class LuggageDetail {

    private String id;
    private List<String> idLuggage;
    private int quantity;
    private double subTotal;

    public  LuggageDetail(String id) {
        this.id = id;
    }

}