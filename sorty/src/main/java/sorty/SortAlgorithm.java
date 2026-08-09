package sorty;

import sorty.algorithms.impl.*;
import sorty.algorithms.DefaultSorter;

public enum SortAlgorithm {
    BUBBLE {
        @Override
        public DefaultSorter createSorter() {
            return new BubbleSorter();
        }
    },
    INSERT {
        @Override
        public DefaultSorter createSorter() {
            return new InsertionSorter();
        }
    },
    SELECTION {
        @Override
        public DefaultSorter createSorter() {
            return new SelectionSorter();
        }
    },
    MERGE {
        @Override
        public DefaultSorter createSorter() {
            return new MergeSorter();
        }
    },
    QUICK {
        @Override
        public DefaultSorter createSorter() {
            return new QuickSorter();
        }
    },
    HEAP {
        @Override
        public DefaultSorter createSorter() {
            return new HeapSorter();
        }
    },
    SHELL {
        @Override
        public DefaultSorter createSorter() {
            return new ShellSorter();
        }
    },
    RADIX {
        @Override
        public DefaultSorter createSorter() {
            return new RadixSorter();
        }
    };

    public abstract DefaultSorter createSorter();
}
