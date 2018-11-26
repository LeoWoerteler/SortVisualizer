package kn.uni.dbis.cs.sorting;

import java.util.Arrays;
import java.util.Random;

/**
 * Enumeration of all available data orderings.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public enum DataOrdering {
    /** Uniformly random order. */
    RANDOM("Random Order") {
        @Override
        public void fill(final int[] values, final int max, final Random rng) {
            for (int i = 0; i < values.length; i++) {
                values[i] = rng.nextInt(max);
            }
        }
    },

    /** Random order of multiples of eight. */
    RANDOM_DUPL("Random Order (many duplicates)") {
        @Override
        public void fill(final int[] values, final int max, final Random rng) {
            for (int i = 0; i < values.length; i++) {
                values[i] = rng.nextInt(max) & ~0x7;
            }
        }
    },

    /** Sorted values where 5% of the values are randomly swapped. */
    ALMOST_SORTED("Almost Sorted") {
        @Override
        public void fill(final int[] values, final int max, final Random rng) {
            ASCENDING.fill(values, max, rng);
            final int n = values.length;
            final int k = Math.max(n / 20, 5);
            for (int i = 0; i < k; i++) {
                final int a = rng.nextInt(n);
                final int b0 = rng.nextInt(n - 1);
                final int b = b0 < a ? b0 : b0 + 1;
                final int tmp = values[a];
                values[a] = values[b];
                values[b] = tmp;
            }
        }
    },

    /** Already completely sorted. */
    ASCENDING("Ascending Order") {
        @Override
        public void fill(final int[] values, final int max, final Random rng) {
            for (int i = 0; i < values.length; i++) {
                values[i] = Math.min((int) Math.round(1.0 * i / values.length * max), max - 1);
            }
        }
    },

    /** Sorted in reverse. */
    DESCENDING("Descending Order") {
        @Override
        public void fill(final int[] values, final int max, final Random rng) {
            final int n = values.length;
            for (int i = 0; i < n; i++) {
                values[n - i - 1] = Math.min((int) Math.round(1.0 * i / n * max), max - 1);
            }
        }
    },

    /** First half is in descending order, the second one in ascending order. */
    VALLEY("Valley") {
        @Override
        public void fill(final int[] values, final int max, final Random rng) {
            final int n = values.length;
            final int k = n / 2;
            for (int i = 0; i < k; i++) {
                final int val = Math.min((int) Math.round(1.0 * (k - 1 - i) / k * max), max - 1);
                values[i] = val;
                values[n - 1 - i] = val;
            }
        }
    },

    /** First half is in ascending order, the second one in descending order. */
    HILL("Hill") {
        @Override
        public void fill(final int[] values, final int max, final Random rng) {
            final int n = values.length;
            final int k = (n + 1) / 2;
            for (int i = 0; i < k; i++) {
                final int val = Math.min((int) Math.round(1.0 * i / k * max), max - 1);
                values[i] = val;
                values[n - 1 - i] = val;
            }
        }
    },

    /** Sequence of ascending and descending segments. */
    SAW_TEETH("Saw Teeth") {
        @Override
        public void fill(final int[] values, final int max, final Random rng) {
            final int n = values.length;
            final int numTeeth = Math.min(7, n / 3);
            final int minSize = n / numTeeth;
            final int add = n % numTeeth;

            for (int i = 0; i < values.length; i++) {
                final int val = Math.min((int) Math.round(1.0 * i / values.length * max), max - 1);
                final int tooth = i % numTeeth;
                final int pos = i / numTeeth;
                final int offset = tooth * minSize + Math.min(tooth, add);
                if (pos % 2 == 0) {
                    values[offset + pos / 2] = val;
                } else {
                    final int size = tooth < add ? minSize + 1 : minSize;
                    values[offset + size - 1 - pos / 2] = val;
                }
            }
        }
    },

    /** A sine wave form. */
    SINE_WAVE("Sine Wave") {
        @Override
        public void fill(final int[] values, final int max, final Random rng) {
            for (int i = 0; i < values.length; i++) {
                final double x = 6 * Math.PI * (i + 0.6 * (rng.nextDouble() - 0.5)) / values.length;
                final double sin = Math.sin(x);
                final double scaledSin = (sin + 1) * max / 2;
                values[i] = Math.max(0, Math.min((int) scaledSin, max - 1));
            }
        }
    },
    ONLY_SAME("Random Only Same") {
        @Override
        public void fill(int[] values, int max, Random rng) {
            Arrays.fill(values, rng.nextInt(max));
        }
    };

    /** Name of the data ordering. */
    private final String name;

    /**
     * Constructor.
     *
     * @param name name of the data ordering
     */
    DataOrdering(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Fills the given array with values conforming to this ordering.
     *
     * @param values output array
     * @param max upper bound on the values
     * @param rng random-number generator
     */
    public abstract void fill(int[] values, int max, Random rng);
}
