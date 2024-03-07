package com.voyager.opt.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CVRPInstanceReader {

  public static void main(String[] args) {
    String fileName = "/Users/klian/dev/books/approaching-vrp-java/data/cvrp/P/P-n20-k2.vrp"; // Change to your file name

    try {
      CVRPLIBInstance instance = parseCVRPLIBInstance(fileName);
      System.out.println(instance); // Print or process the parsed instance
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static CVRPLIBInstance parseCVRPLIBInstance(String fileName) throws IOException {
    List<String> lines = readLines(fileName);
    String name;
    int numCustomers;
    int capacity;
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

    return new CVRPLIBInstance(name, numCustomers, capacity, coordinates, demands, depot);
  }

  private static List<String> readLines(String fileName) throws IOException {
    List<String> lines = new ArrayList<>();
    BufferedReader reader = new BufferedReader(new FileReader(fileName));
    String line;
    while ((line = reader.readLine()) != null) {
      lines.add(line);
    }
    reader.close();
    return lines;
  }
}
