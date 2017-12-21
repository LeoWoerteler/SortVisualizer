package kn.uni.dbis.pk2.sorting.algo;
import java.util.BitSet;

import kn.uni.dbis.pk2.sorting.DataModel;
import kn.uni.dbis.pk2.sorting.Sorter;

/**
 * The Smooth Sort algorithm.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public class SmoothSort implements Sorter {

    /** Leonardo numbers that fit into an {@code int}. */
    private static final int[] L = new int[44];
    static {
        L[0] = 1;
        L[1] = 1;
        for (int i = 2; i < L.length; i++) {
            L[i] = L[i - 2] + L[i - 1] + 1;
        }
    }

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        if (model.getValues().length < 2) {
            return;
        }
        final BitSet sizes = buildHeaps(model);
        sort(model, sizes);
    }

    /**
     * Initial phase that builds a sequence of heaps on the unsorted data.
     *
     * @param model data model
     * @return heap sizes
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static BitSet buildHeaps(final DataModel model) throws InterruptedException {
        final BitSet sizes = new BitSet();
        sizes.set(1);
        model.addArea(0, 2);
        int minSize = 1;
        for (int curr = 1, n = model.getValues().length; curr < n; curr++) {
            if (sizes.get(minSize + 1)) {
                // build a heap from the two previous heaps and the current element
                sizes.clear(minSize, minSize + 2);
                model.removeArea();
                model.removeArea();
                minSize += 2;
            } else {
                // current element is singleton heap
                minSize = minSize == 1 ? 0 : 1;
            }
            sizes.set(minSize);
            model.addArea(0, curr + 1);

            // move new value into the correct heap
            restoreHeaps(model, sizes, minSize, curr);
        }
        return sizes;
    }

    /**
     * Sorts the data by successively extracting the maximal value from the sequence of heaps.
     *
     * @param model data model
     * @param sizes heap sizes
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static void sort(final DataModel model, final BitSet sizes) throws InterruptedException {
        int min = sizes.nextSetBit(0);
        for (int i = model.getValues().length; --i > 0;) {
            sizes.clear(min);
            model.removeArea();
            if (min < 2) {
                min = sizes.nextSetBit(min + 1);
                continue;
            }

            sizes.set(min - 1);
            model.addArea(0, i - L[min]);
            sizes.set(min - 2);
            model.addArea(0, i);
            min -= 2;

            restoreHeaps(model, sizes, min + 1, i - 1 - L[min]);
            restoreHeaps(model, sizes, min, i - 1);
        }
    }

    /**
     * Restores the heaps after the maximum was extracted.
     *
     * @param model data model
     * @param sizes heap sizes
     * @param initSizeL index of the initial heap's size
     * @param initPos initial position in the array
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static void restoreHeaps(final DataModel model, final BitSet sizes, final int initSizeL, final int initPos)
            throws InterruptedException {
        final int[] values = model.getValues();
        final int currVal = values[initPos];
        model.setSpecialValue(currVal);

        int pos = initPos;
        int sizeL = initSizeL;
        for (;;) {
            final int nextL = sizes.nextSetBit(sizeL + 1);
            if (nextL < 0) {
                // we are in the first heap
                break;
            }
            final int size = L[sizeL];
            final int nextHead = pos - size;
            final int nextVal = values[nextHead];
            final int rightHead = pos - 1;
            if (nextVal <= currVal
                    || size > 1 && (nextVal < values[rightHead] || nextVal < values[rightHead - L[sizeL - 2]])) {
                // we have found the correct heap
                break;
            }
            values[pos] = nextVal;
            pos = nextHead;
            sizeL = nextL;
            model.pause();
        }

        pos = siftDown(model, sizeL, pos, currVal);
        if (pos != initPos) {
            model.pause();
            values[pos] = currVal;
        }
        model.setSpecialValue(-1);
    }

    /**
     * Sifts a value down into a single heap.
     *
     * @param model data model
     * @param initL index of the heap's size
     * @param headPos position of the heap's head
     * @param currVal value to sift in
     * @return final position of the value
     * @throws InterruptedException if the sorting thread was interrupted
     */
    private static int siftDown(final DataModel model, final int initL, final int headPos, final int currVal)
            throws InterruptedException {
        // sink the current value into its heap
        final int[] values = model.getValues();
        int sizeL = initL;
        int pos = headPos;
        while (sizeL > 1) {
            final int rightHead = pos - 1;
            final int leftHead = rightHead - L[sizeL - 2];
            if (values[leftHead] <= values[rightHead]) {
                if (currVal >= values[rightHead]) {
                    break;
                }
                values[pos] = values[rightHead];
                sizeL -= 2;
                pos = rightHead;
            } else {
                if (currVal >= values[leftHead]) {
                    break;
                }
                values[pos] = values[leftHead];
                sizeL -= 1;
                pos = leftHead;
            }
            model.pause();
        }
        return pos;
    }
}
