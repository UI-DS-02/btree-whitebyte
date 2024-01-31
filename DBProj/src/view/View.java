package view;

import db.Field;
import db.Record;
import db.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class View {
    Scanner sc = new Scanner(System.in);
    Table t;
    public void createNewTable(String cmd) {  // new tableLabel fieldLabel,fieldType
        String[] spl = cmd.split("\s");
        Map<String, String> m = new HashMap<>();
        boolean first=true;
        for(String s: spl) {
            if (!first){
                String[] spl1 = s.split(",");
                m.put(spl1[0], spl1[1]);
            }
            first=false;
        } t = new Table(spl[0], m);
        System.out.println("created!");
    }
    public void searchRec(String cmd) { // insert fieldLabel=fieldValue
        String[] spl = cmd.split("\s");
        Map<String, String> m = new HashMap<>();
        for(String s: spl) {
            String[] spl1 = s.split("=");
            m.put(spl1[0], spl1[1]);
        }
        ArrayList<Record> qs = t.searchRec(m);
        printRec(qs);
    }
    public void searchRecBound(String cmd) { // insert fieldLabel=fieldValue
        String[] spl = cmd.split("\s");
        Map<String, String> m = new HashMap<>();
        for(String s: spl) {
            String[] spl1 = s.split("=");
            m.put(spl1[0], spl1[1]);
        }
        ArrayList<Record> qs = t.searchRecBound(m);
        printRec(qs);
    }
    public void insertNewRec(String cmd) { // search filedLabel=fieldValue
        String[] spl = cmd.split("\s");
        Map<String, String> m = new HashMap<>();
        for(String s: spl) {
            String[] spl1 = s.split("=");
            m.put(spl1[0], spl1[1]);
        } t.insertRec(m);
        System.out.println("inserted!");
    }
    public void deleteRec(String cmd) { // search filedLabel=fieldValue
        String[] spl = cmd.split("\s");
        Map<String, String> m = new HashMap<>();
        for(String s: spl) {
            String[] spl1 = s.split("=");
            m.put(spl1[0], spl1[1]);
        } t.deleteRec(m);
        System.out.println("deleted!");
    }
    public void deleteRecBound(String cmd) { // search filedLabel=fieldValue
        String[] spl = cmd.split("\s");
        Map<String, String> m = new HashMap<>();
        for(String s: spl) {
            String[] spl1 = s.split("=");
            m.put(spl1[0], spl1[1]);
        } t.deleteRecBound(m);
        System.out.println("deleted!");
    }
    public void update(String cmd) { // search filedLabel=fieldValue
        String[] spl00 = cmd.split("\s",2);
        String[] spl0 = spl00[0].split("=");
        String[] spl = spl00[1].split("\s");
        Map<String, String> m = new HashMap<>();
        for(String s: spl) {
            String[] spl1 = s.split("=");
            m.put(spl1[0], spl1[1]);
        } t.update(m,spl0[0],spl0[1]);
        System.out.println("update!");
    }
    public void printRec(ArrayList<Record> rec) {
        for(Record r: rec) {
            for (Field<?> f: r.getCells()) {
                System.out.println(f.getName() + ": " + f.getValue());
            }
            System.out.println();
        }
    }
}
