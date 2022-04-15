package mrtjp.fengine.print;

import java.util.List;

interface AsciElement {

    void printIntoImage(AsciImage image, List<AsciElement> previous, List<AsciElement> remaining);
}
