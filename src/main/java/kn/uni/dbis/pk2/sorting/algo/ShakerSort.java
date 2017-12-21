package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Shaker Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class ShakerSort implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int[] values = model.getValues();
        int start = 0;
        int end = values.length - 1;
        int dir = 1;
        model.addArea(start, end + 1);
        model.addArea(0, 0);
        while (start != end) {
            int last = start;
            int value = values[start];
            model.setSpecialValue(value);
            for (int i = start; i != end; i += dir) {
                model.changeArea(0, Math.min(i, end), Math.max(i, end) + 1);
                model.pause();
                final int next = values[i + dir];
                if (dir < 0 ? value < next : value > next) {
                    values[i + dir] = value;
                    values[i] = next;
                    last = i;
                } else {
                    values[i] = value;
                    value = next;
                    model.setSpecialValue(value);
                }
            }
            values[end] = value;
            end = start;
            start = last;
            dir *= -1;
            model.changeArea(1, Math.min(start, end), Math.max(start, end) + 1);
        }
        model.removeArea();
        model.removeArea();
        model.setSpecialValue(-1);
    }
}
