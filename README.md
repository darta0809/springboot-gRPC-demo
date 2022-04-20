# Springboot_gRPC_demo

## 流程總覽

* 建立Spring Boot專案

* 設定專案裡所有的pom.xml

* 建立protobuf定義

* Service實作

* Client實作

* Zipkin追蹤APIs

---

## 注意事項

* IntelliJ plugin 下載 `Protobuf` 可高亮程式碼

* 需要 `mvn clean install` grpc-interface 這包到 .m2 本地倉庫

    * 成功後 target 會產生 grpc class 檔

    * 需要 override 的 function 都會在 ***grpc裡

* grpc-server 服務起來後，可從 Postman 建立 gRPC request 並且 import hello.proto 測試

* 也可以在 grpc-server 起服務後，再起 grpc-client 服務去呼叫

---

## 使用 Zipkin 追蹤 APIs

此範例使用 docker 方式啟動 Zipkin

```docker
docker run -d -p 9411:9411 openzipkin/zipkin
```

接著打開 http://localhost:9411/

當使用 Postman 或 grpc-client 呼叫時，上方網頁能夠擷取到資料










