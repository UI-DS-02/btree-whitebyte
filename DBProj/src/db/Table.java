package db;

import ds.ArrayBPTree;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Table {
    public String label;
    public final Map<String, ArrayBPTree<?, Record>> m;

    public Table(String label, Map<String, String> fields) {
        this.label = label;
        this.m = insertCol(fields);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<String, ArrayBPTree<?, Record>> getM() {
        return m;
    }
    private Map<String, ArrayBPTree<?, Record>> insertCol(Map<String, String> fields) {

        Map<String, ArrayBPTree<?, Record>> m = new HashMap<>();

        for (Map.Entry<String, String> e: fields.entrySet()) {
            switch (e.getValue()) {
                case "Integer" -> {
                    ArrayBPTree<Integer, Record> it = new ArrayBPTree<>(3, 1, new Comparator<Integer>(){
                        @Override
                        public int compare(Integer o1, Integer o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    m.put(e.getKey(), it);
                }
                case "Double" -> {
                    ArrayBPTree<Double, Record> dt = new ArrayBPTree<>(3, 1.0, new Comparator<Double>() {
                        @Override
                        public int compare(Double o1, Double o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    m.put(e.getKey(), dt);
                }
                case "String" -> {
                    ArrayBPTree<String, Record> st = new ArrayBPTree<>(3, "string", new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    m.put(e.getKey(), st);
                }
                case "Boolean" -> {
                    ArrayBPTree<Boolean, Record> bt = new ArrayBPTree<>(3, false, new Comparator<Boolean>() {
                        @Override
                        public int compare(Boolean o1, Boolean o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    m.put(e.getKey(), bt);
                }
                case "Date" -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    ArrayBPTree<LocalDate, Record> dt = new ArrayBPTree<>(3,LocalDate.parse("13/02/2022", formatter), new Comparator<LocalDate>() {
                        @Override
                        public int compare(LocalDate o1, LocalDate o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    m.put(e.getKey(), dt);
                }
                default -> System.out.println("invalid type");
            }

        } return m;
    }
    public Record insertRec(Map<String, String> values) { // insert fieldLabel=fieldValue *
        ArrayList<Field<?>> fields = new ArrayList<>();
        for (Map.Entry<String, String> e: values.entrySet()) {
            switch (m.get(e.getKey()).getKey().getClass().getSimpleName()) {
                case "Integer" -> {
                    Field<Integer> iff = new Field<>(e.getKey(), Integer.parseInt(e.getValue()), new Comparator<Integer>() {
                        @Override
                        public int compare(Integer o1, Integer o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    fields.add(iff);
                }
                case "Double" -> {
                    Field<Double> df = new Field<>(e.getKey(), Double.parseDouble(e.getValue()), new Comparator<Double>() {
                        @Override
                        public int compare(Double o1, Double o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    fields.add(df);
                }
                case "String" -> {
                    Field<String> sf = new Field<>(e.getKey(), e.getValue(), new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    fields.add(sf);
                }
                case "Boolean" -> {
                    Field<Boolean> bf = new Field<>(e.getKey(), Boolean.parseBoolean(e.getValue()), new Comparator<Boolean>() {
                        @Override
                        public int compare(Boolean o1, Boolean o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    fields.add(bf);
                }
                case "LocalDate" -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    Field<LocalDate> bf = new Field<>(e.getKey(), LocalDate.parse(e.getValue(),formatter), new Comparator<LocalDate>() {
                        @Override
                        public int compare(LocalDate o1, LocalDate o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    fields.add(bf);
                }
                default -> {
                }
            }
        }
        Record rec = new Record(fields);
        for(Map.Entry<String, String> e: values.entrySet()) {
            switch (m.get(e.getKey()).getKey().getClass().getSimpleName()) {
                case "Integer" -> ((ArrayBPTree<Integer, Record>)m.get(e.getKey())).insert(Integer.parseInt(e.getValue()), rec);
                case "Double" -> ((ArrayBPTree<Double, Record>)m.get(e.getKey())).insert(Double.parseDouble(e.getValue()), rec);
                case "String" -> ((ArrayBPTree<String, Record>)m.get(e.getKey())).insert(e.getValue(), rec);
                case "Boolean" -> ((ArrayBPTree<Boolean, Record>)m.get(e.getKey())).insert(Boolean.parseBoolean(e.getValue()), rec);
                case "LocalDate" -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    ((ArrayBPTree<LocalDate, Record>)m.get(e.getKey())).insert(LocalDate.parse(e.getValue(),formatter), rec);
                }
            }
        }
        return rec;
    }
    public ArrayList<Record> searchRec(Map<String, String> query) {
        ArrayList<Record> res = new ArrayList<>();
        ArrayList<Record> fin = new ArrayList<>();
        boolean isFirst = true;
        for(Map.Entry<String, String> e: query.entrySet()) {
            ArrayBPTree<Object, Record> bpt =(ArrayBPTree<Object, Record>) m.get(e.getKey());
            switch (bpt.getKey().getClass().getSimpleName()) {
                case "Integer" -> res = bpt.search(Integer.parseInt(e.getValue()), Integer.parseInt(e.getValue()));
                case "Double" -> res = bpt.search(Double.parseDouble(e.getValue()), Double.parseDouble(e.getValue()));
                case "String" -> res = bpt.search(e.getValue(), e.getValue());
                case "Boolean" -> res = bpt.search(Boolean.parseBoolean(e.getValue()), Boolean.parseBoolean(e.getValue()));
                case "LocalDate" -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    res=bpt.search(LocalDate.parse(e.getValue(),formatter),LocalDate.parse(e.getValue(),formatter));
                }
            }
            if(!isFirst) {
                fin.retainAll(res);
            }else {
                fin=res;
            }
            isFirst=false;
        } return fin;
    }
    public ArrayList<Record> searchRecBound(Map<String, String> query) {
        ArrayList<Record> res = new ArrayList<>();
        ArrayList<Record> fin = new ArrayList<>();
        boolean isFirst = true;
        for(Map.Entry<String, String> e: query.entrySet()) {
            ArrayBPTree<Object, Record> bpt =(ArrayBPTree<Object, Record>) m.get(e.getKey());
            String[] range=e.getValue().split("-");
            switch (bpt.getKey().getClass().getSimpleName()) {
                case "Integer" -> res = bpt.search(Integer.parseInt(range[0]), Integer.parseInt(range[1]));
                case "Double" -> res = bpt.search(Double.parseDouble(range[0]), Double.parseDouble(range[1]));
                case "String" -> res = bpt.search(range[0], range[1]);
                case "Boolean" -> res = bpt.search(Boolean.parseBoolean(range[0]), Boolean.parseBoolean(range[1]));
                case "LocalDate" -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    res=bpt.search(LocalDate.parse(range[0],formatter),LocalDate.parse(range[1],formatter));
                }
            }
            if(!isFirst) {
                fin.retainAll(res);
            }else {
                fin=res;
            }
            isFirst=false;
        } return fin;
    }
    public ArrayList<Record> deleteRec(Map<String, String> query) {
        ArrayList<Record> res = searchRec(query);
        for (Record record :res){
            for (Field<?> field :record.getCells()){
                ArrayBPTree<Object, Record> bpt =(ArrayBPTree<Object, Record>) m.get(field.getName());
                bpt.delete(record,field.getValue(),field.getValue());
            }
        }
        return res;
    }
    public ArrayList<Record> deleteRecBound(Map<String, String> query) {
        ArrayList<Record> res = searchRecBound(query);
        for (Record record :res){
            for (Field<?> field :record.getCells()){
                ArrayBPTree<Object, Record> bpt =(ArrayBPTree<Object, Record>) m.get(field.getName());
                bpt.delete(record,field.getValue(),field.getValue());
            }
        }
        return res;
    }
    public void update(Map<String, String> query,String fieldName,String newValue) {
        ArrayList<Record> res = deleteRec(query);
        Set<Record> setRe=new HashSet<>();
        for (Record record :res){
            setRe.add(record);
        }
        for (Record record :setRe){
            for (Field<?> field :record.getCells()){
                if (field.getName().equals(fieldName)){
                    switch (field.getValue().getClass().getSimpleName()) {
                        case "Integer" -> {
                            ((Field<Integer>)field).setValue(Integer.parseInt(newValue));
                        }
                        case "Double" -> {
                            ((Field<Double>)field).setValue(Double.parseDouble(newValue));
                        }
                        case "String" -> {
                            ((Field<String>)field).setValue(newValue);
                        }
                        case "Boolean" -> {
                            ((Field<Boolean>)field).setValue(Boolean.parseBoolean(newValue));
                        }
                        case "LocalDate" -> {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            ((Field<LocalDate>)field).setValue(LocalDate.parse(newValue,formatter));
                        }
                    }
                }
            }
            for (Field<?> field :record.getCells()){
                switch (field.getValue().getClass().getSimpleName()) {
                    case "Integer" -> {
                        ((ArrayBPTree<Integer, Record>)m.get(field.getName())).insert((Integer) field.getValue(),record);
                    }
                    case "Double" -> {
                        ((ArrayBPTree<Double, Record>)m.get(field.getName())).insert((Double)field.getValue(),record);
                    }
                    case "String" -> {
                        ((ArrayBPTree<String, Record>)m.get(field.getName())).insert((String) field.getValue(), record);
                    }
                    case "Boolean" -> {
                        ((ArrayBPTree<Boolean, Record>)m.get(field.getName())).insert((Boolean) field.getValue(), record);
                    }
                    case "LocalDate" -> {
                        ((ArrayBPTree<LocalDate, Record>)m.get(field.getName())).insert((LocalDate) field.getValue(), record);
                    }
                }
            }
        }
    }
}
