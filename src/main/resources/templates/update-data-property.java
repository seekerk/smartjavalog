        if (_$PROPERTY_NAME$_new != null) {
            // получаем старые значения
            ArrayList<String> oldVals = getInTriples($PROPERTY_NAME$_URI);
            Iterator<String> itrNew = _$PROPERTY_NAME$_new.iterator();
            while (itrNew.hasNext()) {
                String curNew = itrNew.next();
                // ищем старое значение
                Iterator<String> itrOld = oldVals.iterator();
                while(itrOld.hasNext()) {
                    String curOld = itrOld.next();
                    if (curNew.equals(curOld)) {
                        itrNew.remove();
                        itrOld.remove();
                        break;
                    }
                }
            }
            for(String val : _$PROPERTY_NAME$_new) {
                newTriples.add(createTriple(getID(), $PROPERTY_NAME$_URI, val, "uri", "literal"));
            }
            for(String val : oldVals){
                removeTriples.add(createTriple(getID(), $PROPERTY_NAME$_URI, val, "uri", "literal"));
            }
	    _$PROPERTY_NAME$_new = null;
        }
//-----------------------
