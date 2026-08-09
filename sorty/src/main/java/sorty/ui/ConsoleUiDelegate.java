package sorty.ui;

import sorty.Sorty;
import sorty.algorithms.SorterProtocol;

public class ConsoleUiDelegate implements SorterProtocol, NumbersAwareUiDelegate {
    private Integer[] numbers = new Integer[0];

    @Override
    public void setNumbers(Integer[] numbers) {
        this.numbers = numbers;
    }

    @Override
    public void start(String algorithm, int size) {
        if (Sorty.verbose()) {
            System.out.println("Starting Sorter (Algorithm: " + algorithm + ", Size: " + size + ")");
        }
    }

    @Override
    public void finish() {
    }

    @Override
    public void finish(int total, int compare, int swap, int access) {
        if (Sorty.verbose()) {
            System.out.println(
                "Counters total: " + total
                    + ", compare: " + compare
                    + ", swap: " + swap
                    + ", access: " + access
            );
        }
    }

    @Override
    public void compare(int index1, int index2) {
        if (Sorty.verbose2()) {
            log("Compare", index1, index2);
        }
    }

    @Override
    public void swap(int index1, int index2) {
        if (Sorty.verbose2()) {
            log("Swap", index1, index2);
        }
    }

    @Override
    public int at(int index) {
        return numbers[index];
    }

    private void log(String title, int index1, int index2) {
        System.out.println(title + " " + index1 + " - " + index2);
    }
}
