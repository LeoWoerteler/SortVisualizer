package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Insertion Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public final class CombSort implements Sorter {
    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int n = model.getLength();
        int gap = n;
        boolean sorted;
        do {
            gap = gap * 10 / 13;
            if (gap > 1) {
                sorted = false;
            } else {
                gap = 1;
                sorted = true;
            }
            model.addArea(0, 0);
            model.addArea(0, 0);
            for (int i = gap; i < n; i++) {
                model.changeArea(0, i - gap, i - gap + 1);
                model.changeArea(1, i, i + 1);
                model.setSpecial(i);
                if (model.compare(i - gap, i) > 0) {
                    model.swap(i - gap, i);
                    sorted = false;
                }
            }
            model.removeArea();
            model.removeArea();
            model.setSpecial(-1);
        } while (!sorted);
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
        for (int i = start + 1; i < end; i++) {
            model.changeArea(0, i, end);
            model.setSpecial(i);
            for (int j = i; j > 0 && model.compare(j - 1, j) > 0; j--) {
                model.swap(j - 1, j);
            }
        }
        model.setSpecial(-1);
        model.removeArea();
    }
}
