package mrtjp.fengine.simulate;

public class StaticByteRegister implements ICRegister {

    private final byte b;

    public StaticByteRegister(byte b) {
        this.b = b;
    }

    @Override
    public byte getByteVal() {
        return b;
    }

    @Override
    public boolean queueByteVal(byte newVal) {
        return false;
    }

    @Override
    public void pushVal(ICSimulation ic) {
    }
}
