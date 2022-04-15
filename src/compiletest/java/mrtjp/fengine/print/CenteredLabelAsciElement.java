package mrtjp.fengine.print;

import java.util.List;

class CenteredLabelAsciElement implements AsciElement {

    private String label;

    public CenteredLabelAsciElement(String label) {
        this.label = label;
    }

    @Override
    public void printIntoImage(AsciImage image, List<AsciElement> previous, List<AsciElement> remaining) {
        int centerX = image.width / 2;
        int centerY = image.height / 2;

        int labelW = label.length();

        int numPrev = (int) previous.stream().filter(c -> c instanceof CenteredLabelAsciElement).count();
        int numRem = (int) remaining.stream().filter(c -> c instanceof CenteredLabelAsciElement).count();

        int startX = centerX - labelW / 2;
        int startY = centerY - (numPrev + numRem + 1) + numPrev;

        image.addString(startX, startY, label);
    }
}
