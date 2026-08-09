package sorty;

import sorty.algorithms.impl.BubbleSorter;
import sorty.algorithms.DefaultSorter;
import sorty.algorithms.impl.InsertionSorter;
import sorty.algorithms.impl.MergeSorter;
import sorty.algorithms.impl.SelectionSorter;

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
    };

    public abstract DefaultSorter createSorter();
}
