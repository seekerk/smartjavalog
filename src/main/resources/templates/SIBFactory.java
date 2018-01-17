package $PACKAGE_NAME$;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sofia_kp.KPICore;

/**
 * The class to implement of single point for smart spaces
 */

public class SIBFactory {

    private static String defaultPointName = "default";

    /**
     * точка доступа к фабрике
     */
    private static SIBFactory instance = null;

    private Map<String, KPICore> accessPoints = new HashMap<>();

    private SIBFactory(){}

    public static SIBFactory getInstance() {
        if (instance == null)
            instance = new SIBFactory();

        return instance;
    }

    public KPICore getAccessPoint() {
        if (getAccessPoint(defaultPointName) == null)
                createDefaultAccessPoint();

        return getAccessPoint(defaultPointName);
    }

    public KPICore getAccessPoint(String pointName) {
        if (defaultPointName.equals(pointName) && accessPoints.get(pointName) == null)
            createDefaultAccessPoint();

        return accessPoints.get(pointName);
    }

    public String getDefaultAccessPointName() {
        return defaultPointName;
    }

    public KPICore createAccessPoint(String name, String host, int port) {
        KPICore ret = new KPICore(host, port, name);
        accessPoints.put(name, ret);

        return ret;
    }

    private void createDefaultAccessPoint() {
        createAccessPoint(defaultPointName, "localhost", 10010);
    }
}
