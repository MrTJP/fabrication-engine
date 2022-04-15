package mrtjp.fengine.api;

import mrtjp.fengine.assemble.ICAssemblerImpl;

public class FabricationEngine {

    public static ICAssembler newAssembler() {
        return new ICAssemblerImpl();
    }
}
