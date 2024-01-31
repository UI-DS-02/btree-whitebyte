package db;

import java.util.ArrayList;

public class Record implements Comparable<Record> {

    private final ArrayList<Field<?>> cells;

    public Record(ArrayList<Field<?>> c) {
        cells = c;
    }

    public ArrayList<Field<?>> getCells() {
        return cells;
    }

    @Override
    public int compareTo(Record o) {
        boolean sim=true;
        for (Field<?> field :this.cells){
            for (Field<?> fieldO :o.getCells()){
                if (field.getName().equals(fieldO.getName())){
                    if (!field.getValue().equals(fieldO.getValue())){
                        sim=false;
                    }
                }
            }
        }
        if (sim)
            return 0;
        else return 1;
    }

    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder();
        for (Field<?> field: getCells()){
            stb.append(field.getValue());
            stb.append(" ");
        }
        return stb.toString();
    }
}
