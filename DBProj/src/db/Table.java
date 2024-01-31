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
                    bpt.search(LocalDate.parse(e.getValue(),formatter),LocalDate.parse(e.getValue(),formatter));
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
    public void deleteRec(Map<String, String> query) {
        ArrayList<Record> res = searchRec(query);
        for (Record record :res){
            for (Field<?> field :record.getCells()){
                ArrayBPTree<Object, Record> bpt =(ArrayBPTree<Object, Record>) m.get(field.getName());
                bpt.delete(res,field.getValue());
            }
        }
    }
    public static void main(String[] args) {
        HashMap<String,String> b=new HashMap<>();
        b.put("asd","Integer");
        b.put("asf","Integer");
        Table table=new Table("asd",b);
        HashMap<String,String> c=new HashMap<>();
        c.put("asd","14");
        c.put("asf","5");
        table.insertRec(c);
        HashMap<String,String> f=new HashMap<>();
        f.put("asd","14");
        f.put("asf","6");
        table.insertRec(f);
        HashMap<String,String> g=new HashMap<>();
        g.put("asd","13");
        g.put("asf","7");
        table.insertRec(g);
        HashMap<String,String> h=new HashMap<>();
        h.put("asd","12");
        h.put("asf","8");
        table.insertRec(h);
        HashMap<String,String> i=new HashMap<>();
        i.put("asd","15");
        i.put("asf","9");
        table.insertRec(i);
        HashMap<String,String> j=new HashMap<>();
        j.put("asd","12");
        j.put("asf","10");
        table.insertRec(j);

        HashMap<String,String> d=new HashMap<>();
        d.put("asd","14");
        System.out.println(table.searchRec(d).size());
        table.deleteRec(f);

        System.out.println(0);
    }
}
