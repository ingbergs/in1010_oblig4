import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Legesystem {
    public static void main(String[] args) {
        lesFraFil("legedata.txt");
    }
    
    public static void lesFraFil(String filnavn) {

        // Pasient      -> IndeksertListe
        IndeksertListe<Pasient> pasienter = new IndeksertListe<>();

        // Leger        -> Prioritetskoe
        Prioritetskoe<Lege> leger = new Prioritetskoe<>();

        // Legemidler   -> IndeksertListe
        IndeksertListe<Legemiddel> legemidler = new IndeksertListe<>();

        // Resepter     -> IndeksertListe
        IndeksertListe<Resept> resepter = new IndeksertListe<>();


        try {
            File fil = new File(filnavn); 
            Scanner sc = new Scanner(fil);
            int flagg = 0;

            String linje = sc.nextLine();

            while (sc.hasNextLine()) {                             // Går gjennom filen linje for linje

                linje = sc.nextLine();

                if (linje.charAt(0) == '#') {
                    flagg ++;
                    linje = sc.nextLine();
                }

                if (flagg == 0) {                                  // Fyller pasientlista
                    String[] deler = linje.split(",");             
                    Pasient nyPasient = new Pasient(deler[0],deler[1]);
                    pasienter.leggTil(nyPasient);

                } else if (flagg == 1) {                           // Fyller legemiddellista

                    String[] deler = linje.split(",");

                    String navn = deler[0];
                    String type = deler[1];
                    int pris = Integer.parseInt(deler[2]);
                    double virkestoff = Double.parseDouble(deler[3]);

                    if (navn == "vanlig") {
                        Vanlig nyttVanlig = new Vanlig(navn, pris, virkestoff);
                        legemidler.leggTil(nyttVanlig);

                    } else if (navn == "vanedannende") {
                        int styrke = Integer.parseInt(deler[4]);
                        Vanedannende nyttVanedannende = new Vanedannende(navn, pris, virkestoff, styrke);
                        legemidler.leggTil(nyttVanedannende);

                    } else if (navn == "narkotisk") {
                        int styrke = Integer.parseInt(deler[4]);
                        Narkotisk nyttNarkotisk = new Narkotisk(navn, pris, virkestoff, styrke);
                        legemidler.leggTil(nyttNarkotisk);

                    } else {
                        // Throw navneerror?
                    }

                } else if (flagg == 3) {                            // Fyller legelista

                    String[] deler = linje.split(",");

                    if (deler[1] != "0") {
                        Spesialist nySpesialist = new Spesialist(deler[0], deler[1]);
                        leger.leggTil(nySpesialist);
                    } else {
                        Lege nyLege = new Lege(deler[0]);
                        leger.leggTil(nyLege);
                    }
                } else if (flagg == 4) {                            // Fyller reseptlista

                }

                System.out.println(linje);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error: fil ikke funnet");
            e.printStackTrace();
        }    
    }
}