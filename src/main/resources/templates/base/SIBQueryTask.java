package org.fruct.oss.smartjavalog.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kulakov on 23.01.18.
 */

public abstract class SIBQueryTask extends SIBAsyncTask {

    protected List<TaskListener> listeners = new ArrayList<>();

    public SIBQueryTask(KPIproxy proxy) {
        super(proxy);
    }

    public void addListener(TaskListener taskListener) {
        listeners.add(taskListener);

        if (ex != null) {
            taskListener.onError(ex);
            return;
        }

        if (response != null)
            taskListener.onSuccess(response);
    }

    protected void onPostExecute() {
        if (ex != null) {
            for (TaskListener listener : listeners) {
                listener.onError(ex);
            }
            return;
        }
        if (response != null) {
            for (TaskListener listener : listeners) {
                listener.onSuccess(response);
            }
        }
    }


}
