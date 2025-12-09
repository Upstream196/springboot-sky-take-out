package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理支付超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder(){
        log.info("处理支付超时订单：{}", new Date());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);
        if(ordersList != null && ordersList.size() >0){
           //增强for循环
            ordersList.forEach(order->{
                //更改订单状态
                order.setStatus(Orders.CANCELLED);
                //说明取消原有
                order.setCancelReason("支付超时，自动取消");
                //声明取消时间
                order.setCancelTime(LocalDateTime.now());
                //更新订单信息
                orderMapper.update(order);
            });
        }
    }

    /**
     * 处理“派送中”状态的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("处理派送中订单：{}", new Date());
        //设置超时时间
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        //查询处于派送中且超时的订单列表
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        //当订单列表不为空时执行状态更改操作
        if(ordersList != null || ordersList.size() >0){
            ordersList.forEach(order->{
                //更改订单状态
                order.setStatus(Orders.COMPLETED);
                //更新订单信息
                orderMapper.update(order);
            });
        }
    }
}
