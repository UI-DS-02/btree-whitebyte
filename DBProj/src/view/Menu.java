package view;

import db.Field;
import db.Record;
import db.Table;

import java.util.ArrayList;
import java.util.HashMap;

public class Menu {
    public void help() {
        System.out.println(
                """
                        new tableLabel fieldLabel,fieldType\s
                        insert fieldLabel=fieldValue
                        search fieldLabel=fieldValue
                        searchBound fieldLabel=minFieldValue-maxFieldValue
                        delete fieldLabel=fieldValue
                        deleteBound fieldLabel=minFieldValue-maxFieldValue
                        update fieldLabel=fieldValue(update) fieldLabel=fieldValue
                        switch nameTable """);
    }
}
