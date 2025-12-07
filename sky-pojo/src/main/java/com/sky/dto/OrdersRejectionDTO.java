package com.sky.dto;

import lombok.Data;

@Data
public class OrdersRejectionDTO {

    private Long id;

    //拒单原因
    private String rejectionReason;
}
