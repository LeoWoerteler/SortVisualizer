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
        for (int gap = 10 * n / 13; gap > 1; gap = 10 * gap / 13) {
            model.addArea(0, 0);
            model.addArea(0, 0);
            for (int i = gap; i < n; i++) {
                model.changeArea(0, i - gap, i - gap + 1);
                model.changeArea(1, i, i + 1);
                model.setSpecial(i);
                if (model.compare(i - gap, i) > 0) {
                    model.swap(i - gap, i);
                }
            }
            model.removeArea();
            model.removeArea();
            model.setSpecial(-1);
        }
        BubbleSort.sort(model, 0, n);
    }
}
