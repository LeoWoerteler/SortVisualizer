package kn.uni.dbis.cs.sorting.algo;

import kn.uni.dbis.cs.sorting.DataModel;
import kn.uni.dbis.cs.sorting.Sorter;

/**
 * The Selection Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class SelectionSort implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int n = model.getLength();
        model.addArea(0, n);
        for (int i = 0; i < n - 1; i++) {
            model.changeArea(0, i, n);
            int minPos = i;
            model.setSpecial(minPos);
            model.addArea(i, n);
            for (int j = i + 1; j < n; j++) {
                model.changeArea(0, j - 1, n);
                if (model.compare(j, minPos) < 0) {
                    model.setSpecial(j);
                    minPos = j;
                }
            }
            model.swap(i, minPos);
            model.setSpecial(-1);
            model.removeArea();
        }
        model.removeArea();
    }
}
