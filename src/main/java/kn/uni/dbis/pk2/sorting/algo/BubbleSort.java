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
        final int[] values = model.getValues();
        for (int n = model.getLength(), last = -1; n > 1; n = last) {
            model.addArea(0, n);
            for (int i = 1; i < n; i++) {
                model.setSpecialValue(values[i - 1]);
                if (values[i - 1] > values[i]) {
                    last = i;
                    model.swap(i - 1, i);
                }
                model.changeArea(0, i, n);
                model.pause();
            }
            model.setSpecialValue(-1);
            model.removeArea();
        }
    }
}
