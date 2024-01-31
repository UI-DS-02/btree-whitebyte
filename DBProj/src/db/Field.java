package db;

import java.util.Comparator;

public class Field<F >{

    private String name;
    private F value;
    private Comparator<F> comp;

    public Field(String name, F value, Comparator<F> comp) {
        this.name = name;
        this.value = value;
        this.comp = comp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public F getValue() {
        return value;
    }

    public void setValue(F value) {
        this.value = value;
    }

    public Comparator<F> getComp() {
        return comp;
    }

    public void setComp(Comparator<F> comp) {
        this.comp = comp;
    }
}
