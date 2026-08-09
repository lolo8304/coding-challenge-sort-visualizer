package sorty.algorithms;

import java.util.ArrayList;
import java.util.List;

public class ProtocolInspector implements SorterProtocol {

    public final List<String> swapEvents = new ArrayList<>();
    public final List<String> compareEvents = new ArrayList<>();

    public ProtocolInspector() {

    }

    @Override
    public void start(String algorithm, int size) {

    }

    @Override
    public void finish() {

    }

    @Override
    public void finish(int total, int compare, int swap, int access) {

    }

    @Override
    public void compare(int index1, int index2) {
        this.compareEvents.add("compare - "+index1 + " - " + index2);
    }

    @Override
    public void swap(int index1, int index2) {
        this.swapEvents.add("swap - "+index1 + " - " + index2);
    }

    @Override
    public int at(int index) {
        return 0;
    }

    @Override
    public int put(int index, int value) {
        return 0;
    }
}
