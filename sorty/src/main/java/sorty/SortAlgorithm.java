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
    },
    COCKTAIL {
        @Override
        public DefaultSorter createSorter() {
            return new CocktailSorter();
        }
    },
    COMB {
        @Override
        public DefaultSorter createSorter() {
            return new CombSorter();
        }
    },
    GNOME {
        @Override
        public DefaultSorter createSorter() {
            return new GnomeSorter();
        }
    },
    TIM {
        @Override
        public DefaultSorter createSorter() {
            return new TimSorter();
        }
    },
    INTRO {
        @Override
        public DefaultSorter createSorter() {
            return new IntroSorter();
        }
    },
    BOGO {
        @Override
        public DefaultSorter createSorter() {
            return new BogoSorter();
        }
    };

    public abstract DefaultSorter createSorter();
}
