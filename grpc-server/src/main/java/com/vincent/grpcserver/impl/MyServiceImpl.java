package com.vincent.grpcserver.impl;

import com.vincent.grpc.HelloReply;
import com.vincent.grpc.HelloRequest;
import com.vincent.grpc.MyServiceGrpc.MyServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class MyServiceImpl extends MyServiceImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder()
                .setMessage("Hello! " + request.getName())
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
