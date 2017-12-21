package kn.uni.dbis.pk2.sorting.algo;

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
        final int[] values = model.getValues();
        if (values.length < 2) {
            return;
        }
        final int[] copy = values.clone();
        int start = -1;
        do {
            boolean asc = true;
            for (int end = 0; end < values.length; asc = !asc) {
                start = end;
                model.addArea(start, end);
                int curr;
                do {
                    model.pause();
                    curr = values[end++];
                    model.setSpecialValue(curr);
                    model.changeArea(0, start, end);
                } while (end < values.length && curr <= values[end]);
                while (end < values.length && curr >= values[end]) {
                    model.pause();
                    curr = values[end++];
                    model.setSpecialValue(curr);
                    model.changeArea(0, start, end);
                }
                model.setSpecialValue(-1);
                merge(model, copy, values, start, end - 1, asc);
                System.arraycopy(values, start, copy, start, end - start);
                model.removeArea();
            }
        } while (start != 0);
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
            model.pause();
            if (in[i] <= in[j]) {
                out[k] = in[i++];
            } else {
                out[k] = in[j--];
            }
            model.setSpecialValue(out[k]);
            model.changeArea(0, i, j + 1);
            k += c;
        }
        model.removeArea();
        model.setSpecialValue(-1);
    }
}
