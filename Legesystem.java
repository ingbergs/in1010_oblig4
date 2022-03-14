import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Legesystem {

    // Pasient      -> IndeksertListe
    private static IndeksertListe<Pasient> pasienter = new IndeksertListe<>();

    // Leger        -> Prioritetskoe
    private static Prioritetskoe<Lege> leger = new Prioritetskoe<>();

    // Legemidler   -> IndeksertListe
    private static IndeksertListe<Legemiddel> legemidler = new IndeksertListe<>();

    // Resepter     -> IndeksertListe
    private static IndeksertListe<Resept> resepter = new IndeksertListe<>();

    public static void main(String[] args) {

        // lesFraFil("legedata.txt");

        lesFraFil("nyStorFil.txt");


        //tester:
        // for (Pasient pasient : pasienter) {
        //     System.out.println(pasient);
        // }

        // for (Legemiddel legemiddel : legemidler) {
        //     System.out.println(legemiddel);
        // }

        // for (Lege lege : leger) {
        //     System.out.println(lege);
        // }

        // for (Resept resept : resepter) {
        //     System.out.println(resept);
        // }

        //TODO: mange sjekker for gyldig input && "Denne legen er spsialist" && "kr mangler i utskriften til BlaaResept"

    }
    
    public static void lesFraFil(String filnavn) {              // Fyller instansvariablene med info fra fil

        File fil = null;
        Scanner sc = null;

        try {

            fil = new File(filnavn); 
            sc = new Scanner(fil);
            
        } catch (FileNotFoundException e) {
            System.out.println("Error: fil ikke funnet");
            e.printStackTrace();
        }

        int flagg = 0;

        String linje = sc.nextLine();

        while (sc.hasNextLine()) {                              // Går gjennom filen linje for linje

            linje = sc.nextLine();

            if (linje.charAt(0) == '#') {
                flagg ++;
                linje = sc.nextLine();
            }

            if (flagg == 0) {                                   // Fyller pasientlista
                String[] deler = linje.strip().split(",");             
                Pasient nyPasient = new Pasient(deler[0],deler[1]);
                pasienter.leggTil(nyPasient);

            } else if (flagg == 1) {                            // Fyller legemiddellista

                String[] deler = linje.strip().split(",");

                String navn = deler[0];
                String type = deler[1];
                int pris = (int) Double.parseDouble(deler[2]);  // Caster fra String til double til int. Runder utelukkende ned for oeyeblikket
                double virkestoff = Double.parseDouble(deler[3].strip());

                if (type.equals("vanlig")) {
                    Vanlig nyttVanlig = new Vanlig(navn, pris, virkestoff);
                    legemidler.leggTil(nyttVanlig);

                } else if (type.equals("vanedannende")) {
                    int styrke = Integer.parseInt(deler[4].strip());
                    Vanedannende nyttVanedannende = new Vanedannende(navn, pris, virkestoff, styrke);
                    legemidler.leggTil(nyttVanedannende);

                } else if (type.equals("narkotisk")) {
                    int styrke = Integer.parseInt(deler[4].strip());
                    Narkotisk nyttNarkotisk = new Narkotisk(navn, pris, virkestoff, styrke);
                    legemidler.leggTil(nyttNarkotisk);

                } else {
                    // throw navneerror?
                    System.out.println("Navneerror: legemiddeltype " + type + " støttes ikke.");
                    continue;
                }

            } else if (flagg == 2) {                            // Fyller legelista

                String[] deler = linje.strip().split(",");

                if (Integer.parseInt(deler[1]) != 0) {

                    Spesialist nySpesialist = new Spesialist(deler[0], deler[1]);
                    leger.leggTil(nySpesialist);

                } else {

                    Lege nyLege = new Lege(deler[0]);
                    leger.leggTil(nyLege);

                }

            } else if (flagg == 3) {                            // Fyller reseptlista
                
                String[] deler = linje.strip().split(",");

                Legemiddel legemiddel = null;
                Pasient pasient = null;

                try {
                legemiddel = legemidler.hent(Integer.parseInt(deler[0]) - 1);    // - 1 for å gjøre om fra nummer til indeks
                
                } catch (UgyldigListeindeks e) {
                    System.out.println("Error: legemiddelindeksfeil. " + e);
                    continue;
                }

                try {
                pasient = pasienter.hent(Integer.parseInt(deler[2]) - 1);           // - 1 for å gjøre om fra nummer til indeks
                } catch (UgyldigListeindeks e) {
                    System.out.println("Error: pasientindeksfeil. " + e);
                    continue;
                }
                
                Lege lege = null;
                int reit = 0;

                if (deler.length > 4) {
                    reit = Integer.parseInt(deler[4]);
                }

                for (Lege l : leger) {
                    if (l.hentNavn().equals(deler[1])) {
                        lege = l;
                    }
                }

                if (lege == null) {
                    //throw navneerror?
                    System.out.println("Navneerror: legen " + deler[1] + " er ikke i systemet.");
                    continue;
                }

                if (deler[3].equals("hvit")) {

                    try {
                    
                        Resept nyResept = lege.skrivHvitResept(legemiddel, pasient, reit);
                        resepter.leggTil(nyResept);

                    } catch(UlovligUtskrift e) {
                        System.out.println("Error: " + e);
                    }

                } else if (deler[3].equals("blaa")) {

                    try {

                        Resept nyResept = lege.skrivBlaaResept(legemiddel, pasient, reit);
                        resepter.leggTil(nyResept);

                    } catch(UlovligUtskrift e) {
                        System.out.println("Error: " + e);
                    }
                    
                } else if (deler[3].equals("militaer")) {

                    try {

                        Resept nyResept = lege.skrivMilResept(legemiddel, pasient);
                        resepter.leggTil(nyResept);  

                    } catch(UlovligUtskrift e) {
                        System.out.println("Error: " + e);
                    }

                } else if (deler[3].equals("p")) {

                    try {

                        Resept nyResept = lege.skrivPResept(legemiddel, pasient, reit);
                        resepter.leggTil(nyResept);  
                                        
                    } catch(UlovligUtskrift e) {
                        System.out.println("Error: " + e);
                    }

                } else {
                    // throw navneerror?
                    System.out.println("Navneerror: resepttype " + deler[3] + " støttes ikke.");
                    continue;
                }
            }
        }    
        sc.close();
    }
}
