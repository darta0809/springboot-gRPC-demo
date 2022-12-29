package com.vincent.grpcclient.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DispOrder {

  private int orderId;
  private int productId;
  private String orderTime;
  private String buyerRemark;

}
