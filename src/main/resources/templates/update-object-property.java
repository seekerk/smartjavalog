        if (_$PROPERTY_NAME$_new != null) {
            // получаем старые значения
            ArrayList<String> oldValsIDs = getInTriples($PROPERTY_NAME$_URI);
            Iterator<$PROPERTY_TYPE$> itrNew = _$PROPERTY_NAME$_new.iterator();
            while (itrNew.hasNext()) {
                $PROPERTY_TYPE$ curNew = itrNew.next();
                // ищем старое значение
                Iterator<String> itrOldID = oldValsIDs.iterator();
                while(itrOldID.hasNext()) {
                    String curOldID = itrOldID.next();
                    if (curNew.getID().equals(curOldID)) {
                        itrNew.remove();
                        itrOldID.remove();
                        break;
                    }
                }
            }
            _$PROPERTY_NAME$_new.stream().forEach(($PROPERTY_TYPE$ val) -> {
                newTriples.add(createTriple(getID(), $PROPERTY_NAME$_URI, val.getID(), "uri", "literal"));
            });
            oldValsIDs.stream().forEach((val) -> {
                removeTriples.add(createTriple(getID(), $PROPERTY_NAME$_URI, val, "uri", "literal"));
            });
	    _$PROPERTY_NAME$_new = null;
        }
//--------------------
