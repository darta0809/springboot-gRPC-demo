package com.vincent.grpcclient.service;

import com.vincent.grpc.Buyer;
import com.vincent.grpc.Order;
import com.vincent.grpc.OrderQueryGrpc.OrderQueryBlockingStub;
import com.vincent.grpcclient.model.DispOrder;
import io.grpc.StatusRuntimeException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GrpcClientOrderService {

  @GrpcClient("server-stream-server-side")
  private OrderQueryBlockingStub orderQueryBlockingStub;

  public List<DispOrder> listOrders(final String name) {
    // gRPC request param
    Buyer buyer = Buyer.newBuilder().setBuyerId(101).build();

    // gRPC response
    Iterator<Order> orderIterator;

    // 當前返回值
    List<DispOrder> orders = new ArrayList<>();

    try {
      orderIterator = orderQueryBlockingStub.listOrders(buyer);
    } catch (StatusRuntimeException e) {
      log.error("error gRPC invoke", e);
      return List.of();
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    log.info("start put order to list");

    while (orderIterator.hasNext()) {
      Order order = orderIterator.next();

      orders.add(DispOrder.builder()
          .orderId(order.getOrderId())
          .productId(order.getProductId())
          .orderTime(formatter.format(LocalDateTime.ofEpochSecond(order.getOrderTime(), 0,
              ZoneOffset.of("+8"))))
          .buyerRemark(order.getBuyerRemark())
          .build());
    }
    log.info("end put order to list");

    return orders;
  }
}
