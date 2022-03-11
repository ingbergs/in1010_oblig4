import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Legesystem {
    public static void main(String[] args) {
        
    }
    
    public void lesFraFil(String filnavn) {
        // Leger        -> Prioritetskoe
        // Legemidler   -> IndeksertListe
        // Resepter     -> IndeksertListe
        // Person       -> IndeksertListe
        // test

        try {
            File fil = new File(filnavn);
            Scanner sc = new Scanner(fil);

        } catch (FileNotFoundException e) {
            System.out.println("Error: fil ikke funnet");
            e.printStackTrace();
        }
    }
}