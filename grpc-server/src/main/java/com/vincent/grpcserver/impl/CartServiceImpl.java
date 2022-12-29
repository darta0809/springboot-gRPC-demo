package com.vincent.grpcserver.impl;

import com.vincent.grpc.AddCartReply;
import com.vincent.grpc.CartServiceGrpc.CartServiceImplBase;
import com.vincent.grpc.ProductOrder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
public class CartServiceImpl extends CartServiceImplBase {

  @Override
  public StreamObserver<ProductOrder> addToCart(StreamObserver<AddCartReply> responseObserver) {
    // 返回匿名類，給上層框架使用 client 的 interface
    return new StreamObserver<>() {

      private int totalCount = 0;

      @Override
      public void onNext(ProductOrder productOrder) {
        log.info("正在處理商品[{}], 數量為[{}]", productOrder.getProductId(), productOrder.getNumber());

        totalCount += productOrder.getNumber();
      }

      @Override
      public void onError(Throwable throwable) {
        log.error("添加購物車異常", throwable);
      }

      @Override
      public void onCompleted() {
        log.info("添加購物車完成，共[{}]件商品", totalCount);
        responseObserver.onNext(AddCartReply.newBuilder()
            .setCode(10000)
            .setMessage(String.format("添加購物車完成，共[%d]件商品", totalCount))
            .build());

        responseObserver.onCompleted();
      }
    };
  }
}
