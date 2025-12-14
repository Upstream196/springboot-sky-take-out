package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderReportVO implements Serializable {

    //日期 以逗号分隔，例如：2025-10-01
    private String dateList;
    //每日订单数，以逗号分隔，例如：260，210
    private String orderCountList;
    //每日有效订单数，以逗号分隔，例如：20，201
    private String validOrderCountList;
    //订单总数
    private Integer totalOrderCount;
    //有效订单总数
    private Integer validOrderCount;
    //订单完成率
    private Double orderCompletionRate;
}
