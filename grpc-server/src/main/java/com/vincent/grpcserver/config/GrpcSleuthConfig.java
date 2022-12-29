package com.vincent.grpcserver.config;

import brave.Tracing;
import brave.grpc.GrpcTracing;
import com.vincent.grpcserver.interceptor.LogGrpcInterceptor;
import io.grpc.ServerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GlobalServerInterceptorConfigurer;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.Span;
import zipkin2.reporter.Reporter;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class GrpcSleuthConfig {

    @Bean
    public GrpcTracing grpcTracing(Tracing tracing){
        return GrpcTracing.create(tracing);
    }

    // Use this for debugging (or if there is no Zipkin server running on port 9411)
    @Bean
    @ConditionalOnProperty(value = "sample.zipkin.enabled", havingValue = "false")
    public Reporter<Span> spanReporter() {
        return span -> log.info("{}",span);
    }

    @Bean
    public GlobalServerInterceptorConfigurer globalServerInterceptorConfigurer(GrpcTracing grpcTracing) {
        return registry -> registry.add(grpcTracing.newServerInterceptor());
    }

    @GrpcGlobalServerInterceptor
    ServerInterceptor serverInterceptor(){
        return new LogGrpcInterceptor();
    }
}
