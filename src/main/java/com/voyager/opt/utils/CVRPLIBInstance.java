package com.voyager.opt.utils;

import lombok.Getter;

import java.util.Map;

@Getter
public final class CVRPLIBInstance {
  private final String name;
  private final int numCustomers;
  private final int capacity;
  private final Map<Integer, int[]> coordinates;
  private final Map<Integer, Integer> demands;
  private final int depot;

  public CVRPLIBInstance(String name, int numCustomers, int capacity, Map<Integer, int[]> coordinates, Map<Integer, Integer> demands, int depot) {
    this.name = name;
    this.numCustomers = numCustomers;
    this.capacity = capacity;
    this.coordinates = coordinates;
    this.demands = demands;
    this.depot = depot;
  }

  @Override
  public String toString() {
    return "CVRPLIBInstance{" +
      "name='" + name + '\'' +
      ", numCustomers=" + numCustomers +
      ", capacity=" + capacity +
      ", coordinates=" + coordinates +
      ", demands=" + demands +
      ", depot=" + depot +
      '}';
  }
}
