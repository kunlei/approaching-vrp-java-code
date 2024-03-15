package com.voyager.opt.opensource.ortools;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.voyager.opt.common.VRPSolver;
import com.voyager.opt.utils.CVRPInstanceReader;
import com.voyager.opt.utils.CVRPLIBInstance;

import java.util.HashMap;
import java.util.Map;

public class CVRPOrtoolsSolver implements VRPSolver {
  private CVRPLIBInstance instance;
  private RoutingIndexManager manager;
  private RoutingModel model;
  private Assignment solution;

  @Override
  public void solve(String filename) {
    // read instance
    instance = CVRPInstanceReader.parseInstance(filename);

    Map<Integer, int[]> coordinates = instance.getCoordinates();
    int numCustomers = instance.getNumCustomers();
    int depotId = instance.getDepot();
    Map<Integer, Integer> idxToIdMapping = new HashMap<>(numCustomers);
    Map<Integer, Integer> idToIdxMapping = new HashMap<>(numCustomers);
    int idx = 0;
    for (Integer id : coordinates.keySet()) {
      idxToIdMapping.put(idx, id);
      idToIdxMapping.put(id, idx);
      idx++;
    }

    // prepare distance matrix
    long[][] distanceMatrix = new long[numCustomers][numCustomers];
    for (int i = 0; i < numCustomers - 1; i++) {
      distanceMatrix[i][i] = 0;
      for (int j = i + 1; j < numCustomers; j++) {
        int idi = idxToIdMapping.get(i);
        int idj = idxToIdMapping.get(j);
        distanceMatrix[i][j] = instance.getDistance(idi, idj);
        distanceMatrix[j][i] = distanceMatrix[i][j];
      }
    }
    distanceMatrix[numCustomers - 1][numCustomers - 1] = 0;

    // retrieve depot index
    int depotIdx = idToIdxMapping.get(depotId);
    Map<Integer, Integer> demands = instance.getDemands();
    int totalDemands = demands.values().stream().mapToInt(v -> v).sum();
    int vehicleNumber = (int) Math.ceil((double) totalDemands / instance.getCapacity());

    Loader.loadNativeLibraries();

    manager = new RoutingIndexManager(numCustomers, vehicleNumber, depotIdx);

    model = new RoutingModel(manager);

    final int transitCallbackIndex =
      model.registerTransitCallback((long fromIndex, long toIndex) -> {
        // Convert from routing variable Index to user NodeIndex.
        int fromNode = manager.indexToNode(fromIndex);
        int toNode = manager.indexToNode(toIndex);
        return distanceMatrix[fromNode][toNode];
      });

    model.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

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
        .build();

    solution = model.solveWithParameters(searchParameters);
  }

   void printSolution() {
    // Inspect solution.
    long maxRouteDistance = 0;
    for (int i = 0; i < 3; ++i) {
      long index = model.start(i);
      System.out.println("Route for Vehicle " + i + ":");
      long routeDistance = 0;
      String route = "";
      while (!model.isEnd(index)) {
        route += manager.indexToNode(index) + " -> ";
        long previousIndex = index;
        index = solution.value(model.nextVar(index));
        routeDistance += model.getArcCostForVehicle(previousIndex, index, i);
      }
      System.out.println(route + manager.indexToNode(index));
      System.out.println("Distance of the route: " + routeDistance + "m");
      maxRouteDistance = Math.max(routeDistance, maxRouteDistance);
    }
     System.out.println("Maximum of the route distances: " + maxRouteDistance + "m");
  }

  public static void main(String[] args) {
    String filename = "/Users/klian/dev/books/approaching-vrp-java/data/cvrp/P/P-n20-k2.vrp";

    CVRPOrtoolsSolver cvrpOrtoolsSolver = new CVRPOrtoolsSolver();
    cvrpOrtoolsSolver.solve(filename);
    cvrpOrtoolsSolver.printSolution();
  }
}
