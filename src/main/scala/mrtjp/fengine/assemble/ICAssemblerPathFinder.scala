package mrtjp.fengine.assemble

trait ICAssemblerPathFinder
{
    def doPathFinding(propagationFunc:(Int, Int) => Boolean):PathFinderResult
}
