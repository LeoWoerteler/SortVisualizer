package kn.uni.dbis.pk2.sorting.algo;

import java.util.Arrays;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Natural Merge Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public final class MergeSortNatural implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int n = model.getLength();
        if (n < 2) {
            return;
        }
        int[] in = model.getValues();
        int[] out = model.createCopy();
        Arrays.fill(out, -1);
        int start = -1;
        do {
            boolean asc = true;
            int end = 0;
            model.addArea(0, 0);
            while (end < n) {
                start = end;
                do {
                    model.setSpecialValue(in[end]);
                    end++;
                    model.changeArea(0, start, end);
                } while (end < n && model.compare(in, end - 1, end) <= 0);
                while (end < n && model.compare(in, end - 1, end) >= 0) {
                    model.setSpecialValue(in[end]);
                    end++;
                    model.changeArea(0, start, end);
                }
                model.setSpecial(-1);
                merge(model, in, out, start, end - 1, asc);
                asc = !asc;
            }
            model.removeArea();
            final int[] temp = in;
            in = out;
            out = temp;
        } while (start > 0);
        if (in == model.getCopy()) {
            move(model, in, out, 0, n);
        }
        model.destroyCopy();
    }

    /**
     * Merges an ascending and a descending run into one.
     *
     * @param model data model
     * @param in array to read from
     * @param out array to write to
     * @param lo low end
     * @param hi high end
     * @param asc flag for sorting in ascending order
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static void merge(final DataModel model, final int[] in, final int[] out,
            final int lo, final int hi, final boolean asc) throws InterruptedException {
        int k = asc ? lo : hi;
        final int c = asc ? 1 : -1;
        int i = lo;
        int j = hi;
        model.addArea(lo, hi + 1);
        while (i <= j) {
            final int pos;
            if (model.compare(in, i, j) <= 0) {
                pos = i++;
            } else {
                pos = j--;
            }
            final int val = in[pos];
            in[pos] = -1;
            model.setSpecialValue(val);
            model.setValue(out, k, val);
            model.changeArea(0, i, j + 1);
            k += c;
        }
        model.removeArea();
        model.setSpecial(-1);
    }

    /**
     * Moves a range of values between arrays.
     *
     * @param model data model
     * @param in input array
     * @param out output array
     * @param from start of the range
     * @param to end of the range (exclusive)
     * @throws InterruptedException if the sorting thread was interrupted
     */
    static void move(final DataModel model, final int[] in, final int[] out,
            final int from, final int to) throws InterruptedException {
        model.addArea(from, to);
        for (int i = from; i < to; i++) {
            final int val = in[i];
            in[i] = -1;
            model.setSpecialValue(val);
            model.changeArea(0, i + 1, to);
            model.setValue(out, i, val);
        }
        model.removeArea();
        model.setSpecial(-1);
    }
}
