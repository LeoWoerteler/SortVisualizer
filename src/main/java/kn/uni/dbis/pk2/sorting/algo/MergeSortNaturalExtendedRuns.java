package kn.uni.dbis.pk2.sorting.algo;

import java.util.Arrays;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Natural Merge Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public final class MergeSortNaturalExtendedRuns implements Sorter {

    /** Size of the buffer of values that belong to the next run. */
    private static final int MIN_SIZE = 5;

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int n = model.getLength();
        if (n <= MIN_SIZE) {
            InsertionSort.sort(model, 0, n);
            return;
        }

        final int[] values = model.getValues();
        final int[] copy = model.createCopy();
        Arrays.fill(copy, -1);
        final int[] sizes = new int[32 - Integer.numberOfLeadingZeros(n)];
        int numRuns = 0;
        for (int start = 0; start < n; numRuns++) {
            // create another run
            final int end = nextRun(model, start, n);

            model.addArea(start, end);
            int level = 0;
            while (sizes[level] != 0) {
                final int mid = start;
                start -= sizes[level];
                model.changeArea(0, start, end);
                merge(model, level % 2 == 0, start, mid, end);
                sizes[level] = 0;
                level++;
            }
            sizes[level] = end - start;
            start = end;
            model.removeArea();
        }

        final int maxLevel = 31 - Integer.numberOfLeadingZeros(numRuns);
        int mid = n;
        boolean lastOnCopy = false;
        for (int i = 0; i <= maxLevel; i++) {
            boolean onCopy = i % 2 != 0;
            if (sizes[i] > 0) {
                final int start = mid - sizes[i];
                if (mid != n) {
                    if (onCopy != lastOnCopy) {
                        final int curr = mid - start;
                        final int prev = n - mid;
                        if (curr < prev || !onCopy && start == 0) {
                            MergeSortNatural.move(model, onCopy ? copy : values,
                                    onCopy ? values : copy, start, mid);
                            onCopy = !onCopy;
                        } else {
                            MergeSortNatural.move(model, lastOnCopy ? copy : values,
                                    lastOnCopy ? values : copy, mid, n);
                        }
                    }
                    lastOnCopy = !onCopy;
                    merge(model, lastOnCopy, start, mid, n);
                } else {
                    lastOnCopy = onCopy;
                }
                mid = start;
            }
        }
        if (lastOnCopy) {
            MergeSortNatural.move(model, copy, values, 0, n);
        }
    }

    /**
     * Generates the next <em>run</em>, i.e. the next ascending sequence of values from the input array.
     *
     * @param model data model of values to sort
     * @param start start of the not yet sorted part of the array
     * @param n size of the array
     * @return end of the next run
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static int nextRun(final DataModel model, final int start, final int n)
            throws InterruptedException {
        int end = start;
        int endCurr = Math.min(end + MIN_SIZE, n);
        model.addArea(end, endCurr);
        model.addArea(start, endCurr);
        InsertionSort.sort(model, end, endCurr);
        do {
            end++;
            model.changeArea(1, end, endCurr);
            final int next = end + MIN_SIZE - 1;
            if (next < n && model.compare(end - 1, next) <= 0) {
                // next element belongs into current run
                model.setSpecial(next);
                model.changeArea(0, start, endCurr + 1);
                model.changeArea(1, end, endCurr + 1);
                model.swap(next, endCurr++);
                int pos = endCurr - 1;
                while (pos > end && model.compare(pos - 1, pos) > 0) {
                    model.swap(pos - 1, pos);
                    pos--;
                }
            }
        } while (end < endCurr);
        model.removeArea();
        model.setSpecial(-1);
        return end;
    }

    /**
     * Merges two runs of sorted values.
     *
     * @param model data model
     * @param toCopy flag indicating if the result should be written to the copy
     * @param start start of the first run
     * @param mid point between the two runs
     * @param end end of the second run
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static void merge(final DataModel model, final boolean toCopy,
            final int start, final int mid, final int end) throws InterruptedException {
        final int[] in = toCopy ? model.getValues() : model.getCopy();
        final int[] out = toCopy ? model.getCopy() : model.getValues();
        int l = start;
        int r = mid;
        model.addArea(start, mid);
        model.addArea(mid, end);
        for (int o = start; o < end; o++) {
            final int pos = r == end || l < mid && model.compare(in, l, r) <= 0 ? l++ : r++;
            model.changeArea(1, l, mid);
            model.changeArea(0, r, end);
            final int val = in[pos];
            model.setSpecialValue(val);
            in[pos] = -1;
            model.setValue(out, o, val);
        }
        model.setSpecial(-1);
        model.removeArea();
        model.removeArea();
    }
}
