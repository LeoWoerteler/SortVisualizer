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
        model.swap(start, medianOfThree(model, start, start + (end - start) >>> 1, end - 1));
        model.setSpecial(start);
        int l = start;
        int m = start + 1;
        int r = end;
        model.addArea(m, r);
        for (;;) {
            while (m < r && model.compare(r - 1, m - 1) > 0) {
                r--;
                model.changeArea(0, m, r);
            }
            while (m < r) {
                final int cmp = model.compare(m, m - 1);
                if (cmp < 0) {
                    model.swap(l++, m++);
                } else if (cmp == 0) {
                    m++;
                } else {
                    break;
                }
                model.changeArea(0, m, r);
            }
            if (m >= r) {
                break;
            }
            model.swap(m, --r);
            model.changeArea(0, m, r);
        }
        model.removeArea();
        model.setSpecial(-1);
        sort(model, start, l);
        sort(model, r, end);
        model.removeArea();
    }

    /**
     * Computes the middle value out of three values in the given data model.
     *
     * @param model data model
     * @param a first index to compare
     * @param b second index to compare
     * @param c third index to compare
     * @return index of the median of the three given values
     * @throws InterruptedException if the sorting thread was interrupted
     */
    static final int medianOfThree(final DataModel model, final int a, final int b, final int c)
            throws InterruptedException {
        return model.compare(a, b) < 0
                ? (model.compare(b, c) < 0 ? b : (model.compare(c, a) < 0 ? a : c))
                : (model.compare(b, c) > 0 ? b : (model.compare(c, a) > 0 ? a : c));
    }
}
