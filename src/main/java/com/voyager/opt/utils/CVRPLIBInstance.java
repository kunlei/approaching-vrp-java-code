package com.voyager.opt.utils;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public final class CVRPLIBInstance {
  private final String name;
  private final int numCustomers;
  private final int capacity;
  private final String edgeWeightType;
  private final Map<Integer, int[]> coordinates;
  private final Map<Integer, Integer> demands;
  private final int depot;

  public static CVRPLIBInstance invalidInstance = CVRPLIBInstance.builder()
    .name("invalid")
    .numCustomers(0)
    .capacity(0)
    .edgeWeightType(null)
    .coordinates(null)
    .demands(null)
    .depot(0)
    .build();

  public int getDistance(int id1, int id2) {
    int[] node1Coordinates = coordinates.get(id1);
    int[] node2Coordinates = coordinates.get(id2);
    double sum = Math.pow(node2Coordinates[0] - node1Coordinates[0], 2.0) +
      Math.pow(node2Coordinates[1] - node1Coordinates[1], 2.0);
    return (int) Math.sqrt(sum);
  }

  @Override
  public String toString() {
    return "CVRPLIBInstance{" +
      "name='" + name + '\'' +
      ", numCustomers=" + numCustomers +
      ", capacity=" + capacity +
      ", edgeWeightType='" + edgeWeightType + '\'' +
      ", coordinates=" + coordinates +
      ", demands=" + demands +
      ", depot=" + depot +
      '}';
  }
}
