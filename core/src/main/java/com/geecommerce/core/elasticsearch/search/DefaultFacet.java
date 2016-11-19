package com.geecommerce.core.elasticsearch.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.geecommerce.core.App;
import com.geecommerce.core.elasticsearch.api.search.Facet;
import com.geecommerce.core.elasticsearch.api.search.FacetEntry;
import com.geecommerce.core.service.annotation.Injectable;
import com.google.inject.Inject;

@Injectable
public class DefaultFacet implements Facet {
    private static final long serialVersionUID = -1137462924739136007L;
    protected String code = null;
    protected String label = null;
    protected long totalCount = 0;
    protected int position = 0;
    protected boolean isRangeFacet = false;

    protected List<FacetEntry> entries = new ArrayList<>();

    @Inject
    protected App app;

    @Override
    public Facet values(String code, String label, int position) {
        this.code = code;
        this.label = label;
        this.position = position;

        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public boolean isRange() {
        return isRangeFacet;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public long getTotalCount() {
        if (totalCount == 0 && entries.size() > 0) {
            for (FacetEntry entry : entries) {
                totalCount += entry.getCount();
            }
        }

        return totalCount;
    }

    @Override
    public long getEntryCount() {
        return entries == null ? 0 : entries.size();
    }

    @Override
    public DefaultFacet addEntry(String id, String label, long count, long nonMultiCount) {
        FacetEntry entry = app.getInjectable(FacetEntry.class).values(id, label, count, nonMultiCount);

        entries.add(entry);

        return this;
    }

    @Override
    public DefaultFacet addRangeEntry(String id, String label, Double rangeFrom, Double rangeTo, long count, long nonMultiCount) {
        FacetEntry entry = app.getInjectable(FacetEntry.class).values(id, label, rangeFrom, rangeTo, count, nonMultiCount);

        entries.add(entry);

        isRangeFacet = true;

        return this;
    }

    @Override
    public List<FacetEntry> getEntries() {
        return entries;
    }

    @Override
    public void sortEntries() {
        if (isNumberSortable() || isRangeFacet) {
            Collections.sort(entries, new Comparator<FacetEntry>() {
                @Override
                public int compare(FacetEntry e1, FacetEntry e2) {
                    double n1 = isRangeFacet ? (e1.getRangeFrom() == null ? 0 : e1.getRangeFrom()) : Double.parseDouble(e1.getLabel());
                    double n2 = isRangeFacet ? (e2.getRangeFrom() == null ? 0 : e2.getRangeFrom()) : Double.parseDouble(e2.getLabel());

                    return (n1 < n2 ? -1 : (n1 > n2 ? 1 : 0));
                }
            });
        } else {
            Collections.sort(entries);
        }
    }

    public boolean isNumberSortable() {
        boolean isNumberList = true;

        for (FacetEntry entry : entries) {
            if (!NumberUtils.isNumber(entry.getLabel())) {
                isNumberList = false;
                break;
            }
        }

        return isNumberList;
    }

    @Override
    public int compareTo(Facet facet) {
        return (getPosition() < facet.getPosition() ? -1 : (getPosition() > facet.getPosition() ? 1 : 0));
    }
}
