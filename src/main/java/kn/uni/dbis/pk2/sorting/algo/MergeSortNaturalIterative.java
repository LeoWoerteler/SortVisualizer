package kn.uni.dbis.pk2.sorting.algo;

import java.util.Arrays;

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
        final int[] copy = model.createCopy();
        Arrays.fill(copy, -1);
        final int[] sizes = new int[32 - Integer.numberOfLeadingZeros(n - 1)];
        int from = 0;
        int runs = 0;
        while (from < n) {
            int to = from + 1;
            model.addArea(from, to);
            while (to < n && model.compare(to, to - 1) >= 0) {
                model.setSpecial(to);
                to++;
                model.changeArea(0, from, to);
            }
            int mid = to;
            while (to < n && model.compare(to, to - 1) <= 0) {
                model.setSpecial(to);
                to++;
                model.changeArea(0, from, to);
            }
            model.setSpecial(-1);
            runs++;

            int level = 0;
            int off = from;
            int[] in = values;
            int[] out = copy;
            while (sizes[level] != 0) {
                final int size = sizes[level];
                merge(model, level % 2 == 0, off, mid, to - 1, mid - 1, true);
                sizes[level] = 0;
                mid = off;
                off -= size;
                model.changeArea(0, off, to);
                level++;
                final int[] temp = in;
                in = out;
                out = temp;
            }
            merge(model, level % 2 == 0, off, mid, to - 1, mid - 1, false);
            sizes[level] = to - off;
            model.removeArea();
            from = to;
        }

        final int maxLevel = 31 - Integer.numberOfLeadingZeros(runs);
        int mid = -1;
        model.addArea(n, n);
        boolean lastOnCopy = true;
        for (int l = 0; l <= maxLevel; l++) {
            if (sizes[l] > 0) {
                boolean onCopy = l % 2 == 0;
                if (mid == -1) {
                    mid = n - sizes[l];
                    model.changeArea(0, mid, n);
                    lastOnCopy = onCopy;
                } else {
                    final int off = mid - sizes[l];
                    model.changeArea(0, off, n);
                    int[] in = onCopy ? copy : values;
                    int[] out = onCopy ? values : copy;
                    if (lastOnCopy != onCopy) {
                        final int oldSize = n - mid;
                        final int newSize = sizes[l];
                        if (oldSize > newSize) {
                            MergeSortNatural.move(model, in, out, off, mid);
                            onCopy = lastOnCopy;
                            final int[] temp = in;
                            in = out;
                            out = temp;
                        } else {
                            MergeSortNatural.move(model, out, in, mid, n);
                        }
                    }
                    merge(model, out == copy, off, mid, mid, n, false);
                    mid = off;
                    lastOnCopy = !onCopy;
                }
            }
        }
        if (lastOnCopy) {
            MergeSortNatural.move(model, copy, values, 0, n);
        }
        model.removeArea();
    }

    /**
     * Merges two sorted runs into one, where the direction of inputs as well as output
     * can be ascending or descending.
     *
     * @param model data model
     * @param toCopy flag indicating if the output should be written to the copy
     * @param a0 start of the first run
     * @param a1 end of the first run
     * @param b0 start of the second run
     * @param b1 end of the second run
     * @param descOut flag for making the output run descending
     * @throws InterruptedException if the sorting thread was interrupted
     */
    static void merge(final DataModel model, final boolean toCopy,
            final int a0, final int a1, final int b0, final int b1,
            final boolean descOut) throws InterruptedException {
        final int add = descOut ? -1 : 1;
        final int addI = a0 < a1 ? 1 : -1;
        final int addJ = b0 < b1 ? 1 : -1;
        final int lastA = a1 - addI;
        final int lastB = b1 - addJ;
        model.addArea(Math.min(a0, lastA), Math.max(a0, lastA) + 1);
        model.addArea(Math.min(b0, lastB), Math.max(b0, lastB) + 1);

        final int[] in = toCopy ? model.getValues() : model.getCopy();
        final int[] out = toCopy ? model.getCopy() : model.getValues();
        int i = a0;
        int j = b0;
        int o = descOut ? j : i;
        while (i != a1 || j != b1) {
            final int pos;
            if (j == b1 || i != a1 && model.compare(in, i, j) <= 0) {
                pos = i;
                i += addI;
            } else {
                pos = j;
                j += addJ;
            }
            final int val = in[pos];
            in[pos] = -1;
            model.setSpecialValue(val);
            changeArea(model, 1, i, lastA);
            changeArea(model, 0, j, lastB);
            model.setValue(out, o, val);
            o += add;
        }
        model.setSpecial(-1);
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
