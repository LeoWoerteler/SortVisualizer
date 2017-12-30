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
        model.setSpecial(start);
        int l = start + 1;
        int r = end - 1;
        model.addArea(l, r + 1);
        for (;;) {
            while (l <= r && model.compare(l, start) <= 0) {
                l++;
                model.changeArea(0, l, r + 1);
            }
            while (l <= r && model.compare(r, start) > 0) {
                r--;
                model.changeArea(0, l, r + 1);
            }
            if (l > r) {
                break;
            }
            model.swap(l++, r--);
            model.changeArea(0, l, r + 1);
        }
        model.swap(start, r);
        model.removeArea();
        model.setSpecial(-1);
        sort(model, start, r);
        sort(model, l, end);
        model.removeArea();
    }
}
