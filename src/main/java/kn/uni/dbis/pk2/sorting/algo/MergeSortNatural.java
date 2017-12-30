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
        final int n = model.getLength();
        if (n < 2) {
            return;
        }
        final int[] values = model.getValues();
        final int[] copy = values.clone();
        int start = -1;
        do {
            boolean asc = true;
            int end = 0;
            model.addArea(0, 0);
            while (end < n) {
                start = end;
                do {
                    model.setSpecial(end);
                    end++;
                    model.changeArea(0, start, end);
                } while (end < n && model.compare(end - 1, end) <= 0);
                while (end < n && model.compare(end - 1, end) >= 0) {
                    model.setSpecial(end);
                    end++;
                    model.changeArea(0, start, end);
                }
                model.setSpecial(-1);
                merge(model, copy, start, end - 1, asc);
                System.arraycopy(values, start, copy, start, end - start);
                asc = !asc;
            }
            model.removeArea();
        } while (start > 0);
    }

    /**
     * Merges an ascending and a descending run into one.
     *
     * @param model data model
     * @param copy array to read from
     * @param lo low end
     * @param hi high end
     * @param asc flag for sorting in ascending order
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static void merge(final DataModel model, final int[] copy,
            final int lo, final int hi, final boolean asc) throws InterruptedException {
        int k = asc ? lo : hi;
        final int c = asc ? 1 : -1;
        int i = lo;
        int j = hi;
        model.addArea(lo, hi + 1);
        while (i <= j) {
            if (model.compare(copy, i, j) <= 0) {
                model.setValue(k, copy[i++]);
            } else {
                model.setValue(k, copy[j--]);
            }
            model.setSpecial(k);
            model.changeArea(0, i, j + 1);
            k += c;
        }
        model.removeArea();
        model.setSpecial(-1);
    }
}
