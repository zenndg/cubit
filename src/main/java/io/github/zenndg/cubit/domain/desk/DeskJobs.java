package io.github.zenndg.cubit.domain.desk;

/**
 * The works a desk can have at once: an order, a plan of the cascade and a disassembly. Only one
 * runs at a time, and the others wait for it to end (answers 2015 and 2528).
 */
public record DeskJobs(boolean order, boolean cascade, boolean disassembly) {
  public boolean allowsOrder() {
    return !cascade && !disassembly;
  }

  public boolean allowsCascade() {
    return !order && !disassembly;
  }

  public boolean allowsDisassembly() {
    return !order && !cascade;
  }
}
