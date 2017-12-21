package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * 
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public final class MergeSort implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int[] copy = model.getValues().clone();
        this.mergeSort(model, copy, 0, copy.length - 1);
    }

    /**
     * Sorts the values in the given range using merge sort.
     *
     * @param model data model
     * @param copy copy of the values to sort
     * @param start start of the current range
     * @param end end of the current range
     * @throws InterruptedException if the thread was interrupted
     */
    private void mergeSort(final DataModel model, final int[] copy, final int start, final int end)
            throws InterruptedException {
        final int n = end - start + 1;
        if (n < 2) {
            return;
        }

        // split phase
        model.addArea(start, end + 1);
        final int mid = start + n / 2;
        this.mergeSort(model, copy, start, mid - 1);
        this.mergeSort(model, copy, mid, end);

        // merge phase
        final int[] values = model.getValues();
        int i = start;
        int j = mid;
        for (int o = start; o <= end; o++) {
            model.pause();
            if (j > end || i < mid && copy[i] <= copy[j]) {
                values[o] = copy[i++];
            } else {
                values[o] = copy[j++];
            }
            model.setSpecialValue(values[o]);
        }
        model.setSpecialValue(-1);
        System.arraycopy(values, start, copy, start, n);
        model.removeArea();
    }
}
