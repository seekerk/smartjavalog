package $PACKAGE_NAME$.base;

import java.util.ArrayList;
import java.util.List;

import sofia_kp.SIBResponse;

public abstract class SIBAsyncTask {
    private static String TAG = "SIBAsyncTask";

    protected KPIproxy proxy;

    protected Exception ex = null;

    protected SIBResponse response = null;

    protected List<TaskListener> listeners = new ArrayList<>();

    public SIBAsyncTask(KPIproxy proxy) {
        this.proxy = proxy;
    }

    public void addListener(TaskListener taskListener) {
        listeners.add(taskListener);

        if (ex != null)
            taskListener.onError(ex);

        if (response != null)
            taskListener.onSuccess(response);
    }

    public void execute() {
        doInBackground();
        postExecute();
    }

    protected abstract void doInBackground();

    protected void postExecute() {
        if (ex != null) {
            for (TaskListener listener : listeners) {
                listener.onError(ex);
            }
            return;
        }
        for (TaskListener listener : listeners) {
            listener.onSuccess(response);
        }
    }
}