package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Bubble Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public final class BubbleSort implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        sort(model, 0, model.getLength());
    }

    /**
     * Sorts a range of the given data model using Bubble Sort.
     *
     * @param model data model
     * @param start start of the range (inclusive)
     * @param end end of the range (exclusive)
     * @throws InterruptedException if the sorting thread was interrupted
     */
    static void sort(final DataModel model, final int start, final int end)
            throws InterruptedException {
        for (int r = end, last; r - start > 1; r = last) {
            model.addArea(start, r);
            last = start;
            for (int l = start + 1; l < r; l++) {
                model.setSpecial(l - 1);
                if (model.compare(l - 1, l) > 0) {
                    last = l;
                    model.swap(l - 1, l);
                }
                model.changeArea(0, l, r);
            }
            model.setSpecial(-1);
            model.removeArea();
        }
    }
}
