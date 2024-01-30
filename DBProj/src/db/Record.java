package db;

import java.util.ArrayList;

public class Record {

    private final ArrayList<Field<?>> cells;

    public Record(ArrayList<Field<?>> c) {
        cells = new ArrayList<>();
    }

    public ArrayList<Field<?>> getCells() {
        return cells;
    }
}
