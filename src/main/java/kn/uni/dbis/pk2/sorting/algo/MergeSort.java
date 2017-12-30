package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Merge Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public final class MergeSort implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int[] copy = model.getValues().clone();
        this.mergeSort(model, copy, 0, copy.length);
    }

    /**
     * Sorts the values in the given range using merge sort.
     *
     * @param model data model
     * @param copy copy of the values to sort
     * @param start start of the current range
     * @param n length of the range to sort
     * @throws InterruptedException if the thread was interrupted
     */
    private void mergeSort(final DataModel model, final int[] copy, final int start, final int n)
            throws InterruptedException {
        if (n < 2) {
            return;
        }

        // split phase
        model.addArea(start, start + n);
        final int k = n / 2;
        final int mid = start + k;
        final int end = start + n - 1;
        this.mergeSort(model, copy, start, k);
        this.mergeSort(model, copy, mid, n - k);

        // merge phase
        int i = start;
        int j = start + k;
        for (int o = start; o <= end; o++) {
            if (j > end || i < mid && model.compare(copy, i, j) <= 0) {
                model.setValue(o, copy[i++]);
            } else {
                model.setValue(o, copy[j++]);
            }
            model.setSpecial(o);
        }
        model.setSpecial(-1);
        System.arraycopy(model.getValues(), start, copy, start, n);
        model.removeArea();
    }
}
