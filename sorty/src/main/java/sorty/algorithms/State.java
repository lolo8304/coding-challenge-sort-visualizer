package sorty.algorithms;

public record State(int index1, int at1, int index2, int at2, boolean shouldSwap) {
    public void swap(SorterProtocol sorterProtocol, Integer[] numbers) {
        numbers[index1] = at2;
        sorterProtocol.put(index1, at2);
        numbers[index2] = at1;
        sorterProtocol.put(index2, at1);
        sorterProtocol.swap(index1, index2);
    }
}
