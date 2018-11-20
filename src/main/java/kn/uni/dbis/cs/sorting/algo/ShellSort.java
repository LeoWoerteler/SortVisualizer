package kn.uni.dbis.cs.sorting.algo;

import kn.uni.dbis.cs.sorting.DataModel;
import kn.uni.dbis.cs.sorting.Sorter;

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
        final int n = model.getLength();
        for (final int gap : GAPS) {
            for (int i = gap; i < n; i++) {
                int k = 0;
                for (int j = i; j >= 0; j -= gap) {
                    model.addArea(j, j + 1);
                    k++;
                }
                model.setSpecial(i);
                for (int j = i; j >= gap && model.compare(j - gap, j) > 0; j -= gap) {
                    model.swap(j - gap, j);
                }
                for (int a = 0; a < k; a++) {
                    model.removeArea();
                }
            }
            model.setSpecial(-1);
        }
    }
}
