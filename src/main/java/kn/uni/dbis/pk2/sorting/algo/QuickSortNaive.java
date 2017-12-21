package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * A naïve implementation of the Quick Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class QuickSortNaive implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        sort(model, 0, model.getLength());
    }

    /**
     * Sorts the given range of the given data model.
     * @param model data model
     * @param start start of the range to sort
     * @param end end of the range to sort
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private void sort(final DataModel model, final int start, final int end) throws InterruptedException {
        if (end - start < 2) {
            return;
        }
        model.addArea(start, end);
        final int[] array = model.getValues();
        final int pivot = array[start];
        model.setSpecialValue(pivot);
        int l = start + 1;
        int r = end - 1;
        model.addArea(l, r + 1);
        for (;;) {
            while (l <= r && array[l] <= pivot) {
                l++;
                model.changeArea(0, l, r + 1);
                model.pause();
            }
            while (l <= r && array[r] > pivot) {
                r--;
                model.changeArea(0, l, r + 1);
                model.pause();
            }
            if (l > r) {
                break;
            }
            model.swap(l++, r--);
            model.changeArea(0, l, r + 1);
            model.pause();
        }
        model.swap(start, r);
        model.removeArea();
        model.setSpecialValue(-1);
        model.pause();
        sort(model, start, r);
        sort(model, l, end);
        model.removeArea();
    }
}
