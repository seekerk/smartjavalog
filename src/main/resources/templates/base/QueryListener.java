package org.fruct.oss.smartjavalog.base;

/**
 * Created by kulakov on 26.01.18.
 */

public interface QueryListener {
    /**
     * Notification of the appearance of a new individe in smart space
     * @param item new individe
     */
    void addItem(BaseRDF item);

    /**
     * Notification of an error executed
     * @param ex error description
     */
    void onError(Exception ex);
}
