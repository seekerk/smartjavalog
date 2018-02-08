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

public int get$PROPERTY_NAME$$PROPERTY_TYPE$MinCardinality() {
    return $MIN_CARDINALITY$;
}

public int get$PROPERTY_NAME$$PROPERTY_TYPE$MaxCardinality() {
    return $MAX_CARDINALITY$;
}

public int get$PROPERTY_NAME$$PROPERTY_TYPE$ExactCardinality() {
    return $EXACT_CARDINALITY$;
}
