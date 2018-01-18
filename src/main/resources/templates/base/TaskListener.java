package $PACKAGE_NAME$.base;

import sofia_kp.SIBResponse;

public interface TaskListener {
    void onSuccess(SIBResponse response);
    void onError(Throwable ex);
}
