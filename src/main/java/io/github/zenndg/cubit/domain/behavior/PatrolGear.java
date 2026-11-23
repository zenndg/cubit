package io.github.zenndg.cubit.domain.behavior;

/** The parts of the Galactic Patroller that change in use: the visor, the blade and the wings. */
public record PatrolGear(boolean visorDown, boolean bladeLit, boolean wingsOpen) {}
