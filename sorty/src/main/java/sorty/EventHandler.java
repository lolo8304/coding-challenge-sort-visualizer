package sorty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sorty.algorithms.SorterProtocol;

@Getter
@Setter
public class EventHandler implements SorterProtocol {

    private int triggerEvery = 1;
    private SorterProtocol forwarder;
    private int delayMillis = 100;

    private int counter = 0;
    private int totalCounter = 0;
    private int totalCompare = 0;
    private int totalSwap = 0;
    private int totalAccess = 0;
    private int totalWrite = 0;

    @Builder
    public EventHandler(int triggerEvery, SorterProtocol forwarder, int delayMillis) {
        this.triggerEvery = triggerEvery;
        this.forwarder = forwarder;
        this.delayMillis = delayMillis;
    }

    @Override
    public void start(String algorithm, int size) {
        this.forwarder.start(algorithm, size);
    }

    @Override
    public void finish() {
        this.forwarder.finish(this.totalCounter, this.totalCompare, this.totalSwap, this.totalAccess);
    }

    @Override
    public void finish(int total, int compare, int swap, int access) {
        this.forwarder.finish(total, compare, swap, access);
    }

    @Override
    public void compare(int index1, int index2) {
        this.totalCompare++;
        if (this.nextCounter() == 0) {
            this.forwarder.compare(index1, index2);
            this.delay();
        }
    }

    @Override
    public void swap(int index1, int index2) {
        this.totalSwap++;
        if (this.nextCounter() == 0) {
            this.forwarder.swap(index1, index2);
            this.delay();
        }
    }

    private synchronized  int nextCounter() {
        counter++;
        totalCounter++;
        counter %= triggerEvery;
        return counter;
    }

    @Override
    public int at(int index) {
        this.totalAccess++;
        if (this.nextCounter() == 0) {
            int value = this.forwarder.at(index);
            this.delay();
            return value;
        }
        return 0;
    }

    @Override
    public int put(int index, int value) {
        this.totalWrite++;
        if (this.nextCounter() == 0) {
            int result = this.forwarder.put(index, value);
            this.delay();
            return result;
        }
        return value;
    }

    private void delay() {
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new SortInterruptedException();
        }
    }
}
