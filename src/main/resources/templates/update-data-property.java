        if (_$PROPERTY_NAME$_new != null) {
            // получаем старые значения
            ArrayList<$PROPERTY_TYPE$> oldVals = get$PROPERTY_TYPE$InTriples($PROPERTY_NAME$_URI);
            Iterator<$PROPERTY_TYPE$> itrNew = _$PROPERTY_NAME$_new.iterator();
            while (itrNew.hasNext()) {
                $PROPERTY_TYPE$ curNew = itrNew.next();
                // ищем старое значение
                Iterator<$PROPERTY_TYPE$> itrOld = oldVals.iterator();
                while(itrOld.hasNext()) {
                    $PROPERTY_TYPE$ curOld = itrOld.next();
                    if (curNew.equals(curOld)) {
                        itrNew.remove();
                        itrOld.remove();
                        break;
                    }
                }
            }
            for($PROPERTY_TYPE$ val : _$PROPERTY_NAME$_new) {
                newTriples.add(createTriple(getID(), $PROPERTY_NAME$_URI, val.toString(), "uri", "literal"));
            }
            for($PROPERTY_TYPE$ val : oldVals){
                removeTriples.add(createTriple(getID(), $PROPERTY_NAME$_URI, val.toString(), "uri", "literal"));
            }
	    _$PROPERTY_NAME$_new = null;
        }
//-----------------------
