package com.voyager.opt.opensource.ortools;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;
import com.voyager.opt.common.VRPSolver;
import com.voyager.opt.utils.CVRPInstanceReader;
import com.voyager.opt.utils.CVRPLIBInstance;

import java.util.HashMap;
import java.util.Map;

public class CVRPOrtoolsSolver implements VRPSolver {
  private CVRPLIBInstance instance;
  private Map<Integer, Integer> idxToIdMapping;
  private Map<Integer, Integer> idToIdxMapping;
  private long[][] distanceMatrix;
  private int depotIdx;
  private int numAvailableVehicles;
  private RoutingIndexManager manager;
  private RoutingModel model;
  private Assignment solution;

  public CVRPOrtoolsSolver() {
    this.instance = null;
    this.manager = null;
    this.model = null;
    this.solution = null;
  }

  @Override
  public void readInstance(String filename) {
    // read instance
    instance = CVRPInstanceReader.parseInstance(filename);

    // create mappings between node id and its index
    Map<Integer, int[]> coordinates = instance.getCoordinates();
    int numNodes = instance.getNumNodes();
    int depotId = instance.getDepot();
    idxToIdMapping = new HashMap<>(numNodes);
    idToIdxMapping = new HashMap<>(numNodes);
    int idx = 0;
    for (Integer id : coordinates.keySet()) {
      idxToIdMapping.put(idx, id);
      idToIdxMapping.put(id, idx);
      idx++;
    }

    // prepare distance matrix
    distanceMatrix = new long[numNodes][numNodes];
    int scale = 10;
    for (int i = 0; i < numNodes - 1; i++) {
      int idi = idxToIdMapping.get(i);
      distanceMatrix[i][i] = 0;
      for (int j = i + 1; j < numNodes; j++) {
        int idj = idxToIdMapping.get(j);
        double distance = instance.getDistance(idi, idj);
        long longDist = (long) Math.floor(distance * scale);
        distanceMatrix[i][j] = longDist;
        distanceMatrix[j][i] = distanceMatrix[i][j];
      }
    }
    distanceMatrix[numNodes - 1][numNodes - 1] = 0;

    // retrieve depot index
    depotIdx = idToIdxMapping.get(depotId);
    Map<Integer, Integer> demands = instance.getDemands();
    int totalDemands = demands.values().stream().mapToInt(v -> v).sum();
    numAvailableVehicles = (int) Math.ceil((double) totalDemands / instance.getCapacity());

    int[] route1 = {0, 19, 5, 14, 16, 9, 7, 2, 10, 1, 0};
    int[] route2 = {0, 6, 13, 8, 17, 18, 3, 12, 15, 11, 4, 0};
    int route1Distance = 0;
    int route2Distance = 0;
    System.out.println("depotId: " + depotId);
    for (int i = 0; i < route1.length - 1; i++) {
      int idx1 = idToIdxMapping.get(route1[i] + 1);
      int idx2 = idToIdxMapping.get(route1[i + 1] + 1);
      route1Distance += (int) distanceMatrix[idx1][idx2];
    }
    System.out.println("route1Distance: " + route1Distance);
    for (int i = 0; i < route2.length - 1; i++) {
      int idx1 = idToIdxMapping.get(route2[i] + 1);
      int idx2 = idToIdxMapping.get(route2[i + 1] + 1);
      route2Distance += (int) distanceMatrix[idx1][idx2];
    }
    System.out.println("route2Distance: " + route2Distance);
    System.out.println("total distance: " + (route1Distance + route2Distance));
  }

  @Override
  public void solve() {
    Loader.loadNativeLibraries();
    manager = new RoutingIndexManager(distanceMatrix.length, numAvailableVehicles, depotIdx);
    model = new RoutingModel(manager);

    final int transitCallbackIndex =
      model.registerTransitCallback((long fromIndex, long toIndex) -> {
        // Convert from routing variable Index to user NodeIndex.
        int fromNode = manager.indexToNode(fromIndex);
        int toNode = manager.indexToNode(toIndex);
        return distanceMatrix[fromNode][toNode];
      });

    model.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

    Map<Integer, Integer> demands = instance.getDemands();
    final int demandCallbackIndex =
      model.registerUnaryTransitCallback((long fromIndex) -> {
        int fromNode = manager.indexToNode(fromIndex);
        return demands.get(idxToIdMapping.get(fromNode));
      });

    model.addDimension(demandCallbackIndex,
      0,
      instance.getCapacity(),
      true,
      "capacity");

    RoutingSearchParameters searchParameters =
      main.defaultRoutingSearchParameters()
        .toBuilder()
        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
        .setTimeLimit(Duration.newBuilder().setSeconds(30).build())
        .build();

    solution = model.solveWithParameters(searchParameters);
  }

   void printSolution() {
    // Inspect solution.
    long totalRouteDistances = 0;
    for (int i = 0; i < numAvailableVehicles; ++i) {
      long index = model.start(i);
      System.out.println("Route for Vehicle " + i + ":");
      long routeDistance = 0;
      String route = "";
      while (!model.isEnd(index)) {
        route += idxToIdMapping.get(manager.indexToNode(index)) + " -> ";
        long previousIndex = index;
        index = solution.value(model.nextVar(index));
        routeDistance += model.getArcCostForVehicle(previousIndex, index, i);
      }
      System.out.println(route + idxToIdMapping.get(manager.indexToNode(index)));
      System.out.println("Distance of the route: " + routeDistance + "m");
      totalRouteDistances += routeDistance;
    }
     System.out.println("Total route distances: " + totalRouteDistances + "m");
  }

  public static void main(String[] args) {
    String filename = "/Users/klian/dev/books/approaching-vrp-java/data/cvrp/P/P-n20-k2.vrp";
//    String filename = "/Users/klian/dev/books/approaching-vrp-java/data/cvrp/P/P-n16-k8.vrp";

    CVRPOrtoolsSolver cvrpOrtoolsSolver = new CVRPOrtoolsSolver();
    cvrpOrtoolsSolver.readInstance(filename);
    cvrpOrtoolsSolver.solve();
    cvrpOrtoolsSolver.printSolution();
  }
}
