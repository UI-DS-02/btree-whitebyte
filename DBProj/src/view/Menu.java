package view;

public class Menu {
    public void help() {
        System.out.println(
                """
                        new tableLabel fieldLabel,fieldType\s
                        insert fieldLabel=fieldValue
                        search fieldLabel=fieldValue
                        delete fieldLabel=fieldValue""");
    }
}
