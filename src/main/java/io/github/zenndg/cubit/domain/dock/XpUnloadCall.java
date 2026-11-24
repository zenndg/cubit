package io.github.zenndg.cubit.domain.dock;

/**
 * Whether the Unload XP of the Tablet sends the Cubit to the Dock, or why not: the reasons of Go to
 * Dock, and then an empty tank (answers 2079 and 2107).
 */
public enum XpUnloadCall {
  READY,
  SWITCHED_OFF,
  HIBERNATING,
  NO_DOCK,
  EMPTY;

  public static XpUnloadCall of(DockGoal goal, boolean empty) {
    return switch (goal) {
      case READY -> empty ? EMPTY : READY;
      case SWITCHED_OFF -> SWITCHED_OFF;
      case HIBERNATING -> HIBERNATING;
      case NO_DOCK -> NO_DOCK;
    };
  }

  public boolean ready() {
    return this == READY;
  }
}
