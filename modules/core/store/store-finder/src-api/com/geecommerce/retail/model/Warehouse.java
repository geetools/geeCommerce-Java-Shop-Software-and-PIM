package com.geecommerce.retail.model;

import java.util.Set;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface Warehouse extends Model {

    Id getId();

    void setId(Id id);

    String getPlz();

    void setPlz(String plz);

    String getL1();

    void setL1(String l1);

    String getL2();

    void setL2(String l2);

    String getL3();

    void setL3(String l3);

    String getL4();

    void setL4(String l4);

    String getL5();

    void setL5(String l5);

    String getL6();

    void setL6(String l6);

    Set<String> getNumbers();

    class Column {
        public static final String ID = "_id";
        public static final String PLZ = "plz";
        public static final String L1 = "l1";
        public static final String L2 = "l2";
        public static final String L3 = "l3";
        public static final String L4 = "l4";
        public static final String L5 = "l5";
        public static final String L6 = "l6";
    }
}
