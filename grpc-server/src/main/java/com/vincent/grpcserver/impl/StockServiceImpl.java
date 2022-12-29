package com.vincent.grpcserver.impl;

import com.vincent.grpc.DeductReply;
import com.vincent.grpc.ProductOrder;
import com.vincent.grpc.StockServiceGrpc.StockServiceImplBase;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
public class StockServiceImpl extends StockServiceImplBase {

  @Override
  public StreamObserver<ProductOrder> batchDeduct(StreamObserver<DeductReply> responseObserver) {
    // 返回匿名類，給上層框架使用
    return new StreamObserver<>() {

      private int totalCount = 0;

      @Override
      public void onNext(ProductOrder productOrder) {
        log.info("正在處理商品[{}]，數量為[{}]", productOrder.getProductId(), productOrder.getNumber());
        totalCount += productOrder.getNumber();

        int code;
        String message;

        if (productOrder.getNumber() % 2 == 0) {
          code = 10000;
          message = String.format("商品[%s]扣減庫存數[%d]成功", productOrder.getProductId(),
              productOrder.getNumber());
        } else {
          code = 10001;
          message = String.format("商品[%s]扣減庫存數[%d]失敗", productOrder.getProductId(),
              productOrder.getNumber());
        }

        responseObserver.onNext(DeductReply.newBuilder()
            .setCode(code)
            .setMessage(message)
            .build());
      }

      @Override
      public void onError(Throwable throwable) {
        log.error("批量扣減庫存異常", throwable);
      }

      @Override
      public void onCompleted() {
        log.info("批量扣減庫存完成，共計[{}]件商品", totalCount);
        responseObserver.onCompleted();
      }
    };
  }
}
