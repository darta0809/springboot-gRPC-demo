package com.vincent.grpcserver.impl;

import com.vincent.grpc.Buyer;
import com.vincent.grpc.Order;
import com.vincent.grpc.Order.Builder;
import com.vincent.grpc.OrderQueryGrpc.OrderQueryImplBase;
import io.grpc.stub.StreamObserver;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class OrderServiceImpl extends OrderQueryImplBase {

  private static List<Order> mockOrders() {
    List<Order> orders = new ArrayList<>();
    Builder orderBuilder = Order.newBuilder();

    for (int i = 0; i < 10; i++) {
      orders.add(orderBuilder
          .setOrderId(i)
          .setProductId(10 + i)
          .setOrderTime(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond())
          .setBuyerRemark("remark-" + i)
          .build());
    }
    return orders;
  }

  @Override
  public void listOrders(Buyer request, StreamObserver<Order> responseObserver) {
    for (Order order : mockOrders()) {
      responseObserver.onNext(order);
    }
    responseObserver.onCompleted();
  }
}
