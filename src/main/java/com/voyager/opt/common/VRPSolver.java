package com.voyager.opt.common;

public interface VRPSolver {

  void readInstance(String filename);
  void solve();
}
