package kn.uni.dbis.pk2.sorting.algo;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Heap Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public final class HeapSort implements Sorter {

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        final int[] values = model.getValues();
        final int n = values.length;
        final int lastParent = (n - 1) / 2;
        for (int i = lastParent; i >= 0; i--) {
            model.addArea(i, n);
            siftDown(model, i, n);
            model.removeArea();
        }
        for (int i = n; --i > 0;) {
            model.addArea(0, i);
            model.setSpecialValue(values[i]);
            model.swap(i, 0);
            model.pause();
            siftDown(model, 0, i);
            model.removeArea();
        }
        model.setSpecialValue(-1);
    }

    /**
     * Moves a misplaced value down into the heap until it reaches its dedicated place.
     *
     * @param model data model
     * @param start start of the heap
     * @param end end of the heap
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static void siftDown(final DataModel model, final int start, final int end)
            throws InterruptedException {
        final int[] values = model.getValues();
        int pos = start;
        while (2 * pos < end - 1) {
            final int left = 2 * pos + 1;
            final int child = left + 1 == end || values[left] >= values[left + 1] ? left : left + 1;
            if (values[pos] >= values[child]) {
                break;
            }
            model.addArea(pos, end);
            model.setSpecialValue(values[pos]);
            model.swap(pos, child);
            model.pause();
            pos = child;
            model.removeArea();
        }
        model.setSpecialValue(-1);
    }
}
