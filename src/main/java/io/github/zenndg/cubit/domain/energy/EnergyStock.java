package io.github.zenndg.cubit.domain.energy;

/**
 * The energy kept in a block of the mod, as a dock. Every operation moves whole FE, so nothing is
 * created or lost.
 */
public record EnergyStock(Battery energy) {
  /** A saved amount above the capacity, after a config change, is limited to the capacity. */
  public static EnergyStock of(Energy capacity, Energy saved) {
    return new EnergyStock(new Battery(capacity, saved.min(capacity)));
  }

  public Received receive(Energy offered, Energy inputLeft) {
    Battery.Charged charged = energy.recharge(offered.min(inputLeft));
    return new Received(new EnergyStock(charged.battery()), charged.accepted());
  }

  /** How much more energy fits. */
  public Energy room() {
    return energy.capacity().minus(energy.charge());
  }

  /** A whole item only leaves the slot when its energy fits, so nothing is wasted. */
  public boolean canTake(Energy itemEnergy) {
    return !itemEnergy.isZero() && itemEnergy.amount() <= room().amount();
  }

  public EnergyStock take(Energy itemEnergy) {
    return new EnergyStock(energy.recharge(itemEnergy).battery());
  }

  /** The robot takes what it can at its rate, and the stock loses exactly that amount. */
  public Transfer charge(Battery robot, Energy rate) {
    Battery.Charged charged = robot.recharge(rate.min(energy.charge()));
    return new Transfer(
        new EnergyStock(energy.consume(charged.accepted()).battery()), charged.battery());
  }

  /** What the stock kept from an offer, for the caller to report and to count against the input. */
  public record Received(EnergyStock stock, Energy accepted) {}

  /** What the stock had of a cost, and it loses exactly that amount. */
  public Spent spend(Energy cost) {
    Battery.Consumed consumed = energy.consume(cost);
    return new Spent(new EnergyStock(consumed.battery()), consumed.consumed());
  }

  public record Spent(EnergyStock stock, Energy paid) {}

  /** The result of one tick of charging. */
  public record Transfer(EnergyStock stock, Battery robot) {}
}
