package view;

import db.Field;
import db.Record;
import db.Table;
import exception.InvalidFormatException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class View {
    Scanner sc = new Scanner(System.in);
    ArrayList<Table> tables = new ArrayList<>();
    Table t;
    public void createNewTable(String cmd) throws InvalidFormatException {  // new tableLabel fieldLabel,fieldType
        try {
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
            tables.add(t);
            System.out.println("created!");
        }catch (IndexOutOfBoundsException e){
            throw new InvalidFormatException();
        }
    }
    public void switchTable(String cmd) throws InvalidFormatException {
        // new tableLabel fieldLabel,fieldType
        try {
            for (Table table :tables){
                if (table.getLabel().equals(cmd)) {
                    t=table;
                    break;
                }
            }
            System.out.println("switched!");
        }catch (IndexOutOfBoundsException e){
            throw new InvalidFormatException();
        }
    }
    public void searchRec(String cmd) throws InvalidFormatException { // insert fieldLabel=fieldValue
        try {
            String[] spl = cmd.split("\s");
            Map<String, String> m = new HashMap<>();
            for(String s: spl) {
                String[] spl1 = s.split("=");
                m.put(spl1[0], spl1[1]);
            }
            ArrayList<Record> qs = t.searchRec(m);
            printRec(qs);
        }catch (IndexOutOfBoundsException e){
            throw new InvalidFormatException();
        }
    }
    public void searchRecBound(String cmd) throws InvalidFormatException { // insert fieldLabel=fieldValue
        try {
            String[] spl = cmd.split("\s");
            Map<String, String> m = new HashMap<>();
            for(String s: spl) {
                String[] spl1 = s.split("=");
                m.put(spl1[0], spl1[1]);
            }
            ArrayList<Record> qs = t.searchRecBound(m);
            printRec(qs);
        }catch (IndexOutOfBoundsException e){
            throw new InvalidFormatException();
        }
    }
    public void insertNewRec(String cmd) throws InvalidFormatException { // search filedLabel=fieldValue
        try {
            String[] spl = cmd.split("\s");
            Map<String, String> m = new HashMap<>();
            for(String s: spl) {
                String[] spl1 = s.split("=");
                m.put(spl1[0], spl1[1]);
            } t.insertRec(m);
            System.out.println("inserted!");
        }catch (IndexOutOfBoundsException e){
            throw new InvalidFormatException();
        }
    }
    public void deleteRec(String cmd) throws InvalidFormatException { // search filedLabel=fieldValue
        try {
            String[] spl = cmd.split("\s");
            Map<String, String> m = new HashMap<>();
            for(String s: spl) {
                String[] spl1 = s.split("=");
                m.put(spl1[0], spl1[1]);
            } t.deleteRec(m);
            System.out.println("deleted!");
        }catch (IndexOutOfBoundsException e){
            throw new InvalidFormatException();
        }
    }
    public void deleteRecBound(String cmd) throws InvalidFormatException { // search filedLabel=fieldValue
        try {
            String[] spl = cmd.split("\s");
            Map<String, String> m = new HashMap<>();
            for(String s: spl) {
                String[] spl1 = s.split("=");
                m.put(spl1[0], spl1[1]);
            } t.deleteRecBound(m);
            System.out.println("deleted!");
        }catch (IndexOutOfBoundsException e){
            throw new InvalidFormatException();
        }
    }
    public void update(String cmd) throws InvalidFormatException { // search filedLabel=fieldValue
        try {
            String[] spl00 = cmd.split("\s",2);
            String[] spl0 = spl00[0].split("=");
            String[] spl = spl00[1].split("\s");
            Map<String, String> m = new HashMap<>();
            for(String s: spl) {
                String[] spl1 = s.split("=");
                m.put(spl1[0], spl1[1]);
            } t.update(m,spl0[0],spl0[1]);
            System.out.println("update!");
        }catch (IndexOutOfBoundsException e){
            throw new InvalidFormatException();
        }
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
