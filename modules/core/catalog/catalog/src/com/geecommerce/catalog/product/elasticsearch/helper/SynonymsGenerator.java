package com.geecommerce.catalog.product.elasticsearch.helper;

import com.mongodb.*;
import com.geecommerce.core.app.standalone.helper.MongoHelper;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class SynonymsGenerator {

    public static final String COLLECTION_SYNONYMS = "search_synonyms";
    public static final String FIELD_KEY_WORLD = "word";
    public static final String FIELD_KEY_SYNONYMS = "synonyms";
    public static final String FIELD_KEY_CUSTOM = "custom";
    public static final String DELIMITER = " => ";

    public void generateFile(String filename) {
	DB db = MongoHelper.mongoMerchantDB();

	DBCollection col = db.getCollection(COLLECTION_SYNONYMS);
	DBCursor cursor = col.find();
	try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)))) {
	    while (cursor.hasNext()) {
		DBObject doc = cursor.next();
		String word = doc.get(FIELD_KEY_WORLD) != null ? doc.get(FIELD_KEY_WORLD).toString() : null;
		String synonyms = doc.get(FIELD_KEY_WORLD) != null ? StringUtils.join((BasicDBList) doc.get(FIELD_KEY_SYNONYMS), ",") : null;
		if (word != null && synonyms != null) {
		    out.println(createLine(word, synonyms));
		}
	    }
	} catch (IOException e) {
	    throw new RuntimeException("IOException: Current db cursor with id: " + cursor.curr().get("_id"), e);
	}
    }

    private String createLine(String word, String synonyms) {
	return word + DELIMITER + synonyms;
    }

    public List<String> generateInlineSynonymConfiguration(SynonymsHelper.SynonymsConfiguration synonymsConfiguration) {
	List<Map<String, Object>> synonyms = MongoHelper.find(MongoHelper.mongoMerchantDB(), COLLECTION_SYNONYMS, Collections.emptyMap(), null);
	List<String> result = new LinkedList<>();
	if (synonymsConfiguration.getOnlyCustom()) {
	    synonyms.stream().filter(map -> (Boolean) map.get(FIELD_KEY_CUSTOM)).forEach(map -> result.add(createLine(map.get(FIELD_KEY_WORLD).toString(), StringUtils.join((BasicDBList) map.get(FIELD_KEY_SYNONYMS), ","))));
	} else
	    synonyms.stream().forEach(map -> result.add(createLine(map.get(FIELD_KEY_WORLD).toString(), StringUtils.join((BasicDBList) map.get(FIELD_KEY_SYNONYMS), ","))));
	return result;
    }
}
