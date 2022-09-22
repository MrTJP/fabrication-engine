package mrtjp.fengine.testcases;

import mrtjp.fengine.api.ICAssembler;
import mrtjp.fengine.api.ICFlatMap;
import mrtjp.fengine.assemble.ICStepThroughAssemblerImpl;
import mrtjp.fengine.framework.api.FabricationTestClass;

@FabricationTestClass
public class FabricationSteppedAssemblerTest extends FabricationBasicTest {

    public FabricationSteppedAssemblerTest(FabricationTestScenario scenario) {
        super(scenario);
    }

    @Override
    protected ICAssembler createAssembler() {
        return new ICStepThroughAssemblerImpl();
    }

    @Override
    protected ICFlatMap runAssembler(ICAssembler assembler) {
        ICStepThroughAssemblerImpl steppedAssembler = (ICStepThroughAssemblerImpl) assembler;
        while (!steppedAssembler.isDone()) {
            steppedAssembler.stepIn(); // Take smallest steps possible
        }
        return steppedAssembler.result();
    }
}
