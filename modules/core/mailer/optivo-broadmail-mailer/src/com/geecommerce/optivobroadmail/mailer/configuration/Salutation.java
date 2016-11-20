package com.geecommerce.optivobroadmail.mailer.configuration;

import com.geecommerce.core.App;

public enum Salutation {
    SALUTATION_MR(0, App.get().message("Mr", "Herr", "de")), SALUTATION_MRS(1, App.get().message("Mrs", "Frau", "de"));

    private Integer id;

    private String salutation;

    private Salutation(Integer id, String salutation) {
        this.id = id;
        this.salutation = salutation;
    }

    public final Integer getId() {
        return this.id;
    }

    public final String getSalutationName() {
        return this.salutation;
    }

    public final String getLabel() {
        return App.get().message(new StringBuilder("enum.").append(Salutation.class.getSimpleName()).append(".")
            .append(name()).toString());
    }

    public static final String toHumanReadableLabel() {
        return App.get().message(
            new StringBuilder("enum.").append(Salutation.class.getSimpleName()).append(".label").toString());
    }

    public static Salutation getSalutation(Integer id) {
        for (Salutation type : values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Salutation with id " + id + " is not defined.");
    }

    public static Salutation getSalutation(String salutation) {
        for (Salutation type : values()) {
            if (type.name().equals("SALUTATION_" + salutation.toUpperCase())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Salutation with name " + salutation + " is not defined.");
    }

}
