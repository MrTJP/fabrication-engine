package mrtjp.fengine.simulate

class ValueRegister[Type <: AnyVal](var value:Type) extends ICRegister
{
    var queuedVal:Type = value

    override def getVal[T]:T = value.asInstanceOf[T]

    override def queueVal[T](newVal:T):Boolean =
    {
        queuedVal = newVal.asInstanceOf[Type]
        value != queuedVal
    }

    override def pushVal(ic:ICSimulation):Unit =
    {
        value = queuedVal
    }
}
