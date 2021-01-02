package mrtjp.fengine.spec

import mrtjp.fengine.TileCoord.{allDirs, allPorts, portMaskAll, _}
import mrtjp.fengine.testimpl.{PortPassthroughWireTileImpl, PortlessWireTileImpl}

class WireTileSpec extends FEFlatSpec
{
    "A PortlessWireTile" should "propagate all ports on masked sides" in {
        logger.info("Trying all combinations of: connMask, inDir, inPort, outDir, outPort")
        var i = 0
        for (connMask <- 0 until dirMaskAll) {
            val wire = new PortlessWireTileImpl(connMask)

            for (inDir <- allDirs) for (inP <- allPorts) for (outDir <- allDirs) for (outP <- allPorts) {
                val shouldBeAllowed = (connMask&1<<inDir) != 0 && (connMask&1<<outDir) != 0
                val isAllowed = wire.propagationFunc(inDir, inP)(outDir, outP)
                assert(isAllowed == shouldBeAllowed,
                    s"Unexpected propagation behaviour: (shouldBeAllowed:$shouldBeAllowed, isAllowed:$isAllowed)")
                i += 1
            }
        }
        logger.info(s"$i combinations propagated as expected")
    }

    "A PortPassthroughWire (with single port masked)" should "propagate only masked port on masked sides" in {
        logger.info("Trying all combinations of: connMask, portMask, inDir, inPort, outDir, outPort")
        var i = 0
        for (connMask <- 0 until dirMaskAll) for (port <- allPorts) {
            val portMask = 1<<port
            val wire = new PortPassthroughWireTileImpl(connMask, portMask)
            for (inDir <- allDirs) for (inP <- allPorts) for (outDir <- allDirs) for (outP <- allPorts) {
                val shouldBeAllowed = inP == outP && (portMask&1<<inP) != 0 && (connMask&1<<inDir) != 0 && (connMask&1<<outDir) != 0
                val isAllowed = wire.propagationFunc(inDir, inP)(outDir, outP)
                assert(isAllowed == shouldBeAllowed,
                    s"Unexpected propagation behaviour: (shouldBeAllowed:$shouldBeAllowed, isAllowed:$isAllowed)")
                i += 1
            }
        }
        logger.info(s"$i combinations propagated as expected")
    }

    "A PortPassthroughWire (with all ports masked)" should "propagate any port to same port on masked sides" in {
        logger.info("Trying all combinations of: connMask, portMask, inDir, inPort, outDir, outPort")
        var i = 0
        for (connMask <- 0 until dirMaskAll) {
            val wire = new PortPassthroughWireTileImpl(connMask, portMaskAll)
            for (inDir <- allDirs) for (inP <- allPorts) for (outDir <- allDirs) for (outP <- allPorts) {
                val shouldBeAllowed = inP == outP && (portMaskAll&1<<inP) != 0 && (connMask&1<<inDir) != 0 && (connMask&1<<outDir) != 0
                val isAllowed = wire.propagationFunc(inDir, inP)(outDir, outP)
                assert(isAllowed == shouldBeAllowed,
                    s"Unexpected propagation behaviour: (shouldBeAllowed:$shouldBeAllowed, isAllowed:$isAllowed)")
                i += 1
            }
        }
        logger.info(s"$i combinations propagated as expected")
    }
}