package com.leyou.order.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.common.dto.CartDTO;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.Sku;
import com.leyou.order.client.AddressClient;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.dto.AddressDTO;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.interceptor.UserInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.utils.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hftang
 * @date 2019-02-20 17:27
 * @desc
 */
@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private GoodsClient goodsClient;

    /***
     * 创建订单
     * @param orderDTO
     * @return
     */
    @Transactional
    public Long createOrder(OrderDTO orderDTO) {

        //1新增订单
        Order order = new Order();
        //1.1 订单编号
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDTO.getPaymentType());

        //1.2 用户信息
        UserInfo user = UserInterceptor.getUserInfo();

        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);//是否评价

        //1.3 收货人地址

        AddressDTO addr = AddressClient.findById(orderDTO.getAddressId());
        order.setReceiver(addr.getName());
        order.setReceiverAddress(addr.getAddress());
        order.setReceiverCity(addr.getCity());
        order.setReceiverDistrict(addr.getDistrict());
        order.setReceiverMobile(addr.getPhone());
        order.setReceiverState(addr.getState());
        order.setReceiverZip(addr.getZipCode());

        //1.4 金额
        //获取所有购物车 然后转成map
        Map<Long, Integer> cartMap = orderDTO.getCarts().stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
        //获取里面所有商品的id
//        List<Long> skuIds = carts.stream().map(CartDTO::getSkuId).collect(Collectors.toList());
        Set<Long> skuIdSet = cartMap.keySet();

        //查询所有sku
        List<Sku> skus = goodsClient.querySkuBySpuId(new ArrayList<>(skuIdSet));//将set集合变成 list集合

        //收集订单详情
        List<OrderDetail> details = new ArrayList<>();
        //计算价格
        long totalPay = 0L;

        for (Sku sku : skus) {
            totalPay += sku.getPrice() * cartMap.get(sku.getId());

            //封装detail
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(orderId);
            detail.setImage(StringUtils.substringBefore(sku.getImages(), ","));
            detail.setNum(cartMap.get(sku.getId()));
            detail.setOwnSpec(sku.getOwnSpec());
            detail.setPrice(sku.getPrice());
            detail.setTitle(sku.getTitle());
            Long spuId = sku.getSpuId();
            log.info("[spuid]:::" + spuId);

            detail.setSkuId(sku.getSpuId());


            details.add(detail);
        }

        order.setTotalPay(totalPay);//总金额

        order.setActualPay(totalPay); //实付金额 是 总额+邮费-优惠金额 + order.getPostFee() - 0
        order.setPostFee(20L);
        //1.5写入数据库
        int count_insert = orderMapper.insertSelective(order);
        if (count_insert != 1) {
            log.error("[创建订单失败了order orderid: {}]", orderId);
            //新增失败 抛异常
            throw new LyException(ExceptionEnum.CREATE_ORDER_FAILED);
        }


        //2 新增订单详情
        int count = orderDetailMapper.insertList(details);
        if (count != details.size()) {
            log.error("[创建订单失败了 orderdetail orderid: {}]", orderId);
            //新增失败 抛异常
            throw new LyException(ExceptionEnum.CREATE_ORDER_FAILED);
        }

        //3 新增订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.UNPAY.value());

        orderStatusMapper.insertSelective(orderStatus);


        //4 减库存 同步减
        List<CartDTO> carts = orderDTO.getCarts();
        goodsClient.decreaseStock(carts);

        log.info("[账单创建成功]---------》》》");

        return orderId;
    }

    /**
     * 根据id 返回 order
     *
     * @param id
     * @return
     */

    public Order queryOrderByOrderId(Long id) {

        Order order = this.orderMapper.selectByPrimaryKey(id);
        if (order == null) {
            throw new LyException(ExceptionEnum.ORDER_STATUS_EXCEPTION);
        }

        //1 设置 order 的详情
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(id);
        List<OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);
        if (CollectionUtils.isEmpty(orderDetails)) {
            throw new LyException(ExceptionEnum.ORDER_DETAILS_FAILED);
        }
        order.setOrderDetails(orderDetails);

        //2 设置 order 的状态

        OrderStatus orderStatue = new OrderStatus();
        orderStatue.setOrderId(id);
        List<OrderStatus> orderStatuses = this.orderStatusMapper.select(orderStatue);
        if (CollectionUtils.isEmpty(orderStatuses)) {
            throw new LyException(ExceptionEnum.ORDER_STATUE_FAILED);
        }
        order.setOrderStatus(orderStatue);

        return order;

    }
}
