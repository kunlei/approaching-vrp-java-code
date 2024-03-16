package com.voyager.opt.utils;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public final class CVRPLIBInstance {
  /**
   * name of the instance
   */
  private final String name;
  /**
   * total number of nodes, including the depot
   */
  private final int numNodes;
  /**
   * vehicle capacity
   */
  private final int capacity;
  /**
   * indicate how edge weight is derived
   */
  private final String edgeWeightType;
  /**
   * nodeId -> coordinates
   */
  private final Map<Integer, int[]> coordinates;
  /**
   * nodeId -> demand
   */
  private final Map<Integer, Integer> demands;
  /**
   * depot id
   */
  private final int depot;

  /**
   * an invalid instance
   */
  public static CVRPLIBInstance invalidInstance = CVRPLIBInstance.builder()
    .name("invalid")
    .numNodes(0)
    .capacity(0)
    .edgeWeightType(null)
    .coordinates(null)
    .demands(null)
    .depot(0)
    .build();

  /**
   * computes the distance between two nodes
   * @param id1 id of the first node
   * @param id2 id of the second node
   * @return integral distance value
   */
  public double getDistance(int id1, int id2) {
    int[] node1Coordinates = coordinates.get(id1);
    int[] node2Coordinates = coordinates.get(id2);
    return Math.hypot(node2Coordinates[0] - node1Coordinates[0],
      node2Coordinates[1] - node1Coordinates[1]);
  }

  @Override
  public String toString() {
    return "CVRPLIBInstance{" +
      "name='" + name + '\'' +
      ", numNodes=" + numNodes +
      ", capacity=" + capacity +
      ", edgeWeightType='" + edgeWeightType + '\'' +
      ", coordinates=" + coordinates +
      ", demands=" + demands +
      ", depot=" + depot +
      '}';
  }
}
