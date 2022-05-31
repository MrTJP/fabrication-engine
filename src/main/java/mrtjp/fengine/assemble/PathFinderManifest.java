package mrtjp.fengine.assemble;

import mrtjp.fengine.TileCoord;
import mrtjp.fengine.api.IPathFinderManifest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PathFinderManifest {

    private final Map<TileCoord, ManifestEntry> manifestMap = new HashMap<>();

    public void addInputPropagationTrail(int inputRegId, PathFinderNode node) {
        node.forEachPropagation((pos, inDir, inPort, outDir, outPort) -> {
            ManifestEntry entry = manifestMap.computeIfAbsent(pos, p -> new ManifestEntry());
            entry.addInputPropagation(inputRegId, inDir, inPort, outDir, outPort);
        });
    }

    public void addOutputPropagationTrail(int outputRegId, PathFinderNode node) {
        node.forEachPropagation((pos, inDir, inPort, outDir, outPort) -> {
            ManifestEntry entry = manifestMap.computeIfAbsent(pos, p -> new ManifestEntry());
            entry.addOutputPropagation(outputRegId, inDir, inPort, outDir, outPort);
        });
    }

    public ManifestEntry getManifest(TileCoord pos) {
        return manifestMap.getOrDefault(pos, ManifestEntry.EMPTY);
    }

    public void clear() {
        manifestMap.clear();
    }

    private static class ManifestEntry implements IPathFinderManifest {

        private static final ManifestEntry EMPTY = new ManifestEntry();

        private final Map<Integer, PropagationMask> inputRegisters = new HashMap<>();
        private final Map<Integer, PropagationMask> outputRegisters = new HashMap<>();

        public void addInputPropagation(int inputReg, int inputDir, int inputPort, int outputDir, int outputPort) {
            PropagationMask mask = this.inputRegisters.computeIfAbsent(inputReg, r -> new PropagationMask());
            mask.or(inputDir, inputPort, outputDir, outputPort);
        }

        public void addOutputPropagation(int outputReg, int inputDir, int inputPort, int outputDir, int outputPort) {
            PropagationMask mask = this.outputRegisters.computeIfAbsent(outputReg, r -> new PropagationMask());
            mask.or(inputDir, inputPort, outputDir, outputPort);
        }

        public Set<Integer> getInputRegisters() {
            return inputRegisters.keySet();
        }

        public Set<Integer> getOutputRegisters() {
            return outputRegisters.keySet();
        }

        public Set<Integer> getInputRegistersIntersectingMasks(int inputDirMask, int inputPortMask, int outputDirMask, int outputPortMask) {
            return inputRegisters.entrySet().stream()
                    .filter(e -> e.getValue().testMasks(inputDirMask, inputPortMask, outputDirMask, outputPortMask))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
        }

        public Set<Integer> getOutputRegistersIntersectingMasks(int inputDirMask, int inputPortMask, int outputDirMask, int outputPortMask) {
            return outputRegisters.entrySet().stream()
                    .filter(e -> e.getValue().testMasks(inputDirMask, inputPortMask, outputDirMask, outputPortMask))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
        }
    }

    private static class PropagationMask {

        private int inputDirMask = 0;
        private int inputPortMask = 0;
        private int outputDirMask = 0;
        private int outputPortMask = 0;

        public void or(int inputDir, int inputPort, int outputDir, int outputPort) {
            inputDirMask |= 1 << inputDir;
            inputPortMask |= 1 << inputPort;
            outputDirMask |= 1 << outputDir;
            outputPortMask |= 1 << outputPort;
        }

        public boolean testMasks(int inputDirMask, int inputPortMask, int outputDirMask, int outputPortMask) {
            return (this.inputDirMask & inputDirMask) != 0 && (this.inputPortMask & inputPortMask) != 0 &&
                    (this.outputDirMask & outputDirMask) != 0 && (this.outputPortMask & outputPortMask) != 0;
        }
    }
}
