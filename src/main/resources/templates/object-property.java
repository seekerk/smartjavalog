//------------ $PROPERTY_NAME$ --------------
    private static final String $PROPERTY_NAME$_URI = "$PROPERTY_URI$";
    private ArrayList<$PROPERTY_TYPE$> _$PROPERTY_NAME$_new = null;
    
    
    public ArrayList<$PROPERTY_TYPE$> get$PROPERTY_NAME$() {
        if (_$PROPERTY_NAME$_new != null)
            return _$PROPERTY_NAME$_new;
        
        ArrayList<$PROPERTY_TYPE$> ret = new ArrayList<>();
        //search IDs in triples
        ArrayList<String> $PROPERTY_NAME$IDs = getInTriples($PROPERTY_NAME$_URI);
        for (String locID: $PROPERTY_NAME$IDs) {
            $PROPERTY_TYPE$ value = $PROPERTY_TYPE$.getInstance(locID, _accessPointName);
            ret.add(value);
        }
        
        return ret;
    }
    
    public void set$PROPERTY_NAME$($PROPERTY_TYPE$ loc) {
        set$PROPERTY_NAME$(loc, true);
    }
    
    public void set$PROPERTY_NAME$($PROPERTY_TYPE$ value, boolean removeOldValues) {
        if (_$PROPERTY_NAME$_new == null) {
            _$PROPERTY_NAME$_new = get$PROPERTY_NAME$();
        }
        if (removeOldValues) {
            _$PROPERTY_NAME$_new.clear();
        }
        _$PROPERTY_NAME$_new.add(value);
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

//============== $PROPERTY_NAME$ =============
