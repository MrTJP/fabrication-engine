package mrtjp.fengine.simulate;

public class ByteRegister implements ICRegister {

    private byte b = 0;
    private byte queued = 0;

    @Override
    public byte getByteVal() {
        return b;
    }

    @Override
    public boolean queueByteVal(byte newVal) {
        queued = newVal;
        return b != newVal;
    }

    @Override
    public void pushVal(ICSimulation ic) {
        b = queued;
    }
}
