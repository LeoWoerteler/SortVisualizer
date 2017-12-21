package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Quick Sort algorithm, using the median of first, last, and middle value as pivot.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class QuickSort3 implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        sort(model, 0, model.getLength());
    }

    /**
     * Sorts the given range of the given data model using Quick Sort.
     *
     * @param model data model
     * @param start start of the range to sort
     * @param end end of the range to sort (exclusive)
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private void sort(final DataModel model, final int start, final int end) throws InterruptedException {
        if (end - start < 2) {
            return;
        }

        model.addArea(start, end);
        final int[] array = model.getValues();
        final int pivot = medianOfThree(array[start], array[(start + end) / 2], array[end - 1]);
        model.setSpecialValue(pivot);
        int l = start;
        int m = start;
        int r = end;
        model.addArea(m, r);
        for (;;) {
            while (m < r && array[r - 1] > pivot) {
                r--;
                model.changeArea(0, m, r);
                model.pause();
            }
            while (m < r) {
                if (array[m] < pivot) {
                    model.swap(l++, m++);
                } else if (array[m] == pivot) {
                    m++;
                } else {
                    break;
                }
                model.changeArea(0, m, r);
                model.pause();
            }
            if (m >= r) {
                break;
            }
            model.swap(m, --r);
            model.changeArea(0, m, r);
            model.pause();
        }
        model.removeArea();
        model.setSpecialValue(-1);
        sort(model, start, l);
        sort(model, r, end);
        model.removeArea();
    }

    /**
     * Computes the middle value out of three values.
     *
     * @param a first value to compare
     * @param b second value to compare
     * @param c third value to compare
     * @return median of the three given values
     */
    static final int medianOfThree(final int a, final int b, final int c) {
        return a < b ? (b < c ? b : (c < a ? a : c))
                     : (c < b ? b : (c > a ? a : c));
    }
}
