package sorty.algorithms;

public interface SorterProtocol {
    void start(String algorithm, int size);
    void finish();
    void finish(int total, int compare, int swap, int access);
    void compare(int index1, int index2);
    void swap(int index1, int index2);
    int at(int index);
}
