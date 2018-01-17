package $PACKAGE_NAME$.base;

public interface TaskListener {
    void onSuccess();
    void onError(Throwable ex);
}
