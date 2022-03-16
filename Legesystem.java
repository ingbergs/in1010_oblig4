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

        lesFraFil("legedatar.txt");

        // lesFraFil("nyStorFil.txt");


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

        for (Resept resept : resepter) {
            System.out.println(resept);
        }

        //TODO:"Denne legen er spsialist" && "kr" mangler i utskriften til BlaaResept

    }
    
    public static void lesFraFil(String filnavn) {                                  // Fyller instansvariablene med info fra fil

        File fil = null;
        Scanner sc = null;

        try {

            fil = new File(filnavn); 
            sc = new Scanner(fil);
            
        } catch (FileNotFoundException e) {
            System.out.println("Error: fil \"" + filnavn + "\" ikke funnet");
            return;
        }

        int flagg = 0;

        String linje = sc.nextLine();                                               // Hopper over foerste linje, siden vi vet at den ikke har data

        while (sc.hasNextLine()) {                                                  // Går gjennom filen linje for linje

            linje = sc.nextLine();

            if (linje.charAt(0) == '#') {
                flagg ++;
                linje = sc.nextLine();
            }

            String[] deler = linje.split(",");                                      // Deler opp linjen i biter

            for (int i = 0; i < deler.length; i++) {                                // Fjerner all whitespace før og etter hver bit av linjen
                deler[i] = deler[i].strip(); 
            }
            
            if (!sjekkGyldigFormat(deler, flagg)) {                                 // Sjekker om formatet på dataen er korrekt
                continue;
            }

            if (flagg == 0) {                                                       // Fyller pasientlista
                
                Pasient nyPasient = new Pasient(deler[0],deler[1]);
                pasienter.leggTil(nyPasient);

            } else if (flagg == 1) {                                                // Fyller legemiddellista

                String navn = deler[0];
                String type = deler[1];
            int pris = (int) Double.parseDouble(deler[2]);                          // Caster fra String til double til int. Runder utelukkende ned for oeyeblikket
                double virkestoff = Double.parseDouble(deler[3]);

                if (type.equals("vanlig")) {
                    Vanlig nyttVanlig = new Vanlig(navn, pris, virkestoff);
                    legemidler.leggTil(nyttVanlig);

                } else if (type.equals("vanedannende")) {
                    int styrke = Integer.parseInt(deler[4]);
                    Vanedannende nyttVanedannende = new Vanedannende(navn, pris, virkestoff, styrke);
                    legemidler.leggTil(nyttVanedannende);

                } else if (type.equals("narkotisk")) {
                    int styrke = Integer.parseInt(deler[4]);
                    Narkotisk nyttNarkotisk = new Narkotisk(navn, pris, virkestoff, styrke);
                    legemidler.leggTil(nyttNarkotisk);

                } else {
                    System.out.println("Navneerror: legemiddeltype " + type + " støttes ikke.");
                    continue;
                }

            } else if (flagg == 2) {                                                // Fyller legelista

                if (Integer.parseInt(deler[1]) != 0) {

                    Spesialist nySpesialist = new Spesialist(deler[0], deler[1]);
                    leger.leggTil(nySpesialist);

                } else {

                    Lege nyLege = new Lege(deler[0]);
                    leger.leggTil(nyLege);
                }

            } else if (flagg == 3) {                                                // Fyller reseptlista

                Legemiddel legemiddel = null;
                Pasient pasient = null;

                try {
                legemiddel = legemidler.hent(Integer.parseInt(deler[0]) - 1);       // - 1 for å gjøre om fra nummer til indeks
                
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
                    System.out.println("Navneerror: resepttype " + deler[3] + " støttes ikke.");
                    continue;
                }
            }
        }    
        sc.close();
    }

    private static boolean sjekkGyldigFormat(String[] deler, int flagg) {

        if (flagg == 0) {               // Sjekker pasientformat
            return (deler.length == 2);

        } else if (flagg == 1) {        // Sjekker legemiddelformat

            if (deler[1].equals("vanedannende") || deler[1].equals("narkotisk")) {

                if (deler.length == 5) {

                    try {
                        int test = (int) Double.parseDouble(deler[2]);
                        test = (int) Double.parseDouble(deler[3]);
                        test = (int) Double.parseDouble(deler[4]);

                    } catch (NumberFormatException e) {
                        System.out.println("Error: " + e);
                        return false;
                    }
                    
                    return true;

                } else {
                    return false;
                }

            } else if (deler.length == 4) {

                try {
                    int test = (int) Double.parseDouble(deler[2]);
                    test = (int) Double.parseDouble(deler[3]);

                } catch (NumberFormatException e) {
                    System.out.println("Error: " + e);
                    return false;
                }

                return true;

            } else {
                return false;
            }

        } else if (flagg == 2) {        // Sjekker legeformat
            
            if (deler.length == 2) {

                try {
                    int test = Integer.parseInt(deler[1]);

                } catch (NumberFormatException e) {
                    System.out.println("Error: " + e);
                    return false;
                }

                return true;    

            } else {
                return false;
            }

        } else if (flagg == 3) {        // Sjekker reseptformat

            if (deler[2].equals("militaer")) {

                if (deler.length == 4) {

                    try {
                        int test = Integer.parseInt(deler[0]);
                        test = Integer.parseInt(deler[2]);
    
                    } catch (NumberFormatException e) {
                        System.out.println("Error: " + e);
                        return false;
                    }
    
                    return true; 
                }
                
            } else if (deler.length == 5) {

                try {
                    int test = Integer.parseInt(deler[0]);
                    test = Integer.parseInt(deler[2]);
                    test = Integer.parseInt(deler[4]);

                } catch (NumberFormatException e) {
                    System.out.println("Error: " + e);
                    return false;
                }

                return true; 

            } else {
                return false;
            }
            
        }

        return false;
    }

    public static boolean sjekkOmNumerisk(String s) {
        for (int i = 0; i < s.length(); i++) {

            boolean punktum = false;

            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) {
                    return false;
                } else {
                    continue;
                }
            }

            if (i != 0 && s.charAt(i) == '.') {
                if (!punktum) {
                    punktum = true;
                } else {
                    return false;
                }
            }

            if (! Character.isDigit(s.charAt(i))) {
                return false;
            }

        }
        return true;
    }
}
