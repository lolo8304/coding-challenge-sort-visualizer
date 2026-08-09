package sorty.algorithms;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class BinaryMaxTreeTest {
    @Test
    void buildsTreeOfIndexesIntoValues() {
        Integer[] values = {4, 9, 1, 7, 3};
        Integer[] tree = new Integer[values.length];

        BinaryMaxTree.buildMaxTree(values, tree, new TreeProtocol(tree));

        assertEquals(values.length, tree.length);
        assertContainsEachIndexOnce(tree);
    }

    @Test
    void rootReferencesLargestValue() {
        Integer[] values = {4, 9, 1, 7, 3};
        Integer[] tree = new Integer[values.length];

        BinaryMaxTree.buildMaxTree(values, tree, new TreeProtocol(tree));

        assertEquals(1, tree[0]);
        assertEquals(9, values[tree[0]]);
    }

    @Test
    void satisfiesMaxHeapInvariant() {
        Integer[] values = {4, 9, 1, 7, 3, 12, 8};
        Integer[] tree = new Integer[values.length];

        BinaryMaxTree.buildMaxTree(values, tree, new TreeProtocol(tree));

        for (int parent = 0; parent < tree.length; parent++) {
            int left = 2 * parent + 1;
            int right = left + 1;
            if (left < tree.length) {
                assertTrue(values[tree[parent]] >= values[tree[left]]);
            }
            if (right < tree.length) {
                assertTrue(values[tree[parent]] >= values[tree[right]]);
            }
        }
    }

    @Test
    void handlesEmptyInput() {
        Integer[] tree = new Integer[0];

        BinaryMaxTree.buildMaxTree(new Integer[0], tree, new TreeProtocol(tree));

        assertArrayEquals(new Integer[0], tree);
    }

    @Test
    void rejectsMismatchedTreeLength() {
        Integer[] values = {4, 9, 1};
        Integer[] tree = new Integer[2];

        assertThrows(
            IllegalArgumentException.class,
            () -> BinaryMaxTree.buildMaxTree(values, tree, new TreeProtocol(tree))
        );
    }

    @Test
    void recordsTreeProtocolOperations() {
        Integer[] values = {4, 9, 1, 7, 3};
        Integer[] tree = new Integer[values.length];
        TreeProtocol protocol = new TreeProtocol(tree);

        BinaryMaxTree.buildMaxTree(values, tree, protocol);

        assertTrue(protocol.putEvents.size() >= values.length);
        assertTrue(protocol.atEvents.size() > 0);
        assertTrue(protocol.compareEvents.size() > 0);
        assertTrue(protocol.swapEvents.size() > 0);
    }

    private void assertContainsEachIndexOnce(Integer[] tree) {
        boolean[] seen = new boolean[tree.length];
        for (Integer index : tree) {
            assertTrue(index >= 0 && index < tree.length);
            assertTrue(!seen[index]);
            seen[index] = true;
        }
    }

    private static class TreeProtocol implements SorterProtocol {
        private final Integer[] tree;
        private final List<Integer> atEvents = new ArrayList<>();
        private final List<String> putEvents = new ArrayList<>();
        private final List<String> compareEvents = new ArrayList<>();
        private final List<String> swapEvents = new ArrayList<>();

        TreeProtocol(Integer[] tree) {
            this.tree = tree;
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
            compareEvents.add(index1 + ":" + index2);
        }

        @Override
        public void swap(int index1, int index2) {
            swapEvents.add(index1 + ":" + index2);
        }

        @Override
        public int at(int index) {
            atEvents.add(index);
            return tree[index];
        }

        @Override
        public int put(int index, int value) {
            tree[index] = value;
            putEvents.add(index + ":" + value);
            return value;
        }
    }
}
