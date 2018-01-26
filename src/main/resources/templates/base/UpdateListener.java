package org.fruct.oss.smartjavalog.base;

/**
 * Created by kulakov on 26.01.18.
 */

public interface UpdateListener {
    void onUpdate();

    void onError(Exception ex);
}
