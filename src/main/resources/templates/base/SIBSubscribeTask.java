package org.fruct.oss.smartjavalog.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kulakov on 23.01.18.
 */

public abstract class SIBSubscribeTask extends SIBAsyncTask {
    protected List<SubscribeListener> listeners = new ArrayList();

    public SIBSubscribeTask(KPIproxy proxy) {
        super(proxy);
    }

    public void addListener(SubscribeListener taskListener) {
        listeners.add(taskListener);

        if (ex != null)
            taskListener.onError(ex);

        //TODO: добавить отправку добавлений/изменений/удалений
    }

    @Override
    protected void onPostExecute() {
        if (ex != null) {
            for (SubscribeListener listener : listeners) {
                listener.onError(ex);
            }
            return;
        }
        //TODO: добавить отправку добавлений/изменений/удалений???
    }


}
