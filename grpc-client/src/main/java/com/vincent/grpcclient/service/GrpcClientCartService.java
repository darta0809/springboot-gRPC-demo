package com.vincent.grpcclient.service;

import com.vincent.grpc.AddCartReply;
import com.vincent.grpc.CartServiceGrpc.CartServiceStub;
import com.vincent.grpc.ProductOrder;
import com.vincent.grpcclient.observer.ExtendResponseObserver;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GrpcClientCartService {

  @GrpcClient("server-stream-server-side")
  private CartServiceStub cartServiceStub;

  public String addToCart(int count) {

    CountDownLatch countDownLatch = new CountDownLatch(1);

    // responseObserver 的 onNext 和 onCompleted 會在另一個 thread 中被執行
    // ExtendResponseObserver 繼承自 StreamObserver
    ExtendResponseObserver<AddCartReply> responseObserver = new ExtendResponseObserver<>() {

      String extraStr;

      @Override
      public String getExtra() {
        return extraStr;
      }

      private int code;
      private String message;

      @Override
      public void onNext(AddCartReply addCartReply) {
        log.info("on next");
        code = addCartReply.getCode();
        message = addCartReply.getMessage();
      }

      @Override
      public void onError(Throwable throwable) {
        log.error("gRPC request error", throwable);
        extraStr = "gRPC error, " + throwable.getMessage();
        countDownLatch.countDown();
      }

      @Override
      public void onCompleted() {
        log.info("on completed");
        extraStr = String.format("返回碼[%d], 返回訊息: %s", code, message);
        countDownLatch.countDown();
      }
    };

    StreamObserver<ProductOrder> requestObserver = cartServiceStub.addToCart(responseObserver);

    for (int i = 0; i < count; i++) {
      // 發送數據到服務端
      requestObserver.onNext(build(101 + i, 1 + i));
    }

    // 客戶端告訴服務端: 數據已發完
    requestObserver.onCompleted();

    try {
      // 開始等待，如果服務端處理完成，那麼 responseObserver 的 onCompleted 會在另一個 thread 執行
      // 會執行 countDownLatch 的 countDown()，一旦執行，下面 await 就執行完畢了
      boolean await = countDownLatch.await(2, TimeUnit.SECONDS);
      if (await) {
        log.info("is await done");
      }
    } catch (InterruptedException e) {
      log.error("countDownLatch await error", e);
    }

    log.info("server finish");

    // 服務端返回的內容被放置在 responseObserver 中，從 getExtra() 可取得
    return responseObserver.getExtra();
  }

  private static ProductOrder build(int productId, int num) {
    return ProductOrder.newBuilder().setProductId(productId).setNumber(num).build();
  }
}
