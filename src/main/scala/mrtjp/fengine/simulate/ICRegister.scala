package mrtjp.fengine.simulate

trait ICRegister
{
    def getVal[T]:T

    def queueVal[T](newVal:T):Boolean

    def pushVal(ic:ICSimulation):Unit
}
