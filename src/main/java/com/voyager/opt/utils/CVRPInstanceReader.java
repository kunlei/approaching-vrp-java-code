package com.voyager.opt.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CVRPInstanceReader {

  public static CVRPLIBInstance parseInstance(String fileName) {
    List<String> lines = readLines(fileName);
    if (lines.isEmpty()) {
      return CVRPLIBInstance.invalidInstance;
    }

    String name;
    int numCustomers;
    int capacity;
    String edgeWeightType;
    Map<Integer, int[]> coordinates = new HashMap<>();
    Map<Integer, Integer> demands = new HashMap<>();
    int depot;

    int idx = 0;
    String line = lines.get(idx);
    name = line.split(":")[1].trim();

    idx = 3;
    line = lines.get(idx);
    numCustomers = Integer.parseInt(line.split(":")[1].trim());
    System.out.println("dimension: " + numCustomers);

    idx = 4;
    line = lines.get(idx);
    edgeWeightType = line.split(":")[1].trim();
    System.out.println("edge weight type: " + edgeWeightType);

    idx = 5;
    line = lines.get(idx);
    capacity = Integer.parseInt(line.split(":")[1].trim());

    idx = 7;
    for (int i = idx; i < idx + numCustomers; i++) {
      String[] parts = lines.get(i).trim().split("\\s+");
      int id = Integer.parseInt(parts[0]);
      int x = Integer.parseInt(parts[1]);
      int y = Integer.parseInt(parts[2]);
      coordinates.put(id, new int[]{x, y});
      System.out.println("id: " + id + ", x: " + x + ", y: " + y);
    }

    idx += numCustomers + 1;
    System.out.println("idx: " + idx);
    for (int i = idx; i < idx + numCustomers; i++) {
      String[] parts = lines.get(i).trim().split("\\s+");
      int id = Integer.parseInt(parts[0]);
      int demand = Integer.parseInt(parts[1]);
      demands.put(id, demand);
      System.out.println("id: " + id + ", demand: " + demand);
    }

    idx += numCustomers + 1;
    depot = Integer.parseInt(lines.get(idx).trim());
    System.out.println("depot: " + depot);

    return CVRPLIBInstance.builder()
      .name(name)
      .numCustomers(numCustomers)
      .capacity(capacity)
      .edgeWeightType(edgeWeightType)
      .coordinates(coordinates)
      .demands(demands)
      .depot(depot)
      .build();
  }

  private static List<String> readLines(String fileName) {
    try {
      List<String> lines = new ArrayList<>();
      BufferedReader reader = new BufferedReader(new FileReader(fileName));
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
      reader.close();
      return lines;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Collections.emptyList();
  }
}
