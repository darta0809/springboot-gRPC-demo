package com.vincent.grpcclient.observer;

import io.grpc.stub.StreamObserver;

public interface ExtendResponseObserver<T> extends StreamObserver<T> {

  String getExtra();
}
