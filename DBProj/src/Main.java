import view.Menu;
import view.View;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Menu m = new Menu();
        View vw = new View();
        String cmd;
        while (!(cmd = sc.nextLine()).equals("exit")) {
            String[] spl = cmd.split("\s", 2);
            switch (spl[0]) {
                case "new" :
                    vw.createNewTable(spl[1]);
                    break;
                case "insert" :
                    vw.insertNewRec(spl[1]);
                    break;
                case "delete":
                    //
                    break;
                case "search" :
                    vw.searchRec(spl[1]);
                    break;
                default:
            }
        }
    }
}