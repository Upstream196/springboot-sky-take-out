package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 数据概览
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessDataVO implements Serializable {

    private Double turnover;

    private Integer validOrderCount;

    private Double orderCompletionRate;

    private Double unitPrice;

    private Integer newUsers;
}
