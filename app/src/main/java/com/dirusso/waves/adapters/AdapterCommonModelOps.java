package com.dirusso.waves.adapters;

import java.util.List;

/**
 * Adapter common model operations, since if we are specific, the adapter itself handles
 * view and model operation in MVP architecture.
 *
 * @param <T>
 */
public interface AdapterCommonModelOps<T extends Object> {

    /**
     * Add a list of itmes to adapter
     *
     * @param items
     */
    void addAll(List<T> items);

    /**
     * Add a single item
     *
     * @param item
     */
    void add(T item);

    /**
     * Clear items list
     */
    void clear();

    /**
     * Remove a cetain item
     *
     * @param item
     */
    void remove(T item);

    /**
     * Refresh item list
     */
    void refresh();

    /**
     * Get single item in position
     *
     * @param position
     * @return
     */
    T getItem(int position);

    /**
     * Get items
     *
     * @return
     */
    List<T> getItems();

}
