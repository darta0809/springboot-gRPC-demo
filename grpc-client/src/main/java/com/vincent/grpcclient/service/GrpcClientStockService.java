package com.vincent.grpcclient.service;

import com.vincent.grpc.DeductReply;
import com.vincent.grpc.ProductOrder;
import com.vincent.grpc.StockServiceGrpc.StockServiceStub;
import com.vincent.grpcclient.observer.ExtendResponseObserver;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GrpcClientStockService {

  @GrpcClient("server-stream-server-side")
  private StockServiceStub stockServiceStub;

  public String batchDeduct(int count) {

    CountDownLatch countDownLatch = new CountDownLatch(1);

    // responseObserver 的 onNext 和 onCompleted 在另一個 thread 執行
    // ExtendResponseObserver 繼承 StreamObserver
    ExtendResponseObserver<DeductReply> responseObserver = new ExtendResponseObserver<>() {

      // 保存所有來自服務端的響應
      private StringBuilder stringBuilder = new StringBuilder();

      @Override
      public String getExtra() {
        return stringBuilder.toString();
      }

      /**
       * client 流 request 期間，每一筆 request 都會收到 server 端的一個 response
       * 對應每一個 response，這裡的 onNext 都會被執行一次，param 是 response 內容
       * */
      @Override
      public void onNext(DeductReply deductReply) {
        log.info("batch deduct on next");
        // 放入匿名類的成員變量中
        stringBuilder.append(
            String.format("返回碼[%d]，返回訊息: %s<br>", deductReply.getCode(), deductReply.getMessage()));

      }

      @Override
      public void onError(Throwable throwable) {
        log.error("batch deduct gRPC request error", throwable);
        stringBuilder.append("batch deduct gRPC error, " + throwable.getMessage());
        countDownLatch.countDown();
      }

      /**
       * server 端確認 response 完成後，會調用這裡
       * */
      @Override
      public void onCompleted() {
        log.info("batch deduct on completed");
        // 執行了 countDown() 後，前面執行的 await() 的 thread 就不再 wait 了，會繼續往下執行
        countDownLatch.countDown();
      }
    };

    // 遠程調用，此時數據尚未給到服務端
    StreamObserver<ProductOrder> requestObserver = stockServiceStub.batchDeduct(responseObserver);

    for (int i = 0; i < count; i++) {
      // 每次執行 onNext() 都會發送一筆數據到 server 端
      // server 端的 onNext() 都會被執行一次
      requestObserver.onNext(build(101 + i, 1 + i));
    }

    // client 端告訴 server 端，數據已發完
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

    log.info("service finish");

    // 服務端返回的內容被放置在 responseObserver 中，從 getExtra() 可取得
    return responseObserver.getExtra();
  }

  private static ProductOrder build(int productId, int num) {
    return ProductOrder.newBuilder().setProductId(productId).setNumber(num).build();
  }
}
