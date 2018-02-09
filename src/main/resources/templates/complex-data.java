package $PACKAGE_NAME$;


/**
 $TYPE_DESCRIPTION$
 */
public class $TYPE_NAME$ {

    private static final String TYPE_URI = "$TYPE_URI$";

    private String value;

    <T> $TYPE_NAME$(T value) {
        check_type(value);
        this.value = value.toString();
    }

    public String getValue() {
        return value;
    }

    public static <T> $TYPE_NAME$ getInstance(T value, String accessPointName) {
        return new $TYPE_NAME$(value);
    }

    public static <T> $TYPE_NAME$ getInstance(T value) {
        return new $TYPE_NAME$(value);
    }

    public String getID() {
        return value;
    }

    private <T> void check_type(T value) {
        //TODO: check value type
    }
}