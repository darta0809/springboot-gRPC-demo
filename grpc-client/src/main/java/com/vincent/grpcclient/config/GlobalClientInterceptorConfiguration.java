package com.vincent.grpcclient.config;

import com.vincent.grpcclient.interceptor.LogGrpcInterceptor;
import io.grpc.ClientInterceptor;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration(proxyBeanMethods = false)
public class GlobalClientInterceptorConfiguration {

  @GrpcGlobalClientInterceptor
  ClientInterceptor clientInterceptor(){
    return new LogGrpcInterceptor();
  }
}
