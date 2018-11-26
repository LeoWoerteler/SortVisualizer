package kn.uni.dbis.cs.sorting.algo;

import kn.uni.dbis.cs.sorting.DataModel;
import kn.uni.dbis.cs.sorting.Sorter;

/**
 * HeapSort implementation following the version in our KDI lecture more closely.
 */
public class HeapSortKDI implements Sorter {

    /**
     * Array slice.
     */
    private static class Slice {
        private final DataModel model;
        private final int start;
        private final int end;

        Slice(final DataModel model, final int start, final int end) {
            this.model = model;
            this.start = start;
            this.end = end;
        }

        int length() {
            return end - start;
        }
        void swap(final int i, final int j) throws InterruptedException {
            this.model.swap(start + i, start + j);
        }
        int compare(final int i, final int j) throws InterruptedException {
            return this.model.compare(start + i, start + j);
        }

        void addArea(final int start, final int end) {
            this.model.addArea(this.start + start, this.start + end);
        }


        void removeArea() {
            this.model.removeArea();
        }

        void setSpecial(final int i) {
            this.model.setSpecial(start + i);
        }

        void unsetSpecial() {
            this.model.setSpecial(-1);
        }
    }

    @Override
    public void sort(final DataModel model) throws InterruptedException {
        sort(model, 0, model.getLength());
    }

    /**
     * Sort a range in the data model using HeapSort.
     * @param model data values
     * @param start start of range to sort
     * @param end end of range to sort (exclusive)
     * @throws InterruptedException sorting thread interrupted
     */
    static void sort(final DataModel model, final int start, final int end) throws InterruptedException {
        final Slice m = new Slice(model, start, end);
        final int n = end - start;
        if (n <= 1) {
            return;
        }
        heapify(m);
        for (int k = n; --k > 0;) {
            m.addArea(0, k);
            m.setSpecial(k);
            m.swap(0, k);
            sink(m, 0, k);
            m.removeArea();
        }
        m.unsetSpecial();
    }

    private static void heapify(final Slice m) throws InterruptedException {
        final int n = m.length();
        final int lastParent = (n - 1) / 2;
        for (int s = lastParent; s >= 0; s--) {
            m.addArea(s, n);
            sink(m, s, n);
            m.removeArea();
        }
    }

    private static void sink(final Slice m, final int start, final int end) throws InterruptedException {
        int k = start;
        int child;
        while ((child = 2 * k + 1) < end) {
            if (child < end - 1 && m.compare(child + 1, child) > 0) {
                child += 1;
            }
            if (m.compare(k, child) > 0) {
                break;
            }
            m.addArea(k, end);
            m.setSpecial(k);
            m.swap(k, child);
            k = child;
            m.removeArea();
        }
        m.unsetSpecial();
    }
}
