package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Natural Merge Sort algorithm in an iterative variant.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public final class MergeSortNaturalIterative implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int n = model.getLength();
        if (n < 2) {
            return;
        }
        final int[] values = model.getValues();
        final int[] copy = values.clone();
        final int[] levels = new int[32 - Integer.numberOfLeadingZeros(n - 1)];
        int maxLevel = 0;
        int from = 0;
        while (from < n) {
            int to = from + 1;
            model.addArea(from, to);
            while (to < n && values[to] >= values[to - 1]) {
                model.setSpecialValue(values[to]);
                model.pause();
                to++;
                model.changeArea(0, from, to);
            }
            int mid = to;
            while (to < n && values[to] <= values[to - 1]) {
                model.setSpecialValue(values[to]);
                model.pause();
                to++;
                model.changeArea(0, from, to);
            }
            model.setSpecialValue(-1);

            int level = 0;
            int off = from;
            while (levels[level] != 0) {
                final int size = levels[level];
                merge(model, copy, off, mid, to - 1, mid - 1, true);
                System.arraycopy(values, off, copy, off, to - off);
                levels[level] = 0;
                mid = off;
                off -= size;
                model.changeArea(0, off, to);
                level++;
            }
            merge(model, copy, off, mid, to - 1, mid - 1, false);
            System.arraycopy(values, off, copy, off, to - off);
            levels[level] = to - off;
            maxLevel = Math.max(maxLevel, level);
            model.removeArea();
            from = to;
        }

        int mid = -1;
        model.addArea(n, n);
        for (int l = 0; l <= maxLevel; l++) {
            if (levels[l] > 0) {
                if (mid == -1) {
                    mid = n - levels[l];
                    model.changeArea(0, mid, n);
                } else {
                    final int off = mid - levels[l];
                    model.changeArea(0, off, n);
                    merge(model, copy, off, mid, mid, n, false);
                    System.arraycopy(values, off, copy, off, n - off);
                    mid = off;
                }
            }
        }
        model.removeArea();
    }

    /**
     * Merges two sorted runs into one, where the direction of inputs as well as output
     * can be ascending or descending.
     *
     * @param model data model
     * @param copy copy to read from
     * @param a0 start of the first run
     * @param a1 end of the first run
     * @param b0 start of the second run
     * @param b1 end of the second run
     * @param descOut flag for making the output run descending
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static void merge(final DataModel model, final int[] copy,
            final int a0, final int a1, final int b0, final int b1,
            final boolean descOut) throws InterruptedException {
        final int[] values = model.getValues();
        final int add = descOut ? -1 : 1;
        final int addI = a0 < a1 ? 1 : -1;
        final int addJ = b0 < b1 ? 1 : -1;
        final int lastA = a1 - addI;
        final int lastB = b1 - addJ;
        model.addArea(Math.min(a0, lastA), Math.max(a0, lastA) + 1);
        model.addArea(Math.min(b0, lastB), Math.max(b0, lastB) + 1);

        int i = a0;
        int j = b0;
        int o = descOut ? j : i;
        while (i != a1 || j != b1) {
            model.pause();
            if (j == b1 || i != a1 && copy[i] <= copy[j]) {
                values[o] = copy[i];
                i += addI;
            } else {
                values[o] = copy[j];
                j += addJ;
            }
            model.setSpecialValue(values[o]);
            changeArea(model, 1, i, lastA);
            changeArea(model, 0, j, lastB);
            o += add;
        }
        model.setSpecialValue(-1);
        model.removeArea();
        model.removeArea();
    }

    /**
     * Adds a highlighted area to the data model where the start and end can be either direction.
     *
     * @param model data model
     * @param pos position of the area
     * @param a one end of the area
     * @param b other end of the area
     */
    private static void changeArea(final DataModel model, final int pos, final int a, final int b) {
        model.changeArea(pos, Math.min(a, b), Math.max(a, b) + 1);
    }
}
