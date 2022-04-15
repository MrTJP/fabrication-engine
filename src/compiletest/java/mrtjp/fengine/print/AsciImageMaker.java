package mrtjp.fengine.print;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AsciImageMaker {

    public static char DOWN_ARROW = '↓';
    public static char LEFT_ARROW = '←';
    public static char UP_ARROW = '↑';
    public static char RIGHT_ARROW = '→';

    private List<AsciElement> elements = new LinkedList<>();

    public void addElement(AsciElement element) {
        elements.add(element);
    }

    public AsciImage createImage(int width, int height) {

        AsciImage image = new AsciImage(width, height);

        LinkedList<AsciElement> completed = new LinkedList<>();
        LinkedList<AsciElement> remaining = new LinkedList<>(elements);

        while (!remaining.isEmpty()) {
            AsciElement next = remaining.removeFirst();
            next.printIntoImage(image, Collections.unmodifiableList(completed), Collections.unmodifiableList(remaining));
            completed.add(next);
        }

        return image;
    }
}
