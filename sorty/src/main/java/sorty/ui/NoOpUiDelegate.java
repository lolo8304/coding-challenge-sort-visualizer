package sorty.ui;

import sorty.algorithms.SorterProtocol;

public class NoOpUiDelegate implements SorterProtocol, NumbersAwareUiDelegate {
    private Integer[] numbers = new Integer[0];

    @Override
    public void setNumbers(Integer[] numbers) {
        this.numbers = numbers;
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
    }

    @Override
    public void swap(int index1, int index2) {
    }

    @Override
    public int at(int index) {
        return numbers[index];
    }
}
