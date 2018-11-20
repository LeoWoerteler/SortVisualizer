package kn.uni.dbis.cs.sorting;

/**
 * Sorting algorithm that also visualizes its progress.
 *
 * @author Leo Woerteler &lt;leonard.woerteler@uni-konstanz.de&gt;
 */
public interface Sorter {
    /**
     * Sorts the data in the given data model.
     *
     * @param model data model
     * @throws InterruptedException if the sorting thread was interrupted
     */
    void sort(DataModel model) throws InterruptedException;
}
