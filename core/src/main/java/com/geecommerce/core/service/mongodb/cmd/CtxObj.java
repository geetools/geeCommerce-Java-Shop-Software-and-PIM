package com.geecommerce.core.service.mongodb.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.DBObject;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;

public class CtxObj extends AbstractCommand {
    // TODO: @obj:{val:true}

    private static final String CMD_PREFIX = "{{";
    private static final String CMD_OPEN_BRACKET = "[";
    private static final String CMD_CLOSE_BRACKET = "]";

    @Override
    public boolean isOwner(String key, Object value) {
	if (value == null || !(value instanceof String))
	    return false;

	return ((String) value).startsWith(CMD_PREFIX);
    }

    @Override
    public void process(Class<? extends Model> modelClass, String originalKey, String columnName, Object value, DBObject query, QueryOptions queryOptions) {
	String val = (String) value;
	StringBuilder json = new StringBuilder(val).replace(0, 1, CMD_OPEN_BRACKET).replace(val.length() - 1, val.length(), CMD_CLOSE_BRACKET);

	ContextObject<?> ctxObj = ContextObject.fromJSON(json.toString());

	Map<String, Object> allPart = new HashMap<String, Object>();
	List<Map<String, Object>> allElemMatchParts = new ArrayList<>();

	Map<String, Object> valuePart = new HashMap<String, Object>();
	valuePart.put(ContextObject.VALUE, ctxObj.getVal());

	Map<String, Object> elemMatchPart = new HashMap<String, Object>();
	elemMatchPart.put("$elemMatch", valuePart);

	allElemMatchParts.add(elemMatchPart);

	allPart.put("$all", allElemMatchParts);

	query.put(columnName, allPart);

	System.out.println("CtxObj: " + query);
    }
}
