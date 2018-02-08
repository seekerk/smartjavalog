//------------ $PROPERTY_NAME$ --------------
private static final String $PROPERTY_NAME$_URI = "$PROPERTY_URI$";
private List<String> _$PROPERTY_NAME$_new = null;


public List<String> get$PROPERTY_NAME$() {
    if (_$PROPERTY_NAME$_new != null) {
        return _$PROPERTY_NAME$_new;
    }
    // search in triple store
    return getInTriples($PROPERTY_NAME$_URI);
}

public <T> void set$PROPERTY_NAME$(List<T> value) {
    set$PROPERTY_NAME$(value, true);
}

public <T> void set$PROPERTY_NAME$(List<T> value, boolean removeOldValues) {
    if (_$PROPERTY_NAME$_new == null) {
        _$PROPERTY_NAME$_new = get$PROPERTY_NAME$();
    }
    if (removeOldValues) {
        _$PROPERTY_NAME$_new.clear();
    }
    for (T item : value)
        _$PROPERTY_NAME$_new.add(item.toString());
}

$SET_DATA_PROPERTY$
//============== $PROPERTY_NAME$ =============
