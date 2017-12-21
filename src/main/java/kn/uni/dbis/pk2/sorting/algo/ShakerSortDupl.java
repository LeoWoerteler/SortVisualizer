package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Shaker Sort algorithm, moving more than one (equal) value.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class ShakerSortDupl implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int[] values = model.getValues();
        int start = 0;
        int end = values.length - 1;
        int dir = 1;
        model.addArea(0, 0);
        model.addArea(0, 0);
        model.addArea(0, 0);
        while (start != end) {
            int last = start;
            int value = values[start];
            model.setSpecialValue(value);
            int i = start;
            int j = start + dir;
            while (j - dir != end) {
                model.pause();
                final int next = values[j];
                if (dir < 0 ? value < next : value > next) {
                    values[j] = value;
                    values[i] = next;
                    last = i;
                } else if (value == next) {
                    i -= dir;
                } else {
                    values[i] = value;
                    value = next;
                    model.setSpecialValue(value);
                    i = j - dir;
                }
                i += dir;
                j += dir;
                model.changeArea(1, Math.min(i, end), Math.max(i, end) + 1);
                model.changeArea(0, Math.min(i, j - dir), Math.max(i, j - dir) + 1);
            }
            values[end] = value;
            end = start;
            start = last;
            dir *= -1;
            model.changeArea(2, Math.min(start, end), Math.max(start, end) + 1);
        }
        model.removeArea();
        model.removeArea();
        model.removeArea();
        model.setSpecialValue(-1);
    }
}
