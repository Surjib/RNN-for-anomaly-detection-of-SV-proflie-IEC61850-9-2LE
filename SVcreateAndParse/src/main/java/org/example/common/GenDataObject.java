package org.example.common;

import java.util.ArrayList;
import java.util.List;

/** Абстрактный класс для создания объектов данных**/
public abstract class GenDataObject {

    private String name;

    private String reference;

    private GenDataObject parent;

    private final List<GenDataObject> children = new ArrayList<>();






    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public GenDataObject getParent() {
        return parent;
    }

    public void setParent(GenDataObject parent) {
        this.parent = parent;
    }

    public List<GenDataObject> getChildren() {
        return children;
    }
}
