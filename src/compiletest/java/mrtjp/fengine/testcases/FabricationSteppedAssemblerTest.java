package mrtjp.fengine.testcases;

import mrtjp.fengine.api.ICAssembler;
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
}
