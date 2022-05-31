package mrtjp.fengine.api;

import mrtjp.fengine.simulate.ICGate;
import mrtjp.fengine.simulate.ICRegister;
import mrtjp.fengine.tiles.FETileMap;

import java.util.List;
import java.util.Map;
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
     * <p>
     * Allocate output register IDs and Gate IDs.
     *
     * @param allocator Used to allocate registers and gates
     */
    default void allocate(Allocator allocator) { }

    /**
     * Assembly pass 2:
     * <p>
     * Use provided pathfinder to locate input register IDs.
     *
     * @param pathFinder Pathfinder to be used to locate input registers from the map
     */
    default void locate(IPathFinder pathFinder) { }

    /**
     * Assembly pass 3:
     * <p>
     * Search pathfinder manifest for any registers of interest. Typically, used by propagation-capable
     * tiles to figure out which signals passed through them by peeking at pathfinding results of other
     * tiles.
     *
     * @param manifest The manifest to search
     */
    default void searchManifest(IPathFinderManifest manifest) { }

    /**
     * Assembly pass 4:
     * <p>
     * Use provided remap registry to declare necessary remaps
     *
     * @param remapRegistry Used to register remaps
     */
    default void registerRemaps(RemapRegistry remapRegistry) { }

    /**
     * Assembly pass 5:
     * <p>
     * Check all register IDs for remap and adjust to new value if necessary.
     *
     * @param remapProvider Provides remaps that were registered during assembly pass 2
     */
    default void consumeRemaps(RemapProvider remapProvider) { }

    /**
     * Assembly pass 6:
     * <p>
     * Add registers and gates to the assembler
     *
     * @param collector The collector for assigning registers and gates
     */
    default void collect(Collector collector) { }

    /**
     * Used to calculate where an incoming signal can propagate to.
     *
     * @param inDir  The direction from which the propagating signal is coming from
     * @param inPort The port for which the incoming signal is associated with
     * @return Propagation test function `f(Int, Int) => Boolean` such that if the incoming signal (inDir, inPort)
     * is allowed to propagate out to direction `outDir` on port `outPort`, then:
     * f(outDir, outPort) == true
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

    interface Allocator {

        int allocRegisterID();

        int allocRegisterID(int id);

        int allocGateID();

        int allocGateID(int id);
    }

    interface RemapRegistry {

        void addRemap(int oldID, int newID);
    }

    interface RemapProvider {

        int getRemappedRegisterID(int id);
    }

    interface Collector {

        void addRegister(int id, ICRegister r);

        void addGate(int id, ICGate gate, List<Integer> drivingRegs, List<Integer> drivenRegs);

        void addTileMap(FETileMap map, Map<Integer, Integer> remaps);

        void addFlatMap(ICFlatMap flatMap, Map<Integer, Integer> remaps);
    }
}
