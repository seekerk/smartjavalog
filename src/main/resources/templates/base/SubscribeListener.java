package org.fruct.oss.smartjavalog.base;

/**
 * Subscribe listener interface
 */

public interface SubscribeListener<T> {
    /**
     * Notification of the appearance of a new individe in smart space
     * @param item new individe
     */
    void addItem(T item);

    /**
     * Notification of an individe remove in smart space
     * @param item removed individe
     */
    void removeItem(T item);

    /**
     * Notification of an error executed
     * @param ex error description
     */
    void onError(Exception ex);
}
