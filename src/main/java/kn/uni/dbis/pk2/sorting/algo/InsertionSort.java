package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Insertion Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public final class InsertionSort implements Sorter {
    @Override
    public void sort(final DataModel model) throws InterruptedException {
        sort(model, 0, model.getLength());
    }

    /**
     * Sorts the given range of the given data model using Insertion Sort.
     *
     * @param model data model
     * @param start start of the range to sort
     * @param end end of the range to sort (exclusive)
     * @throws InterruptedException if the sorting thread was interrupted
     */
    static void sort(final DataModel model, final int start, final int end) throws InterruptedException {
        model.addArea(start, end);
        final int[] array = model.getValues();
        for (int i = start + 1; i < end; i++) {
            model.changeArea(0, i, end);
            model.setSpecialValue(array[i]);
            for (int j = i; j > 0 && array[j - 1] > array[j]; j--) {
                model.pause();
                model.swap(j - 1, j);
            }
        }
        model.setSpecialValue(-1);
        model.removeArea();
    }
}
