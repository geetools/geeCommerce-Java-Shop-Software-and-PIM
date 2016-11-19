package com.geecommerce.search.model;

import java.util.ArrayList;
import java.util.List;

public class Facet {
    private String code = null;
    private String label = null;
    private int totalCount = 0;
    private int position = 0;
    private List<FacetEntry> entries = new ArrayList<>();

    public Facet(String code, String label, int position) {
	this.code = code;
	this.label = label;
	this.position = position;
    }

    public String getCode() {
	return code;
    }

    public String getLabel() {
	return label;
    }

    public int getPosition() {
	return position;
    }

    public long getTotalCount() {
	if (totalCount == 0 && entries.size() > 0) {
	    for (FacetEntry entry : entries) {
		totalCount += entry.getCount();
	    }
	}

	return totalCount;
    }

    public Facet addEntry(String id, String label, int count) {
	entries.add(new FacetEntry(id, label, count));
	return this;
    }

    public List<FacetEntry> getEntries() {
	return entries;
    }
}
