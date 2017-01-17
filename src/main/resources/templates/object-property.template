//------------ $PROPERTY_NAME$ --------------
    private static final String $PROPERTY_NAME$_URI = "$PROPERTY_URI$";
    private ArrayList<$PROPERTY_TYPE$> _$PROPERTY_NAME$_new = null;
    
    
    public ArrayList<$PROPERTY_TYPE$> $PROPERTY_NAME$() {
        if (_$PROPERTY_NAME$_new != null)
            return _$PROPERTY_NAME$_new;
        
        ArrayList<$PROPERTY_TYPE$> ret = new ArrayList();
        //search IDs in triples
        ArrayList<String> $PROPERTY_NAME$IDs = getStringInTriples($PROPERTY_NAME$_URI);
        for (String locID: $PROPERTY_NAME$IDs) {
            $PROPERTY_TYPE$ value = new $PROPERTY_TYPE$(_kp, locID);
            ret.add(value);
        }
        
        return ret;
    }
    
    public void $PROPERTY_NAME$($PROPERTY_TYPE$ loc) {
        $PROPERTY_NAME$(loc, true);
    }
    
    public void $PROPERTY_NAME$($PROPERTY_TYPE$ value, boolean removeOldValues) {
        if (_$PROPERTY_NAME$_new == null) {
            _$PROPERTY_NAME$_new = $PROPERTY_NAME$();
        }
        if (removeOldValues) {
            _$PROPERTY_NAME$_new.clear();
        }
        _$PROPERTY_NAME$_new.add(value);
    }
//============== $PROPERTY_NAME$ =============
