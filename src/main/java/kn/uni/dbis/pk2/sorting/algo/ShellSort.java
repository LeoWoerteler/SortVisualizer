package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Shell Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public final class ShellSort implements Sorter {

    /** Sequence of gaps to use. */
    private static final int[] GAPS = { 701, 301, 132, 57, 23, 10, 4, 1 };

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int[] values = model.getValues();
        final int n = values.length;
        for (final int gap : GAPS) {
            for (int i = gap; i < n; i++) {
                int k = 0;
                for (int j = i; j >= 0; j -= gap) {
                    model.addArea(j, j + 1);
                    k++;
                }
                final int temp = values[i];
                model.setSpecialValue(temp);
                int j = i;
                while (j >= gap && values[j - gap] > temp) {
                    model.pause();
                    values[j] = values[j - gap];
                    j -= gap;
                }
                model.pause();
                values[j] = temp;
                for (int a = 0; a < k; a++) {
                    model.removeArea();
                }
            }
            model.setSpecialValue(-1);
        }
    }
}
