package com.vincent.grpcclient;

import com.vincent.grpcclient.service.GrpcClientService;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GrpcClientApplication {

    @Autowired
    private GrpcClientService grpcClientService;

    public static void main(String[] args) {
        SpringApplication.run(GrpcClientApplication.class, args);
    }

    @PostConstruct
    public void doStuff() {
        // the service will have been initialized and wired into the field by now
        grpcClientService.receiveGreeting("hi?");
    }

    // Client的部分若要設定timeout可以利用stub本身的function - withDeadlineAfter
    // reply = myServiceBlockingStub.withDeadlineAfter(50,TimeUnit.MILLISECONDS).sayHello(request);
}
