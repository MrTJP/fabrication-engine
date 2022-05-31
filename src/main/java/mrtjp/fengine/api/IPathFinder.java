package mrtjp.fengine.api;

import mrtjp.fengine.assemble.PathFinderResult;

public interface IPathFinder {

    PathFinderResult doPathFinding(PropagationFunction propagationFunc);
}
