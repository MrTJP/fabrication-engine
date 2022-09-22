package mrtjp.fengine;

import mrtjp.fengine.assemble.StepTree;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class StepTreeTest {

    @Test
    public void testStepOverExecutesAll() {

        List<String> executedSteps = new LinkedList<>();
        List<String> addedSteps = new LinkedList<>();

        StepTree.StepTreeEventReceiver<String, Void> receiver = new StepTree.StepTreeEventReceiver<String, Void>() {
            @Override
            public void onStepExecuted(List<Integer> treePath, String descriptor, Void result) {
                System.out.println("Step executed: " + descriptor + " at " + treePath);
                executedSteps.add(descriptor);
            }
            @Override
            public void onStepAdded(List<Integer> treePath, String descriptor) {
                System.out.println("Step added: " + descriptor + " at " + treePath);
                addedSteps.add(descriptor);
            }
        };

        StepTree<String, Void> tree = new StepTree<>(receiver);

        tree.addStep("1", () -> {
            tree.addStep("1.1", () -> {
                tree.addStep("1.1.1", () -> null);
                return null;
            });

            tree.addStep("1.2", () -> null);
            return null;
        });

        tree.addStep("2", () -> {
            tree.addStep("2.1", () -> null);

            tree.addStep("2.2", () -> {
                tree.addStep("2.2.1", () -> null);
                return null;
            });
            return null;
        });

        // Check initial conditions (2 steps added, none executed)
        assertEquals(2, tree.stepsRemaining());
        assertIterableEquals(Arrays.asList("1", "2"), addedSteps);
        assertIterableEquals(Collections.emptyList(), executedSteps);

        // Execute all steps
        tree.stepOver();

        // Check end conditions (0 steps remaining, all steps executed)
        assertEquals(0, tree.stepsRemaining());
        assertIterableEquals(Arrays.asList("1", "2", "1.1", "1.2", "1.1.1", "2.1", "2.2", "2.2.1"), addedSteps);
        assertIterableEquals(Arrays.asList("1", "1.1", "1.1.1", "1.2", "2", "2.1", "2.2", "2.2.1"), executedSteps);
    }
}
