package mrtjp.fengine.simulate;

/**
 * A single compute instance in an IC simulation
 * <p>
 * This gate or function will:
 * <ul>
 * <li>Read values from its dependency registers
 * <li>Perform some calculation to obtain output value
 * <li>Queue new output value to its output register
 * </ul>
 * <p>
 */
public interface ICGate {

    /**
     * Perform compute in the given simulation (i.e. read input registers, calculate, and store
     * results in output registers).
     * <p>
     * Suppose this gate performs the following calculation:
     * <pre>{@code
     *  outA = f(inA, inB)
     * }</pre>
     * <p>
     * This gate has 2 input registers ({@code inA} and {@code inB}), and 1 output ({@code outA}).
     * That is, values should be read from {@code inA} and {@code inB}. Some calculated value should
     * be written to {@code outB}.
     * <p>
     * During assembly, this gate would be declared as follows:
     * <pre>{@code
     *  List<Integer> inputRegisters = Arrays.asList(inARegId, inBRegId);
     *  List<Integer> outputRegisters = Arrays.asList(outARegId);
     *
     *  assembler.addGate(someGateId, thisGate, inputRegisters, outputRegisters);
     * }</pre>
     * <p>
     * Given a simulation object {@code ic}, the computation is performed as follows:
     * <pre>{@code
     *  // Obtain register IDs
     *  int inARegId = inputs[0]; // Index of inARegId in inputRegisters above
     *  int inBRegId = inputs[1]; // Likewise, index of inBRegId above
     *  int outARegId = outputs[0];
     *
     *  // Use IDs to obtain input values
     *  int inA = ic.getRegVal(inARegId);
     *  int inB = ic.getRegVal(inBRegId);
     *
     *  // Calculate result
     *  int outA = f(inA, inB);
     *
     *  // Write result to output register
     *  ic.queueRegVal(outARegId, outA);
     * }</pre>
     *
     * @param ic      The simulation this gate is inside of
     * @param inputs  Input register IDs
     * @param outputs Output register IDs
     */
    void compute(ICSimulation ic, int[] inputs, int[] outputs);
}
