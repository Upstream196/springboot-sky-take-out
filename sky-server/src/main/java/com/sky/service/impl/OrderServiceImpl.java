package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    WeChatPayUtil weChatPayUtil;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //获取前端订单的地址id
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        //检验收货地址是否有效(收货地址是否为空)
        if(addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //获取当前登录用户的id，赋值给购物车
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);

        //检查当前用户的购物车是否为空
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if(shoppingCartList == null || shoppingCartList.size() == 0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //构造订单数据
        Orders order = new Orders();
        //将用户提交订单的数据传递给订单表
        BeanUtils.copyProperties(ordersSubmitDTO,order);
        //设置订单信息
        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getDetail());
        order.setConsignee(addressBook.getConsignee());
        //将当前系统的毫秒时间设置为订单号
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setUserId(userId);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setPayStatus(Orders.UN_PAID);
        order.setOrderTime(LocalDateTime.now());

        //向订单表插入1条数据
        orderMapper.insert(order);

        //创建并批量保存订单明细
        ArrayList<OrderDetail> orderDetailList = new ArrayList<>();
        //此时遍历的是用户购物车，不是订单细节类
        for(ShoppingCart cart : shoppingCartList){
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            //此时订单已经从DTO对象获取了前端传递的数据
            orderDetail.setOrderId(order.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        //清空用户购物车
        shoppingCartMapper.deleteByUserId(userId);

        //封装返回结果给前端
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime())
                .build();

        return orderSubmitVO;
    }

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     * @throws Exception
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception{
        //获取当前登录用户的id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        /*JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01),   //支付金额 单位：元
                "苍穹外卖订单",   //商品描述
                user.getOpenid()    //微信用户的openid
        );

        //判断订单是否已支付
        if(jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")){
            throw new OrderBusinessException("该订单已支付");
        }*/

        //将微信返回的数据转成我们自定义的VO对象
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        //替代微信支付成功后的数据库订单状态更新，多定义一个方法进行修改
        Integer OrderPaidStatus =Orders.PAID;   //支付状态，已支付
        Integer OrderStatus = Orders.TO_BE_CONFIRMED;   //订单状态，待接单
        //补充支付时间check_out属性赋值
        LocalDateTime check_out_time = LocalDateTime.now();
        //获取订单号码
        String orderNumber = ordersPaymentDTO.getOrderNumber();
        log.info("调用updateStatus，用于替换微信支付更新数据库状态的问题");
        orderMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, orderNumber);
        //返回给APP
        return vo;
    }

    @Override
    public void paySuccess(String outTradeNo) {
        //获取当前用户id
        Long userId = BaseContext.getCurrentId();
        //根据微信传来的订单号与当前用户id，查询当前用户的订单
        Orders ordersDB = orderMapper.getByNumberAndUserId(outTradeNo, userId);

        //根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        //更新订单数据
        orderMapper.update(orders);
    }

    /**
     * 用户端订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQueryUser(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("输入DTO:{}", ordersPageQueryDTO.toString());
        Long currentId = BaseContext.getCurrentId();
        log.warn("【历史订单查询】当前线程用户ID = {}", currentId);

        //去除@RequestBody会出现什么结果？
        //设置页面框架
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        //获取订单基础信息
        //获取当前登录的用户id与订单状态
        OrdersPageQueryDTO dto = new OrdersPageQueryDTO();  //创建新的dto对象用于设置用户id与状态
        dto.setUserId(BaseContext.getCurrentId());          //若使用参数dto对象会出现什么结果？
        dto.setStatus(ordersPageQueryDTO.getStatus());

        //分页条件查询
        Page<Orders> page = orderMapper.pageQuery(dto);
        List<OrderVO> list = new ArrayList();
        log.info("查询结果：total={},list size={}",page.getTotal(),list.size());

        //获取订单详细内容
        if(page !=null && page.getTotal() >0){
            for(Orders orders : page){
                //获取订单id
                Long orderId = orders.getId();
                //查看订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);
                //创建视图对象，将订单基础信息与详细信息封装到视图对象中
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);
                log.info("处理订单ID={}，details size={}", orders.getId(),orderDetails.size());
                //修复添加orderDishes -->为订单列表页提供一个快速预览的“点了啥”字符串
                List<String> orderDishList = orderDetails.stream().map(x -> {
                    String orderDish = x.getName() + "*" + x.getNumber() + ";";
                    return orderDish;
                }).collect(Collectors.toList());

                // 将拼接好的字符串赋值给 orderVO
                String orderDishes = String.join("", orderDishList);
                orderVO.setOrderDishes(orderDishes);


                list.add(orderVO);
            }
        }

        return new PageResult(page.getTotal(),list);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO details(Long id) {
        //根据id查询订单
        Orders orders = orderMapper.getById(id);
        //查询该订单对应的菜品/套餐明细
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        //将该订单及其详情封装到OrderVO并返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }
}
