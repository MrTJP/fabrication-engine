package mrtjp.fengine.simulate

class StaticValueRegister[Type <: AnyVal](c:Type) extends ICRegister
{
    override def getVal[T]:T = c.asInstanceOf[T]
    override def queueVal[T](newVal:T) = false
    override def pushVal(ic:ICSimulation):Unit =
    {}
}