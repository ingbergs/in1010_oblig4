import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Legesystem {
    public static void main(String[] args) {
        lesFraFil("legedata.txt");
    }
    
    public static void lesFraFil(String filnavn) {
        // Leger        -> Prioritetskoe
        // Legemidler   -> IndeksertListe
        // Resepter     -> IndeksertListe
        // Person       -> IndeksertListe

        try {
            File fil = new File(filnavn); 
            Scanner sc = new Scanner(fil);
            int flagg = 0;

            String linje = sc.nextLine();

            while (sc.hasNextLine()) {                          // Går gjennom filen linje for linje

                linje = sc.nextLine();

                if (linje.charAt(0) == '#') {
                    flagg ++;
                    linje = sc.nextLine();
                }
                
                System.out.println(linje);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error: fil ikke funnet");
            e.printStackTrace();
        }    
    }
}