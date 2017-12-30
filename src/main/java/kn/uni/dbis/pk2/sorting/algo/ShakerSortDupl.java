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
        int start = 0;
        int end = model.getLength() - 1;
        model.addArea(0, 0);
        model.addArea(0, 0);
        model.addArea(0, 0);
        for (int dir = 1; start != end; dir *= -1) {
            int lastMod = start;
            int duplicates = 0;
            for (int curr = start; curr != end; curr += dir) {
                model.setSpecial(curr);
                final int cmp = dir * model.compare(curr, curr + dir);
                if (cmp > 0) {
                    model.swap(curr - duplicates, curr + dir);
                    lastMod = curr - duplicates;
                } else if (cmp == 0) {
                    duplicates += dir;
                } else {
                    duplicates = 0;
                }
                final int r = curr + dir;
                final int l = r - duplicates;
                model.changeArea(1, Math.min(l, end), Math.max(l, end) + 1);
                model.changeArea(0, Math.min(l, r), Math.max(l, r) + 1);
            }
            end = start;
            start = lastMod;
            model.changeArea(2, Math.min(start, end), Math.max(start, end) + 1);
        }
        model.removeArea();
        model.removeArea();
        model.removeArea();
        model.setSpecial(-1);
    }
}
