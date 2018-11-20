package kn.uni.dbis.cs.sorting.algo;

import kn.uni.dbis.cs.sorting.DataModel;
import kn.uni.dbis.cs.sorting.Sorter;

/**
 * The Shaker Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class ShakerSort implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        int start = 0;
        int end = model.getLength() - 1;
        int dir = 1;
        model.addArea(start, end + 1);
        model.addArea(0, 0);
        while (start != end) {
            int last = start;
            model.setSpecial(start);
            for (int pos = start; pos != end; pos += dir) {
                model.changeArea(0, Math.min(pos, end), Math.max(pos, end) + 1);
                final int nextPos = pos + dir;
                if (dir * model.compare(pos, nextPos) > 0) {
                    model.swap(pos, nextPos);
                    last = pos;
                } else {
                    model.setSpecial(nextPos);
                }
            }
            end = start;
            start = last;
            dir *= -1;
            model.changeArea(1, Math.min(start, end), Math.max(start, end) + 1);
        }
        model.removeArea();
        model.removeArea();
        model.setSpecial(-1);
    }
}
