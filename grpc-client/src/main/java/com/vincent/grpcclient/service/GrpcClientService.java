package com.vincent.grpcclient.service;

import com.vincent.grpc.HelloReply;
import com.vincent.grpc.HelloRequest;
import com.vincent.grpc.MyServiceGrpc.MyServiceBlockingStub;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class GrpcClientService {

  @GrpcClient("server-stream-server-side")
  private MyServiceBlockingStub myServiceBlockingStub;

  public String receiveGreeting(final String name) {

    HelloRequest request = HelloRequest.newBuilder()
        .setName(name)
        .build();
    try {
      HelloReply reply = myServiceBlockingStub.sayHello(request);
      System.out.println(reply.getMessage());
      return reply.getMessage();
    } catch (StatusRuntimeException e) {
      System.out.println("RPC failed:" + e.getMessage());
      return "FAILED with " + e.getStatus().getCode().name();
    }
  }
}
