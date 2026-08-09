package sorty.algorithms.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sorty.SortDirection;
import sorty.algorithms.BinaryMaxTree;
import sorty.algorithms.DefaultSorter;
import sorty.algorithms.State;
import sorty.algorithms.SorterProtocol;

@Getter
@Setter
@NoArgsConstructor
public class HeapSorter extends DefaultSorter {

    private Integer[] binaryTree;
    private int found = 0;

    @Override
    public void sort(SortDirection direction) {
        this.getSorter().start("Heap Sort " + direction, this.numbers.length);
        this.binaryTree = new Integer[this.numbers.length];
        this.found = this.binaryTree.length;
        this.heapify();
        while (found > 1) {
            // the pos 0 is the max
            this.swapIndexes(this.binaryTree[0], found - 1);
            found--;
            this.heapify();
        }
        if (direction == SortDirection.DESCENDING) {
            this.reverse();
        }
        this.getSorter().finish();
    }

    private void heapify() {
        BinaryMaxTree.buildMaxTree(this.numbers, this.binaryTree, binaryTreeProtocol(), this.found);
    }

    private void reverse() {
        for (int left = 0, right = this.numbers.length - 1; left < right; left++, right--) {
            swapIndexes(left, right);
        }
    }

    private void swapIndexes(int index1, int index2) {
        if (index1 == index2) {
            return;
        }
        int value1 = this.getAt(index1);
        int value2 = this.getAt(index2);
        this.swap(new State(index1, value1, index2, value2, true));
    }

    private SorterProtocol binaryTreeProtocol() {
        return new SorterProtocol() {
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
                getSorter().compare(binaryTree[index1], binaryTree[index2]);
            }

            @Override
            public void swap(int index1, int index2) {
                getSorter().swap(binaryTree[index1], binaryTree[index2]);
            }

            @Override
            public int at(int index) {
                Integer valueIndex = binaryTree[index];
                if (valueIndex != null) {
                    getSorter().at(valueIndex);
                }
                return valueIndex;
            }

            @Override
            public int put(int index, int value) {
                binaryTree[index] = value;
                return value;
            }
        };
    }
}
