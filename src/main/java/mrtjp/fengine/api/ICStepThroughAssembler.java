package mrtjp.fengine.api;

import mrtjp.fengine.TileCoord;

import java.util.List;
import java.util.Map;

public interface ICStepThroughAssembler extends ICAssembler, Stepper {

    void setEventReceiver(EventReceiver receiver);

    enum AssemblerStepType {
        CHECK_OPEN_TILE_MAPS,
        CHECK_OPEN_FLAT_MAPS,
        MERGE_TILE_MAP,
        MERGE_FLAT_MAP,

        MERGE_TILE_MAP_PRE,
        MERGE_TILE_MAP_PHASE1,
            PHASE1_ALLOC,
        MERGE_TILE_MAP_PHASE2,
            PHASE2_PATHFIND,
        MERGE_TILE_MAP_PHASE3,
            PHASE3_PF_MANIFEST_SEARCH,
        MERGE_TILE_MAP_PHASE4,
            PHASE4_REGISTER_REMAPS,
        MERGE_TILE_MAP_PHASE5,
            PHASE5_CONSUME_REMAPS,
        MERGE_TILE_MAP_PHASE6,
            PHASE6_COLLECT,
        MERGE_TILE_MAP_POST
    }

    interface AssemblerStepDescriptor {

        AssemblerStepType getStepType();
        List<Integer> getTreePath();
    }

    interface AssemblerStepResult {

        AssemblerStepType getStepType();
        List<Integer> getTreePath();

        List<TileCoord> getTileCoords();
        List<Integer> getRegisterIds();
        List<Integer> getGateIds();
        Map<Integer, Integer> getRemappedRegisterIds();
    }

    interface EventReceiver {

        void onStepAdded(AssemblerStepDescriptor descriptor);

        void onStepExecuted(AssemblerStepResult result);
    }
}
