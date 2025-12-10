package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Value("${sky.shop.address")
    private String shopAddress;
    @Value("${sky.baidu.ak}")
    private String ak;
    @Autowired
    private WebSocketServer webSocketServer;


    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //获取前端订单的地址id
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        //检验收货地址是否有效(收货地址是否为空)
        if(addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //检查用户的收获地址是否超出配送范围
        checkOutOfRange(addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail());

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

        Map map = new HashMap();
        map.put("type",1);
        map.put("orderId",orders.getId());
        map.put("content","订单号：" + outTradeNo);
        //通过WebSocket实现来单提醒，向客户端浏览器推送消息
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
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

    /**
     * 用户取消订单
     * @param id
     */
    @Override
    public void userCancelById(Long id){
        Orders ordersDB = orderDetailMapper.getById(id);
        if(ordersDB == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if(ordersDB.getStatus()>2){
            //当订单状态大于2时，不可取消订单
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        //订单处于待接单状态下取消，需要进行退款
        if(ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            //支付状态修改为退款
            orders.setPayStatus(Orders.REFUND);
        }

        //更新订单状态、取消原因、取消时间
        orders.setPayStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 用户再下单
     * @param id
     */
    @Override
    public void repetition(Long id) {
        //查询当前用户id
        Long userId = BaseContext.getCurrentId();

        //根据订单id查询当前订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        //将订单详情对象转换为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            //将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        //将购物车对象批量添加到数据库
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        //开启分页
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());

        //查询订单信息并数据进行分页处理
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        //部分订单状态，需要额外返回订单菜品信息订单菜品信息，将Orders转化为OrderVO
        List<OrderVO> orderVOList = getOrderVOList(page);

        return new PageResult(page.getTotal(),orderVOList);
    }



    private List<OrderVO> getOrderVOList(Page<Orders> page) {
        //需要返回订单菜品信息，自定义OrderVO响应结果
        List<OrderVO>  orderVOList = new ArrayList<>();

        List<Orders> ordersList = page.getResult();
        if(!CollectionUtils.isEmpty(ordersList)) {
            for (Orders orders : ordersList) {
                //将共同字段复制到OrderVO
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders,orderVO);
                String orderDishes = getOrderDishesStr(orders);

                //将订单菜品信息封装到orderVO中，并添加到orderVOList
                orderVO.setOrderDishes(orderDishes);
                orderVOList.add(orderVO);
            }

        }
        return orderVOList;
    }

    /**
     * 根据订单id获取菜品信息字符串
     * @param orders
     * @return
     */
    private String getOrderDishesStr(Orders orders){
        //查询订单菜品详情信息(订单中的菜品和数量)
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        //将每条订单菜品信息拼接为字符串
        List<String> orderDishList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        //将该订单对应的所有菜品信息拼接在一起
        return String.join("",orderDishList);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        //根据状态，分别查询出待接单、待派送、派送中的订单数量
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        //将查询出的数据封装到orderStatisticsVO中响应
        OrderStatisticsVO statisticsVO = new OrderStatisticsVO();
        statisticsVO.setToBeConfirmed(toBeConfirmed);
        statisticsVO.setConfirmed(confirmed);
        statisticsVO.setDeliveryInProgress(deliveryInProgress);
        return statisticsVO;
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();

        orderMapper.update(orders);
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        //根据id查询订单
        Orders orders = orderMapper.getById(ordersRejectionDTO.getId());

        //订单只有存在且状态为2(待接单)才可以接单
        if(orders ==null || !orders.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //拒单需要退款，根据订单id更新订单状态、拒单原因、取消时间
        Orders o = new Orders();
        o.setId(orders.getId());
        o.setStatus(Orders.CANCELLED);
        o.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        o.setCancelTime(LocalDateTime.now());

        orderMapper.update(o);
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        //根据id查询订单
        Orders orders = orderMapper.getById(ordersCancelDTO.getId());

        //管理端取消订单需要退款，根据订单id更新订单状态、取消原因、取消时间
        Orders o = new Orders();
        o.setId(ordersCancelDTO.getId());
        o.setStatus(Orders.CANCELLED);
        o.setCancelReason(ordersCancelDTO.getCancelReason());
        o.setCancelTime(LocalDateTime.now());
        orderMapper.update(o);
    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void delivery(Long id) {
        //根据id查询订单
        Orders orders = orderMapper.getById(id);

        //校验订单是否存在，且状态为3
        if(orders == null || !orders.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders o = new Orders();
        o.setId(orders.getId());
        //更新订单状态，改为派送中
        o.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(o);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void complete(Long id) {
        //根据id查询订单
        Orders orders = orderMapper.getById(id);

        //校验订单是否存在，且状态为4
        if(orders == null || !orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders o = new Orders();
        o.setId(orders.getId());
        //更新订单状态，状态转为完成
        o.setStatus(Orders.COMPLETED);
        o.setDeliveryTime(LocalDateTime.now());

        orderMapper.update(o);
    }


    /**
     * 校验收货地址是否超出配送范围
     * @param address
     */
    private void checkOutOfRange(String address){
        HashMap map = new HashMap();
        map.put("address", shopAddress);
        map.put("output","json");
        map.put("ak",ak);

        //获取店铺的经纬度坐标
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);
        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if(!jsonObject.getString("status").equals("0")) {
            throw new OrderBusinessException("店铺地址解析失败");
        }

        //数据解析
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");
        //店铺经纬度坐标
        String shopLngLat= lat + "," +lng;
        map.put("address",address);

        //获取用户收获地址的经纬度坐标
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3",map);
        jsonObject = JSON.parseObject(userCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("收获地址解析失败");
        }

        //数据解析
        location = jsonObject.getJSONObject("result").getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");
        //用户收获地址经纬度坐标
        String userLngLat= lat + "," +lng;
        map.put("origin",shopLngLat);
        map.put("destination",userLngLat);
        map.put("steps_info","0");

        //路线规划
        String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);

        jsonObject = JSON.parseObject(json);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("配送路线规划失败");
        }

        //数据解析
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = (JSONArray) result.get("routes");
        Integer distance =(Integer) ((JSONObject) jsonArray.get(0)).get("distance");

        if(distance > 50000){
            //配送距离超过50000米
            throw new OrderBusinessException("超出配送范围");
        }
    }

    @Override
    public void reminder(Long id) {
        //查询订单是否存在
        Orders orders = orderMapper.getById(id);
        if(orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //基于WebSocket实现催单
        Map map = new HashMap();
        map.put("type",2);
        map.put("orderId",id);
        map.put("content", "订单号：" + orders.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

}
