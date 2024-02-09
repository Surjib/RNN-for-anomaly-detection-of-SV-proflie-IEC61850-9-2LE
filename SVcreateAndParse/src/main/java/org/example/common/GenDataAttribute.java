package org.example.common;

/** Атрибут данных*/
public class GenDataAttribute<T> extends GenDataObject {

    private T value;

    public GenDataAttribute(T v) {

    }

    public GenDataAttribute() {
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
