package de.sudoq.controller.menus;

import androidx.annotation.NonNull;

class StringAndEnum<E> {
    private String s;
    private E e;
    public StringAndEnum(String s, E e){
        this.s = s;
        this.e = e;
    }

    public String getString() {
        return s;
    }

    public E getEnum() {
        return e;
    }

    @NonNull
    @Override
    public String toString() {
        return s;
    }

}
