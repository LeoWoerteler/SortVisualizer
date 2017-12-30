package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Natural Merge Sort algorithm in an iterative variant.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public final class MergeSortNaturalIterative2 implements Sorter {

    /** Minimum number of values in an initial run. */
    private static final int MIN_RUN = 10;

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int n = model.getLength();
        if (n < 2) {
            return;
        }
        final int[] values = model.getValues();
        final int[] copy = values.clone();
        final int[] sizes = new int[32 - Integer.numberOfLeadingZeros(n - 1)];
        int numRuns = 0;
        int from = 0;
        int dir = 1;
        while (from < n) {
            int to = from + 1;
            model.addArea(from, to);
            while (to < n && dir * model.compare(to, to - 1) >= 0) {
                model.setSpecial(to);
                to++;
                model.changeArea(0, from, to);
            }

            if (dir < 0) {
                // reverse the run
                for (int i = (to - from) / 2; --i >= 0;) {
                    model.setSpecial(to - 1 - i);
                    model.swap(from + i, to - 1 - i);
                }
            }
            while (to < n && to - from < MIN_RUN) {
                int curr = to++;
                model.changeArea(0, from, to);
                model.setSpecial(curr);
                while (curr > from && model.compare(curr, curr - 1) < 0) {
                    model.swap(curr - 1, curr);
                    curr--;
                }
            }
            System.arraycopy(values, from, copy, from, to - from);
            model.setSpecial(-1);

            while (numRuns > 0 && sizes[numRuns - 1] < 2 * (to - from)) {
                final int nextSize = sizes[numRuns - 1];
                final int off = from - nextSize;
                model.changeArea(0, off, to);
                merge(model, copy, off, from, to);
                System.arraycopy(values, off, copy, off, to - off);
                numRuns--;
                from = off;
            }
            sizes[numRuns++] = to - from;
            model.removeArea();
            from = to;
            dir *= -1;
        }

        int mid = -1;
        model.addArea(n, n);
        while (--numRuns >= 0) {
            if (mid == -1) {
                mid = n - sizes[numRuns];
                model.changeArea(0, mid, n);
            } else {
                final int off = mid - sizes[numRuns];
                model.changeArea(0, off, n);
                merge(model, copy, off, mid, n);
                System.arraycopy(values, off, copy, off, n - off);
                mid = off;
            }
        }
        model.removeArea();
    }

    /**
     * Merges two sorted runs into one.
     *
     * @param model data model
     * @param copy copy to read from
     * @param start start of the first run
     * @param mid start of the second run
     * @param end end of the second run
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static void merge(final DataModel model, final int[] copy,
            final int start, final int mid, final int end) throws InterruptedException {
        model.addArea(start, mid);
        model.addArea(mid, end);
        int i = start;
        int j = mid;
        for (int o = start; o < end; o++) {
            if (j == end || i < mid && model.compare(copy, i, j) <= 0) {
                model.setValue(o, copy[i++]);
            } else {
                model.setValue(o, copy[j++]);
            }
            model.setSpecial(o);
            model.changeArea(1, i, mid);
            model.changeArea(0, j, end);
        }
        model.setSpecial(-1);
        model.removeArea();
        model.removeArea();
    }
}
