package mrtjp.fengine

case class TileCoord(x:Int, y:Int, z:Int)
{
    override def equals(obj:scala.Any):Boolean = obj match
    {
        case TileCoord(x1, y1, z1) => x == x1 && y == y1 && z == z1
        case _ => false
    }

    // No collisions for:
    // 8 bits x
    // 8 bits y
    // 5 bits z
    override def hashCode():Int = {
        var result = x
        result = (result << 8) + y
        result = (result << 5) + z
        result
    }

    def copy:TileCoord = TileCoord(x, y, z)

    def add(dx:Int, dy:Int, dz:Int):TileCoord = TileCoord(x+dx, y+dy, z+dz)
    def subtract(dx:Int, dy:Int, dz:Int):TileCoord = add(-dx, -dy, -dz)
    def multiply(i:Int, j:Int, k:Int):TileCoord = TileCoord(x*i, y*j, z*k)
    def divide(i:Int, j:Int, k:Int):TileCoord = TileCoord(x/i, y/j, z/k)

    def add(d:Int):TileCoord = add(d, d, d)
    def subtract(d:Int):TileCoord = subtract(d, d, d)
    def multiply(k:Int):TileCoord = multiply(k, k, k)
    def divide(k:Int):TileCoord = divide(k, k, k)

    def add(that:TileCoord):TileCoord = add(that.x, that.y, that.z)
    def subtract(that:TileCoord):TileCoord = subtract(that.x, that.y, that.z)
    def multiply(that:TileCoord):TileCoord = multiply(that.x, that.y, that.z)
    def divide(that:TileCoord):TileCoord = divide(that.x, that.y, that.z)

    def negate:TileCoord = TileCoord(-x, -y, -z)

    def max(that:TileCoord):TileCoord = TileCoord(x max that.x, y max that.y, z max that.z)
    def min(that:TileCoord):TileCoord = TileCoord(x min that.x, y min that.y, z min that.z)

    def offset(dir:Int):TileCoord = offset(dir, 1)
    def offset(dir:Int, amount:Int):TileCoord = this+(TileCoord.dirOffsets(dir)*amount)

    def unary_- :TileCoord = negate

    def +(that:TileCoord):TileCoord = add(that)
    def -(that:TileCoord):TileCoord = subtract(that)
    def *(that:TileCoord):TileCoord = multiply(that)
    def /(that:TileCoord):TileCoord = divide(that)

    def +(that:Int):TileCoord = add(that)
    def -(that:Int):TileCoord = subtract(that)
    def *(that:Int):TileCoord = multiply(that)
    def /(that:Int):TileCoord = divide(that)

    override def toString = s"TileCoord(x:$x y:$y z:$z)"
}

object TileCoord
{
    val infinite:TileCoord = TileCoord(Int.MaxValue, Int.MaxValue, Int.MaxValue)
    val origin:TileCoord = TileCoord(0, 0, 0)

    val dirOffsets = Seq(
        TileCoord( 0, 0,-1),
        TileCoord( 0, 0, 1),
        TileCoord( 0,-1, 0),
        TileCoord( 0, 1, 0),
        TileCoord(-1, 0, 0),
        TileCoord( 1, 0, 0)
    )

    val dirDown  = 0
    val dirUp    = 1
    val dirNorth = 2
    val dirSouth = 3
    val dirWest  = 4
    val dirEast  = 5

    val bitDown  = 0x01
    val bitUp    = 0x02
    val bitNorth = 0x04
    val bitSouth = 0x08
    val bitWest  = 0x10
    val bitEast  = 0x20

    val allDirs:Seq[Int] = Seq(dirDown, dirUp, dirNorth, dirSouth, dirWest, dirEast)
    val dirMaskAll:Int = bitDown | bitUp | bitNorth | bitSouth | bitWest | bitEast
    def maskToDirs(mask:Int):Seq[Int] = allDirs.filter(d => (mask&1<<d) != 0)
    def dirsToMask(dirs:Seq[Int]):Int = dirs.foldLeft(0) { (mask, d) => mask|1<<d }
    def oppositeDir(dir:Int):Int = dir^1

    val allPorts:Seq[Int] = 0 until 16
    val portMaskAll:Int = portsToMask(allPorts)
    def maskToPorts(mask:Int):Seq[Int] = allPorts.filter(p => (mask&1<<p) != 0)
    def portsToMask(ports:Seq[Int]):Int = ports.foldLeft(0) { (mask, p) => mask|1<<p }
}