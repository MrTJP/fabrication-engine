package mrtjp.fengine.assemble;

import mrtjp.fengine.api.Stepper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;

public class StepTree<Descriptor, Result> implements Stepper {

    private final Stack<StepTreeNode> stack = new Stack<>();
    private final StepTreeEventReceiver<Descriptor, Result> receiver;

    private int stepsRemaining = 0;

    public StepTree(StepTreeEventReceiver<Descriptor, Result> receiver) {
        this.receiver = receiver;
        stack.push(new StepTreeNode());
    }

    public void addStep(Descriptor descriptor, Supplier<Result> task) {

        stack.peek().createChild(descriptor, task);
        stepsRemaining++;
    }

    @Override
    public void stepOver() {

        if (stepsRemaining == 0) return;

        StepTreeNode target = stack.peek(); // Node to step over
        if (target.isComplete()) {
            if (!target.isRoot()) popAndSetExecuted();
            return;
        }

        while (!target.isComplete()) {
            StepTreeNode head = stack.peek();

            if (!head.isComplete()) {
                StepTreeNode next = head.getNextChildToExecute();
                pushAndExecute(next);

            } else {
                popAndSetExecuted();
            }
        }
    }

    @Override
    public void stepIn() {

        if (stepsRemaining == 0) return;

        StepTreeNode head = stack.peek();
        if (head.isComplete()) {
            if (!head.isRoot()) popAndSetExecuted();
            return;
        }

        StepTreeNode next = head.getNextChildToExecute();
        pushAndExecute(next);
    }

    @Override
    public void stepOut() {

        if (stepsRemaining == 0) return;

        StepTreeNode head = stack.peek();

        if (!head.isComplete()) stepOver();

        if (!head.isRoot()) popAndSetExecuted();
    }

    @Override
    public int stepsRemaining() {
        return stepsRemaining;
    }

    private void pushAndExecute(StepTreeNode node) {
        stack.push(node);
        node.executeTask();
        stepsRemaining--;
    }

    private void popAndSetExecuted() {
        stack.pop();
        stack.peek().setNextChildExecuted();
    }

    private class StepTreeNode {

        private final List<StepTreeNode> children = new ArrayList<>();
        private int completedChildren = 0;

        private final Descriptor descriptor;
        private final List<Integer> treePath;
        private final Supplier<Result> task;

        boolean taskExecuted = false;

        public StepTreeNode(List<Integer> treePath, Descriptor descriptor, Supplier<Result> task) {
            this.treePath = treePath;
            this.descriptor = descriptor;
            this.task = task;
        }

        public StepTreeNode() {
            this(Collections.emptyList(), null, null);
            taskExecuted = true; // Container only node. No actual task to execute.
        }

        public boolean isRoot() {
            return treePath.isEmpty();
        }

        public void createChild(Descriptor descriptor, Supplier<Result> task) {

            List<Integer> childTreePath = new ArrayList<>(treePath.size() + 1);
            childTreePath.addAll(treePath);
            childTreePath.add(children.size());

            StepTreeNode child = new StepTreeNode(childTreePath, descriptor, task);
            children.add(child);

            receiver.onStepAdded(childTreePath, descriptor);
        }

        public StepTreeNode getNextChildToExecute() {
            return children.get(completedChildren);
        }

        public void setNextChildExecuted() {
            completedChildren++;
        }

        public void executeTask() {
            if (taskExecuted) throw new RuntimeException("Task already executed");

            Result result = task.get();
            taskExecuted = true;
            receiver.onStepExecuted(treePath, descriptor, result);
        }

        public boolean isComplete() {
            return taskExecuted && completedChildren == children.size();
        }
    }

    public interface StepTreeEventReceiver<Descriptor, Result> {

        void onStepExecuted(List<Integer> treePath, Descriptor descriptor, Result result);

        void onStepAdded(List<Integer> treePath, Descriptor descriptor);
    }
}
