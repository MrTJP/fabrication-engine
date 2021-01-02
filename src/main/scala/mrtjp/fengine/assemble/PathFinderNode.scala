package mrtjp.fengine.assemble

import mrtjp.fengine.TileCoord

case class PathFinderNode(pos:TileCoord, dir:Int, port:Int)
{
    private var root:Option[PathFinderNode] = None

    def rootNode:PathFinderNode = root match {
        case Some(rootNode) => rootNode
        case None => this
    }

    def moveTo(pos:TileCoord, dir:Int, port:Int):PathFinderNode = {
        val newNode = PathFinderNode(pos, dir, port)
        newNode.root = root
        newNode
    }

    override def equals(that:Any):Boolean = that match {
        case n:PathFinderNode => n.pos == pos && n.dir == dir && n.port == port
        case _ => false
    }

    // No collisions for:
    // 25 bits pos.hashCode()
    // 3 bits dir
    // 4 bits port
    override def hashCode():Int = {
        var result = pos.hashCode()
        result = (result << 3) + dir
        result = (result << 4) + port
        result
    }

    override def toString = s"PathfinderNode(${pos.toString}, dir:$dir, port:$port)"
}