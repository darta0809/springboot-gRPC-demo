package com.vincent.grpcclient.controller;

import com.vincent.grpcclient.service.GrpcClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GrpcClientController {

  @Autowired
  private GrpcClientService grpcClientService;

  @GetMapping
  public String printMessage(@RequestParam(defaultValue = "hello") String name){
    return grpcClientService.receiveGreeting(name);
  }
}
