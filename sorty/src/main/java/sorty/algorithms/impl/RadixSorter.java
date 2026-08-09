package sorty.algorithms.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sorty.SortDirection;
import sorty.algorithms.DefaultSorter;

@Getter
@Setter
@NoArgsConstructor
public class RadixSorter extends DefaultSorter {
    private static final int RADIX = 256;
    private static final int BITS_PER_PASS = 8;
    private static final int PASSES = Integer.BYTES;
    private static final int SIGN_BIT = Integer.MIN_VALUE;

    @Override
    public void sort(SortDirection direction) {
        this.getSorter().start("Radix Sort " + direction, this.numbers.length);
        int[] sorted = sortedValues();
        writeSorted(sorted, direction);
        this.getSorter().finish();
    }

    private int[] sortedValues() {
        int[] source = new int[this.numbers.length];
        int[] target = new int[this.numbers.length];

        for (int i = 0; i < this.numbers.length; i++) {
            source[i] = this.getAt(i);
            this.sorter.put(i, source[i]);
        }

        for (int pass = 0; pass < PASSES; pass++) {
            int shift = pass * BITS_PER_PASS;
            int[] counts = new int[RADIX];

            var t = 0;
            for (int value : source) {
                this.sorter.at(t++);
                counts[bucket(value, shift)]++;
            }

            int total = 0;
            for (int i = 0; i < counts.length; i++) {
                int count = counts[i];
                counts[i] = total;
                total += count;
            }

            t = 0;
            for (int value : source) {
                this.sorter.at(t++);
                this.sorter.put(counts[bucket(value, shift)], value);
                target[counts[bucket(value, shift)]++] = value;
            }

            int[] swap = source;
            source = target;
            target = swap;
        }

        return source;
    }

    private int bucket(int value, int shift) {
        return ((value ^ SIGN_BIT) >>> shift) & 0xFF;
    }

    private void writeSorted(int[] sorted, SortDirection direction) {
        for (int i = 0; i < sorted.length; i++) {
            int sourceIndex = direction == SortDirection.ASCENDING ? i : sorted.length - 1 - i;
            this.sorter.at(sourceIndex);
            this.putAt(i, sorted[sourceIndex]);
        }
    }
}
