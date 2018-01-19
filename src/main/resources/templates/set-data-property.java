public void set$PROPERTY_NAME$($PROPERTY_TYPE$ value) {
    set$PROPERTY_NAME$(value, true);
}

public void set$PROPERTY_NAME$($PROPERTY_TYPE$ value, boolean removeOldValues) {
    if (_$PROPERTY_NAME$_new == null) {
        _$PROPERTY_NAME$_new = get$PROPERTY_NAME$();
    }
    if (removeOldValues) {
        _$PROPERTY_NAME$_new.clear();
    }
    _$PROPERTY_NAME$_new.add(value.toString());
}

