package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Selection Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class SelectionSort implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int[] values = model.getValues();
        final int n = values.length;
        model.addArea(0, n);
        for (int i = 0; i < n - 1; i++) {
            model.pause();
            model.changeArea(0, i, n);
            int minPos = i;
            model.setSpecialValue(values[minPos]);
            model.addArea(i, n);
            for (int j = i + 1; j < n; j++) {
                model.changeArea(0, j - 1, n);
                if (values[j] < values[minPos]) {
                    model.setSpecialValue(values[j]);
                    minPos = j;
                }
                model.pause();
            }
            model.swap(i, minPos);
            model.setSpecialValue(-1);
            model.removeArea();
        }
        model.removeArea();
    }
}
