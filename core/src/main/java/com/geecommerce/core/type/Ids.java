package com.geecommerce.core.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ids {
    /**
     * Filters list of Ids out of Id array.
     * 
     * @param idObjectsToFilter
     * @param allIds
     * @return newFilteredIdList
     */
    public static final <T extends IdSupport> Id[] filterIds(List<T> idObjectsToFilter, Id... targetIdArray) {
	List<Id> newFilteredIdList = new ArrayList<>();

	List<Id> idsToFilter = toIdList(idObjectsToFilter);

	for (Id id : targetIdArray) {
	    if (!idsToFilter.contains(id)) {
		newFilteredIdList.add(id);
	    }
	}

	return newFilteredIdList.toArray(new Id[newFilteredIdList.size()]);
    }

    /**
     * Converts arbitrary IdObjects to Id list.
     * 
     * @param idObjectsToFiler
     * @return ids
     */
    public static final <T extends IdSupport> List<Id> toIdList(List<T> idObjectsToFiler) {
	List<Id> ids = new ArrayList<>();

	if (idObjectsToFiler == null || idObjectsToFiler.size() == 0)
	    return ids;

	for (IdSupport idObject : idObjectsToFiler) {
	    ids.add(idObject.getId());
	}

	return ids;
    }

    public static final <T extends IdSupport> List<Id> toIdList(T[] idObjectsToFiler) {
	List<Id> ids = new ArrayList<>();

	if (idObjectsToFiler == null || idObjectsToFiler.length == 0)
	    return ids;

	for (IdSupport idObject : idObjectsToFiler) {
	    ids.add(idObject.getId());
	}

	return ids;
    }

    public static final <T extends IdSupport> Id[] toIds(List<T> idObjects) {
	List<Id> ids = new ArrayList<>();

	if (idObjects == null || idObjects.size() == 0)
	    return ids.toArray(new Id[0]);

	for (IdSupport idObject : idObjects) {
	    ids.add(idObject.getId());
	}

	return ids.toArray(new Id[ids.size()]);
    }

    public static final Id[] toIds(String[] stringIds) {
	List<Id> ids = new ArrayList<>();

	if (stringIds == null || stringIds.length == 0)
	    return ids.toArray(new Id[0]);

	for (String stringId : stringIds) {
	    ids.add(Id.valueOf(stringId));
	}

	return ids.toArray(new Id[ids.size()]);
    }

    public static final Id[] toIds(Number[] numberIds) {
	List<Id> ids = new ArrayList<>();

	if (numberIds == null || numberIds.length == 0)
	    return ids.toArray(new Id[0]);

	for (Number numId : numberIds) {
	    ids.add(Id.valueOf(numId));
	}

	return ids.toArray(new Id[ids.size()]);
    }

    /**
     * Creates a new map grouping objects by their id.
     * 
     * @param idObjects
     * @return groupedObjects
     */
    public static <T extends IdSupport> Map<Id, List<T>> toIdListMap(List<T> idObjects) {
	Map<Id, List<T>> returnMap = new HashMap<>();

	for (T t : idObjects) {
	    List<T> subList = returnMap.get(t.getId());

	    if (subList == null) {
		subList = new ArrayList<>();
		returnMap.put(t.getId(), subList);
	    }

	    subList.add(t);
	}

	return returnMap;
    }

    /**
     * Convert a list of objects to a map.
     * 
     * @param idObjects
     * @return map
     */
    public static <T extends IdSupport> Map<Id, T> toMap(List<T> idObjects) {
	Map<Id, T> returnMap = new HashMap<>();

	for (T t : idObjects) {
	    returnMap.put(t.getId(), t);
	}

	return returnMap;
    }
}
