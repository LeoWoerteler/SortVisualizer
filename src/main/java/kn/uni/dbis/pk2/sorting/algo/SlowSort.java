package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Slow Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class SlowSort implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        slowSort(model, 0, model.getValues().length);
    }

    /**
     * Recursively sorts the given range of the given data model.
     *
     * @param model data model
     * @param start start of the range to sort
     * @param end end of the range to sort
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static void slowSort(final DataModel model, final int start, final int end)
            throws InterruptedException {
        if (start >= end) {
            return;
        }
        model.addArea(start, end);
        final int[] values = model.getValues();
        final int m = (start + end) / 2;
        model.setSpecialValue(values[m]);
        slowSort(model, start, m);
        slowSort(model, m + 1, end);
        model.pause();
        model.setSpecialValue(values[m]);
        if (values[end] < values[m]) {
            model.pause();
            model.swap(end, m);
        }
        model.setSpecialValue(-1);
        slowSort(model, start, end - 1);
        model.removeArea();
    }
}
