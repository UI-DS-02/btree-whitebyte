import exception.InvalidFormatException;
import view.Menu;
import view.View;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Menu m = new Menu();
        View vw = new View();
        String cmd;
        m.help();
        while (!(cmd = sc.nextLine()).equals("exit")) {
            try {
                String[] spl = cmd.split("\s", 2);
                switch (spl[0]) {
                    case "new" :
                        vw.createNewTable(spl[1]);
                        break;
                    case "insert" :
                        vw.insertNewRec(spl[1]);
                        break;
                    case "delete":
                        vw.deleteRec(spl[1]);
                        break;
                    case "deleteBound":
                        vw.deleteRecBound(spl[1]);
                        break;
                    case "search" :
                        vw.searchRec(spl[1]);
                        break;
                    case "searchBound" :
                        vw.searchRecBound(spl[1]);
                        break;
                    case "update" :
                        vw.update(spl[1]);
                        break;
                    case "switch" :
                        vw.switchTable(spl[1]);
                        break;
                    default: throw new InvalidFormatException();
                }
            }catch (Exception exception){
                System.out.println(exception.getMessage());
            }
        }
    }
}