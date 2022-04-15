package mrtjp.fengine.simulate;

public interface ICRegister {

    byte getByteVal();

    boolean queueByteVal(byte newVal);

    void pushVal(ICSimulation ic);
}
