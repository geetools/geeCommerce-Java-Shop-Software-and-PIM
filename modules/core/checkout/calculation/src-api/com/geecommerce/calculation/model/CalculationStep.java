package com.geecommerce.calculation.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface CalculationStep extends Model {
    public Id getId();

    public CalculationStep setId(Id id);

    public Id getScriptletId();

    public CalculationStep setScriptletId(Id scriptletId);

    public CalculationScriptlet getScriptlet();

    public CalculationStep setScriptlet(CalculationScriptlet scriptlet);

    public Integer getSortOrder();

    public CalculationStep setSortOrder(Integer sortOrder);

    static final class Column {
        public static final String ID = "_id";
        public static final String SCRIPTLET_ID = "scriptlet_id";
        public static final String SORT_ORDER = "order";
    }
}
