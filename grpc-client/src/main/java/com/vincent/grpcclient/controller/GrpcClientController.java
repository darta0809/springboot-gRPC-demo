package com.vincent.grpcclient.controller;

import com.vincent.grpcclient.model.DispOrder;
import com.vincent.grpcclient.service.GrpcClientOrderService;
import com.vincent.grpcclient.service.GrpcClientService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GrpcClientController {

  private final GrpcClientService grpcClientService;
  private final GrpcClientOrderService grpcClientOrderService;

  @GetMapping("hello")
  public String printMessage(@RequestParam(defaultValue = "hello") String name) {
    return grpcClientService.receiveGreeting(name);
  }

  @GetMapping("order")
  public List<DispOrder> printOrder(@RequestParam(defaultValue = "order") String name) {
    return grpcClientOrderService.listOrders(name);
  }
}
