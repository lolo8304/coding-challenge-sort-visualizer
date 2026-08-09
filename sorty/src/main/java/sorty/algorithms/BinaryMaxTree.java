package sorty.algorithms;

public final class BinaryMaxTree {
    private BinaryMaxTree() {
    }

    public static void buildMaxTree(Integer[] values, Integer[] tree, SorterProtocol treeProtocol) {
        buildMaxTree(values, tree, treeProtocol, values.length);
    }

    public static void buildMaxTree(Integer[] values, Integer[] tree, SorterProtocol treeProtocol, int n) {
        if (tree.length < n || values.length < n) {
            throw new IllegalArgumentException("Tree and values lengths must be at least n.");
        }

        // tree contains references/indexes into values[]
        for (int i = 0; i < n; i++) {
            treeProtocol.put(i, i);
        }

        // bottom-up heap construction: O(N)
        for (int i = n / 2 - 1; i >= 0; i--) {
            siftDown(values, treeProtocol, i, n);
        }
    }

    static void siftDown(Integer[] values, SorterProtocol treeProtocol, int i, int n) {
        while (true) {
            int largest = i;
            int left = 2 * i + 1;
            int right = left + 1;

            if (left < n) {
                treeProtocol.compare(left, largest);
                if (treeValue(values, treeProtocol, left) > treeValue(values, treeProtocol, largest)) {
                    largest = left;
                }
            }

            if (right < n) {
                treeProtocol.compare(right, largest);
                if (treeValue(values, treeProtocol, right) > treeValue(values, treeProtocol, largest)) {
                    largest = right;
                }
            }

            if (largest == i) {
                return;
            }

            Integer tmp = treeProtocol.at(i);
            treeProtocol.swap(i, largest);
            treeProtocol.put(i, treeProtocol.at(largest));
            treeProtocol.put(largest, tmp);

            i = largest;
        }
    }

    private static int treeValue(Integer[] values, SorterProtocol treeProtocol, int treeIndex) {
        int valueIndex = treeProtocol.at(treeIndex);
        return values[valueIndex];
    }
}
