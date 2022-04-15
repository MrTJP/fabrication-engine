package mrtjp.fengine.api;

import mrtjp.fengine.assemble.PathFinder;

import java.util.Optional;

/**
 * Represents a node placed on an FETileMap. Nodes can have 2 basic features:
 * <ul>
 * <li>Perform compute: Nodes can read the output value of connected input nodes,
 * compute the output, and write it to the input of a connected output node.
 * <li>Form connections: Nodes can allow signal flows from input direction to output
 * directions, serving as the connection path between other nodes.
 * </ul>
 */
public interface ICAssemblyTile {

    /**
     * Assembly pass 1:
     *
     * Allocate output register IDs and Gate IDs.
     *
     * @param assembler The assembler performing the flat mapping
     */
    default void allocate(ICAssembler assembler) {}

    /**
     * Assembly pass 2:
     *   - Use provided pathfinder to locate input register IDs.
     *   - Use provided assembler to declare necessary remaps
     *
     * @param assembler The running Assembler
     * @param pathFinder Pathfinder to be used to locate input registers from the map
     */
    default void locate(ICAssembler assembler, PathFinder pathFinder) {}

    /**
     * Assembly pass 3:
     *   - Check all register IDs for remap and adjust to new value if necessary.
     *
     * @param assembler The assembler performing the flat mapping
     */
    default void remap(ICAssembler assembler) {}

    /**
     * Assembly pass 4:
     *   - Add registers and gates to the assembler
     *
     * @param assembler The assembler performing the flat mapping
     */
    default void collect(ICAssembler assembler) {}

    /**
     * Used to calculate where an incoming signal can propagate to.
     *
     * @param inDir  The direction from which the propagating signal is coming from
     * @param inPort The port for which the incoming signal is associated with
     * @return Propagation test function `f(Int, Int) => Boolean` such that if the incoming signal (inDir, inPort)
     *         is allowed to propagate out to direction `outDir` on port `outPort`, then:
     *         f(outDir, outPort) == true
     *
     */
    default PropagationFunction propagationFunc(int inDir, int inPort) { return (a, b) -> false; }

    /**
     * Optionally returns the ID of a driven register if one exists for the given parameters.
     *
     * @param outDir  The output direction that is being queried
     * @param outPort The output port that is being queried
     * @return A register ID if this gate is driving port `outPort` on side `outDir`, or None
     */
    default Optional<Integer> getOutputRegister(int outDir, int outPort) { return Optional.empty(); }

    /**
     * Optionally returns the ID of a driving register if one exists for the given parameters.
     *
     * @param inDir  The input direction that is being queried
     * @param inPort The input port that is being queried
     * @return A register ID if this gate is driven by port `inPort` on side `outputDir`, or None
     */
    default Optional<Integer> getInputRegister(int inDir, int inPort) { return Optional.empty(); }
}
