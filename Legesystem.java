import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.InputMismatchException;

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

        lesFraFil("legedata.txt");
        // lesFraFil("nyStorFil.txt");

        meny();

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

        //TODO: legge til relevante resepter i listene til egene og pasientene
    }
    

    // Oppgave E1
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

        String linje = sc.nextLine();   //TO DO: hvorfor har ikke nyStorFil ikke forste linje                                            // Hopper over foerste linje, siden vi vet at den ikke har data

                                                 
        while (sc.hasNextLine()) {                                                  // Gaar gjennom filen linje for linje

            linje = sc.nextLine();

            if (linje.charAt(0) == '#') {
                flagg ++;
                linje = sc.nextLine();
            }

            String[] deler = linje.split(",");                                      // Deler opp linjen i biter

            for (int i = 0; i < deler.length; i++) {                                // Fjerner all whitespace foer og etter hver bit av linjen
                deler[i] = deler[i].strip(); 
            }
            
            if (!sjekkGyldigFormat(deler, flagg)) {                                 // Sjekker om formatet paa dataen er korrekt
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
                    System.out.println("Navneerror: legemiddeltype " + type + " stoettes ikke.");
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
                legemiddel = legemidler.hent(Integer.parseInt(deler[0]) - 1);       // - 1 for aa gjoere om fra nummer til indeks
                
                } catch (UgyldigListeindeks e) {
                    System.out.println("Error: legemiddelindeksfeil. " + e);
                    continue;
                }

                try {
                pasient = pasienter.hent(Integer.parseInt(deler[2]) - 1);           // - 1 for aa gjore om fra nummer til indeks
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
                        pasient.nyResept(nyResept);

                    } catch(UlovligUtskrift e) {
                        System.out.println("Error: " + e);
                    }

                } else if (deler[3].equals("blaa")) {

                    try {

                        Resept nyResept = lege.skrivBlaaResept(legemiddel, pasient, reit);
                        resepter.leggTil(nyResept);
                        pasient.nyResept(nyResept);


                    } catch(UlovligUtskrift e) {
                        System.out.println("Error: " + e);
                    }
                    
                } else if (deler[3].equals("militaer")) {

                    try {

                        Resept nyResept = lege.skrivMilResept(legemiddel, pasient);
                        resepter.leggTil(nyResept);  
                        pasient.nyResept(nyResept);

                    } catch(UlovligUtskrift e) {
                        System.out.println("Error: " + e);
                    }

                } else if (deler[3].equals("p")) {

                    try {

                        Resept nyResept = lege.skrivPResept(legemiddel, pasient, reit);
                        resepter.leggTil(nyResept);  
                        pasient.nyResept(nyResept);
                                       
                    } catch(UlovligUtskrift e) {
                        System.out.println("Error: " + e);
                    }

                } else {
                    System.out.println("Navneerror: resepttype " + deler[3] + " stoettes ikke.");
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
    //Del E2
    public static void meny() {
        String valg = "";
        String s = "\n___LEGESYSTEM___"
        + "\nHva vil du gjore?"
        + "\n1: Skriv ut fullstendig oversikt"
        + "\n2: Legg til nytt element"
        + "\n3: Bruk resept"
        + "\n4: Skriv ut statistikk"
        + "\n5: Skriv til fil"
        + "\n6: Avslutt";
        Scanner sc = new Scanner(System.in);

        
        while (!valg.equals("6")) {
            System.out.println(s);
            
            valg = sc.next();
            if (valg.equals("1")) {
                skrivOversikt();
            }
            else if (valg.equals("2")) { 
                nyttElement();
            }
            else if (valg.equals("3")) {
                brukResept();
            }
            else if (valg.equals("4")) {
                skrivStatistikk();
            }
            else if (valg.equals("5")) {
                skrivTilFil();
            }
            else if (!valg.equals("6")) {
                System.out.println("Vennligst velg et tall 1-6.\n");
            }
        }
    }  
    
    public static void skrivOversikt() {
        System.out.println("__ALLE ELEMENTER I SYSTEMET__");
        System.out.println("\n__Leger__");
        for (Lege lege : leger) {
            System.out.println(lege);
        }
        
        System.out.println("\n__Pasienter__");
        for (Pasient pasient : pasienter) {
             System.out.println(pasient);
        }

        System.out.println("\n__Legemidler__");
        for (Legemiddel legemiddel : legemidler) {
            System.out.println(legemiddel);
        } 
        System.out.println();
    }

    public static void nyttElement() {
        String s = "\n___LEGG TIL ELEMENT___"
        + "\nHva vil du legge til?"
        + "\n1: Lege"
        + "\n2: Pasient"
        + "\n3: Resept"
        + "\n4: Legemiddel"
        + "\n5: Tilbake";
        
        Scanner sc = new Scanner(System.in);
        String valg = "";
        System.out.println(s);
        while (!valg.equals("5")) {
            
            if (valg.equals("1")) {
                leggTilLege();
            } else if (valg.equals("2")) { 
                leggTilPasient();
            } else if (valg.equals("3")) {
                leggTilResept();   
            } else if (valg.equals("4")) {
                leggTilLegemiddel();
            } else if (valg.equals("5")) { //Tilbake
                return;
            } else if (valg != ""){
                System.out.println("Vennligst velg et tall 1-5.\n");
            }
            System.out.println(s);
            valg = sc.next();
        }
    }

    public static void leggTilLege(){
        String navn;
        String kontID;
        String spesialist;
        Lege nyLege;
        Scanner sc = new Scanner(System.in);

        System.out.println("Navn: ");
        navn = sc.nextLine();
        System.out.println("Spesialist (Y/N): ");
        spesialist = sc.next();
        if (spesialist.equals("Y")){
            System.out.println("KontrollID: ");
            kontID = sc.next();
            nyLege = new Spesialist(navn, kontID);
            } else{
                nyLege = new Lege(navn);
            }
        leger.leggTil(nyLege);
        System.out.println("\n" + navn + " ble lagt til.\n");
    }
    public static void leggTilPasient(){
        String navn;
        String fodselsnummer;
        Pasient nyPasient;
        Scanner sc = new Scanner(System.in);

        System.out.println("Navn: ");
        navn = sc.next();
        System.out.println("Fodselsnummer: ");
        fodselsnummer = sc.next();
        nyPasient = new Pasient(navn, fodselsnummer);
        pasienter.leggTil(nyPasient);
        
    }

    public static void leggTilResept(){
        String farge = "";
        String type = "";
        String s = "Hvilken type resept: ";
        String hb = "\nH: Hvit \nB: Blaa";
        String mp = "\nM: Militaerresept \nP: P-resept \nV: Vanlig hvit";
        String tmp;

        Pasient pasient = null;
        Lege lege = null;
        Legemiddel legemiddel = null;
        int reit;
        Scanner sc = new Scanner(System.in);

        String legeNavn = sc.nextLine();//TODO hvis tid. Mulighet for aa legge til lege hvis ikke i system.
        while (lege != null) {
            System.out.println("Navn paa utskrivende lege:");
            for (Lege l : leger) {
                if (l.hentNavn().equals(legeNavn)) {
                    lege = l;
                    break;
                }
            }
                
            if (lege == null){
                System.out.println("Lege ikke i system.");
            }
        }

        String pasientNavn = sc.nextLine();//TODO hvis tid. Mulighet for aa legge til pasient hvis ikke i system.
        while (pasient != null) {
            System.out.println("Navn paa pasient:");
            for (Pasient p : pasienter) {
                if (p.hentNavn().equals(pasientNavn)) {
                    pasient = p;
                    break;
                }
            }
                
            if (pasient == null){
                System.out.println("Pasient ikke i system.");
            }
        }

        String legemiddelNavn = sc.nextLine(); //TODO hvis tid. Mulighet for aa legge til legemiddel hvis ikke i system.
        IndeksertListe<Legemiddel> tmpLegemidler = new IndeksertListe<>();
        while (legemiddel != null) {
            System.out.println("Navn paa legemiddel:");
            for (Legemiddel lm : legemidler) {
                if (lm.hentNavn().equals(legemiddelNavn)) {
                    tmpLegemidler.leggTil(lm);
                }
            }
            if (lege == null){
                System.out.println("Legemiddel ikke i system.");
            }
        }

        if (tmpLegemidler.stoerrelse() > 1) {
            System.out.println("Hvilken av disse onsker du?");
            int valg = 1; 
            for (Legemiddel lm : tmpLegemidler) {
                System.out.println(valg + "" + lm);
                valg++;
            }
            tmp = sc.nextint() 
            legemiddel = tmpLegemidler.hent(valg-1);
            //TODO Brukeren maa faa velge mellom legemidlene med samme navn.
        } else {legemiddel = tmpLegemidler.hent(0);}


        

    
        while (!(farge.equals("H") || farge.equals("B"))){
            if (farge != "") {System.out.println("Vennligst skriv H eller B");}
            System.out.println(s + hb);
            farge = sc.next();
        }
        if (farge.equals("H")){
            System.out.println(mp);
            type = sc.next();
            while(!(type.equals("M") || type.equals("P")|| type.equals("V"))){
                if (type != "") {System.out.println("Vennligst skriv M, P eller V\n");} 
                System.out.println(mp);
                type = sc.next();
            }
            if (type.equals("M")) {
                //legemiddel = new MilResept();
            }
        } else{
                    
        }
        // Resept(Legemiddel legemiddel, Lege utskrivendeLege, Pasient pasient, int reit)
    }
    public static void leggTilLegemiddel(){
        String navn;
        int pris;
        double dose;
        int styrke=0;
        Legemiddel nyttLegemiddel;

        String s = "Hva slags legemiddel?\nV: Vanlig\nVD: Vanedannende\nN: Narkotisk";

        Scanner sc = new Scanner(System.in);

        System.out.println(s);
        String valg = sc.next();
        while (!(valg.equals("V") || valg.equals("VD") || valg.equals("N"))){
            System.out.println("Velg V, VD eller N.\n");
            System.out.println(s);
            valg = sc.next();
        }

        System.out.print("Navn: ");
        navn = sc.nextLine(); 
        System.out.println();
        
        while (true) {
            System.out.print("Pris: ");
            try {
                pris = Integer.parseInt(sc.next());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Ugyldig pris >:(");
            }   
        }

        while (true) {
            System.out.print("Dose: ");
            try {
                dose = Double.parseDouble(sc.next());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Ugyldig dose >:(");
            }   
        }
        while (true && (!(valg.equals("V")))) {
            System.out.print("Styrke: ");
            try {
                styrke = Integer.parseInt(sc.next());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Ugyldig styrke >:(");
            }   
        }
        
        if (valg.equals("VD")){
            nyttLegemiddel = new Vanedannende(navn, pris, dose, styrke);
        } else if (valg.equals("N")){
            nyttLegemiddel = new Narkotisk(navn, pris, dose, styrke);
        } else {
            nyttLegemiddel = new Vanlig(navn, pris, dose);
        }

        legemidler.leggTil(nyttLegemiddel);
    }

    public static void brukResept() {        
        // String valgtPasient;
        // Scanner sc = new Scanner(System.in);

        // System.out.println("Hvilken pasient vil du se resepter for?");
        // for (Pasient pasient : pasienter) {
        //     System.out.println(pasient.hentID()+ ":" + pasient.hentNavn() + " (fnr " + pasient.hentFodselsnr() + ")");
        // }
        // valgtPasient = sc.next();

        // System.out.println("Valgt pasient: " + valgtPasient);
        // System.out.println("Hvilken resept vil du bruke?");
        // System.out.println("Brukte resept");
        // System.out.println("Kunne ikke bruke resept");
        // System.out.println("Hovedmeny");
    }
    
    public static void skrivStatistikk() {}

    public static void skrivTilFil() {}
}
