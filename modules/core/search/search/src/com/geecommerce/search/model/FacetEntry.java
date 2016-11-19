package com.geecommerce.search.model;

public class FacetEntry {
    private String id = null;
    private String label = null;
    private int count = 0;

    public FacetEntry(String id, String label, int count) {
	super();
	this.id = id;
	this.label = label;
	this.count = count;
    }

    public String getId() {
	return id;
    }

    public String getLabel() {
	return label;
    }

    public FacetEntry setLabel(String label) {
	this.label = label;
	return this;
    }

    public int getCount() {
	return count;
    }
}
