package db;

import ds.ArrayBPTree;

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
                case "integer" -> {
                    ArrayBPTree<Integer, Record> it = new ArrayBPTree<>(3, 1, new Comparator<Integer>() {
                        @Override
                        public int compare(Integer o1, Integer o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    m.put(e.getKey(), it);
                }
                case "double" -> {
                    ArrayBPTree<Double, Record> dt = new ArrayBPTree<>(3, 1.0, new Comparator<Double>() {
                        @Override
                        public int compare(Double o1, Double o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    m.put(e.getKey(), dt);
                }
                case "string" -> {
                    ArrayBPTree<String, Record> st = new ArrayBPTree<>(3, "string", new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    m.put(e.getKey(), st);
                }
                case "boolean" -> {
                    ArrayBPTree<Boolean, Record> bt = new ArrayBPTree<>(3, false, new Comparator<Boolean>() {
                        @Override
                        public int compare(Boolean o1, Boolean o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    m.put(e.getKey(), bt);
                }
                default -> System.out.println("invalid type");
            }

        } return m;
    }
    public Record insertRec(Map<String, String> values) { // insert fieldLabel=fieldValue *
        ArrayList<Field<?>> fields = new ArrayList<>();
        for (Map.Entry<String, String> e: values.entrySet()) {
            switch (e.getKey()) {
                case "integer" -> {
                    Field<Integer> iff = new Field<>(e.getKey(), Integer.parseInt(e.getValue()), new Comparator<Integer>() {
                        @Override
                        public int compare(Integer o1, Integer o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    fields.add(iff);
                }
                case "double" -> {
                    Field<Double> df = new Field<>(e.getKey(), Double.parseDouble(e.getValue()), new Comparator<Double>() {
                        @Override
                        public int compare(Double o1, Double o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    fields.add(df);
                }
                case "string" -> {
                    Field<String> sf = new Field<>(e.getKey(), e.getValue(), new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    fields.add(sf);
                }
                case "boolean" -> {
                    Field<Boolean> bf = new Field<>(e.getKey(), Boolean.parseBoolean(e.getValue()), new Comparator<Boolean>() {
                        @Override
                        public int compare(Boolean o1, Boolean o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    fields.add(bf);
                }
                default -> {
                }
            }
        }
        return new Record(fields);
    }
    public void searchRec(Map<String, String> query) {
        Set<Record> res = new HashSet<>();
        boolean isFirst = true;
        for(Map.Entry<String, String> e: query.entrySet()) {
            ArrayBPTree<?, Record> bpt = m.get(e.getKey());
            if(bpt.getKey().getClass().getSimpleName().equals("Integer")){
                //bpt.search(Integer.parseInt(e.getValue()), Integer.parseInt(e.getValue()));
            } else if(bpt.getKey().getClass().getSimpleName().equals("Double")) {

            } else if(bpt.getKey().getClass().getSimpleName().equals("String")) {

            } else if(bpt.getKey().getClass().getSimpleName().equals("Boolean")) {

            }
        }
    }
}
