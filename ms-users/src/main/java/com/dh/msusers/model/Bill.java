package com.dh.msusers.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Bill {

    private String idBill;

    private String userId;

    private String productBill;

    private Double totalPrice;
}