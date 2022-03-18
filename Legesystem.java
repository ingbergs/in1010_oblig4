import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.io.PrintWriter;

public class Legesystem {

    // Pasient      -> IndeksertListe
    private static IndeksertListe<Pasient> pasienter = new IndeksertListe<>();

    // Leger        -> Prioritetskoe
    private static Prioritetskoe<Lege> leger = new Prioritetskoe<>();

    // Legemidler   -> IndeksertListe
    private static IndeksertListe<Legemiddel> legemidler = new IndeksertListe<>();

    // Resepter     -> IndeksertListe
    private static IndeksertListe<Resept> resepter = new IndeksertListe<>();

    public static void main(String[] args){

        lesFraFil("legedata.txt");
        //lesFraFil("nyStorFil.txt");

        meny();

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

        String linje = sc.nextLine();                                               // Hopper over foerste linje, siden vi vet at den ikke har data

                                                 
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
                    legemiddel = legemidler.hent(Integer.parseInt(deler[0]) - 1);   // - 1 for aa gjoere om fra nummer til indeks
                
                } catch (UgyldigListeindeks e) {
                    System.out.println("Error: legemiddelindeksfeil. " + e);
                    continue;
                }

                try {
                    pasient = pasienter.hent(Integer.parseInt(deler[2]) - 1);       // - 1 for aa gjore om fra nummer til indeks
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

    private static boolean sjekkGyldigFormat(String[] deler, int flagg) {           // Sjekker om inputformatet fra fil er korrekt

        if (flagg == 0) {               // Sjekker pasientformat
            return (deler.length == 2);

        } else if (flagg == 1) {        // Sjekker legemiddelformat

            if (deler[1].equals("vanedannende") || deler[1].equals("narkotisk")) {

                if (deler.length == 5) {

                    return (sjekkOmNumerisk(deler[2]) && sjekkOmNumerisk(deler[3]) && sjekkOmNumerisk(deler[4]));

                } else {
                    return false;
                }

            } else if (deler.length == 4) {

                return (sjekkOmNumerisk(deler[2]) && sjekkOmNumerisk(deler[3]));

            } else {
                return false;
            }

        } else if (flagg == 2) {        // Sjekker legeformat
            
            if (deler.length == 2) {

                return (sjekkOmNumerisk(deler[1]));

            } else {
                return false;
            }

        } else if (flagg == 3) {        // Sjekker reseptformat

            if (deler[3].equals("militaer")) {

                if (deler.length == 4) {

                    return (sjekkOmNumerisk(deler[0]) && sjekkOmNumerisk(deler[2]));

                }
                
            } else if (deler.length == 5) {

                return (sjekkOmNumerisk(deler[0]) && sjekkOmNumerisk(deler[2]) && sjekkOmNumerisk(deler[4]));

            } else {
                return false;
            }
            
        }

        return false;
    }

    public static boolean sjekkOmNumerisk(String s) {                               // Sjekker om en String kan gjores om til et tall (uten exceptions)

        boolean punktum = false;

        for (int i = 0; i < s.length(); i++) {

            if (i != 0 && i != (s.length() - 1) && s.charAt(i) == '.') {
                if (!punktum) {
                    punktum = true;
                } else {
                    return false;
                }

            } else if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    } 

    // Oppgave E2
    public static void meny() {
        String valg = "";
        String filnavn;
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
                nyttElement(sc);
            }
            else if (valg.equals("3")) {
                brukResept(sc);
            }
            else if (valg.equals("4")) {
                skrivStatistikk(sc);
            }
            else if (valg.equals("5")) {
                System.out.println("\nHva vil du at filen skal hete?");
                filnavn = sc.next();
                skrivTilFil(filnavn + ".txt");
            }
            else if (!valg.equals("6")) {
                System.out.println("Vennligst velg et tall 1-6.\n");
            }
        }
        sc.close();
    }  
    
    // Oppgave E3
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

    // Oppgave E4
    public static void nyttElement(Scanner sc){
        String s = "\n___LEGG TIL ELEMENT___"
        + "\nHva vil du legge til?"
        + "\n1: Lege"
        + "\n2: Pasient"
        + "\n3: Resept"
        + "\n4: Legemiddel"
        + "\n5: Tilbake";
        
        String valg = "";
        System.out.println(s);
        while (!valg.equals("5")) {
            
            if (valg.equals("1")) {
                leggTilLege(sc);
            } else if (valg.equals("2")) { 
                leggTilPasient(sc);
            } else if (valg.equals("3")) {
                try {
                    leggTilResept(sc);
                } catch (UlovligUtskrift e){
                    System.out.println(e);
                } 
            } else if (valg.equals("4")) {
                leggTilLegemiddel(sc);
            } else if (valg.equals("5")) { //Tilbake
                return;
            } else if (valg != ""){
                System.out.println("Vennligst velg et tall 1-5.\n");
            }
            System.out.println(s);
            valg = sc.next();
        }
    }

    public static void leggTilLege(Scanner sc){
        String navn;
        String kontID;
        String spesialist;
        Lege nyLege;

        System.out.println("Navn: ");

        sc.nextLine();                                          // Leser den gjenvaerende newline-characteren (\n) fra forrige .next()-kall
        navn = sc.nextLine();
        System.out.print("Navn: ");
        sc.nextLine();  
        navn = sc.nextLine();
        
        while (navn.strip().equals("")) {
            System.out.println("Legen maa ha et navn");
            System.out.print("Navn: ");
            sc.nextLine();                                          // Leser den gjenvaerende newline-characteren (\n) fra forrige .next()-kall
            navn = sc.nextLine();
        }
        
        navn = "Dr. " + navn;
        System.out.println("Spesialist (Y/N): ");
        spesialist = sc.next();
        if (spesialist.equals("Y") || spesialist.equals("y")){
            System.out.println("KontrollID: ");
            kontID = sc.next();
            nyLege = new Spesialist(navn, kontID);
            } else {
                nyLege = new Lege(navn);
            }
        leger.leggTil(nyLege);
        System.out.println("\n" + navn + " ble lagt til.\n");
        if (spesialist != "Y" && spesialist != "N" && spesialist != "n"){
            System.out.println(navn + " er ikke spesialist.\n");
        }
    }

    public static void leggTilPasient(Scanner sc){
        String navn = "";
        String fodselsnummer = "";
        Pasient nyPasient;

        System.out.println("\nNavn: ");
        sc.nextLine();                                    // Leser den gjenvaerende newline-characteren (\n) fra forrige .next()-kall
        navn = sc.nextLine();
        
        while (navn.strip().equals("")) {
            System.out.println("Vennligst skriv et navn!\nNavn: ");
            navn = sc.nextLine();
        }
        
        System.out.println("Fodselsnummer: ");
        fodselsnummer = sc.nextLine();
        //sc.nextLine(); 

        while(fodselsnummer.strip().equals("")) {
            System.out.println("Vennligst skriv et fodselsnummer!\nFodselsnummer: ");
            //sc.nextLine(); 
            fodselsnummer = sc.nextLine();
        }

        nyPasient = new Pasient(navn, fodselsnummer);
        pasienter.leggTil(nyPasient);
        System.out.println(navn + " lagt til i system.");
        
    }

    public static void leggTilResept(Scanner sc) throws UlovligUtskrift{
        Legemiddel legemiddel = null;
        Pasient pasient = null;
        Lege lege = null;
        String type = null;
        int reit = 0;
        boolean funnet = false;
        int inpInt;
        String inpStr;
        Resept nyResept = null;

        System.out.println("\nHvilket legemiddel skal resepten vaere for?");
        for (Legemiddel l : legemidler) {
            System.out.println(l.hentId()+ ": " + l.hentNavn() + ", " + l.hentVirkestoff() + " mg");
        }

        inpStr = sc.next();
        while (!sjekkOmNumerisk(inpStr) || (sjekkOmNumerisk(inpStr) && Integer.parseInt(inpStr) > legemidler.stoerrelse())) {
            System.out.println("\nVennligst skriv et tall mellom 1 og " + legemidler.stoerrelse() + ".");
            inpStr = sc.next();
        }

        inpInt = Integer.parseInt(inpStr);
        
        for (Legemiddel l : legemidler) {
            if (l.hentId()==inpInt && !funnet){
                legemiddel=l;
                funnet=true;
            }
        }
        funnet = false;

        System.out.println("\nHvilken pasient skal resepten vaere for?");
        for (Pasient p : pasienter) {
            System.out.println(p.hentID()+ ":" + p.hentNavn() + " (fnr " + p.hentFodselsnr() + ")");
        }
        
        inpStr = sc.next();
        while (!sjekkOmNumerisk(inpStr) || (sjekkOmNumerisk(inpStr) && Integer.parseInt(inpStr) > legemidler.stoerrelse())) {
            System.out.println("\nVennligst skriv et tall mellom 1 og " + pasienter.stoerrelse() + ".");
            inpStr = sc.next();
        }

        inpInt = Integer.parseInt(inpStr);

        for (Pasient p : pasienter) {
            if (p.hentID()==inpInt && !funnet){
                pasient=p;
                funnet=true;
            }
        }
        funnet = false;

        System.out.println("\nHvilken lege skal skrive ut resepten?");
        int teller = 1;
        for (Lege l : leger) {
            System.out.println(teller+ ": " + l.hentNavn() ); //TO DO: legge til kontrollID?
            teller++;
        }
        
        inpStr = sc.next();

        while (!sjekkOmNumerisk(inpStr) || (sjekkOmNumerisk(inpStr) && Integer.parseInt(inpStr) > legemidler.stoerrelse())) {
            System.out.println("\nVennligst skriv et tall mellom 1 og " + leger.stoerrelse() + ".");
            inpStr = sc.next();
        }
        
        inpInt = Integer.parseInt(inpStr);
    
        teller = 1;
        for (Lege l : leger) {
            if (teller == inpInt) {
                lege = l;
            }
            teller ++;
        }
        funnet = false;
        
        System.out.println("\nVelg type resept:\nB: Blaa\nH: Hvit");
        inpStr = sc.next();

        while (!(inpStr.toLowerCase().equals("b") || inpStr.toLowerCase().equals("h"))) {
            System.out.println("Vennligst skriv \"B\" eller \"H\".");
            inpStr = sc.next();
        }

        if (inpStr.toLowerCase().equals("b")){
            type = "blaa";
        } else if (inpStr.toLowerCase().equals("h")){

            System.out.println("\nM: Militaerresept\nP: P-resept\nV: Vanlig");
            inpStr = sc.next();

            while (!(inpStr.toLowerCase().equals("m") || inpStr.toLowerCase().equals("p") || inpStr.toLowerCase().equals("v"))) {
                System.out.println("Vennligst skriv \"M\", \"P\", eller \"V\".");
                inpStr = sc.next();
            }    

            if (inpStr.toLowerCase().equals("m")) {
                type = "militaer";
            } else if (inpStr.toLowerCase().equals("p")){
                type = "p";
            } else {
                type = "hvit";
            }
        }

        if (!type.equals("militaer")) {
            System.out.println("\nVelg antall reit:");
            inpStr = sc.next();
            while (!sjekkOmNumerisk(inpStr)) {
                System.out.println("Vennligst skriv et tall.");
                inpStr = sc.next();
            }
            reit = Integer.parseInt(inpStr);
        }

        if (type.equals("blaa")) {
            nyResept=lege.skrivBlaaResept(legemiddel, pasient, reit);
        } else if (type.equals("militaer")) {
            nyResept=lege.skrivMilResept(legemiddel, pasient);
        } else if (type.equals("p")) {
            nyResept=lege.skrivPResept(legemiddel, pasient, reit);
        } else {
            nyResept=lege.skrivHvitResept(legemiddel, pasient, reit);
        }
        
        pasient.nyResept(nyResept);

        System.out.println("Vellykket opprettelse av\n" + nyResept);
    }

    public static void leggTilLegemiddel(Scanner sc){
        String navn = "";
        int pris;
        double dose;
        int styrke=0;
        Legemiddel nyttLegemiddel;

        String s = "\nHva slags legemiddel?\nV: Vanlig\nVD: Vanedannende\nN: Narkotisk";

        System.out.println(s);
        String valg = sc.next();
        while (!(valg.toLowerCase().equals("v") || valg.toLowerCase().equals("vd") || valg.toLowerCase().equals("n"))){
            System.out.println("Velg V, VD eller N.\n");
            System.out.println(s);
            valg = sc.next();
        }

        System.out.print("\nNavn: ");
        sc.nextLine();                      // Leser den gjenvaerende newline-characteren (\n) fra forrige .next()-kall
        navn = sc.nextLine();
        
        while (navn.strip().equals("")) {
            System.out.println("Vennligst skriv et navn!\nNavn:");
            navn = sc.nextLine();
        }
        
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
            System.out.print("Mendge virkestoff(mg): ");
            try {
                dose = Double.parseDouble(sc.next());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Ugyldig dose >:(");
            }   
        }
        while (true && (!(valg.toLowerCase().equals("v")))) {
            System.out.print("Styrke: ");
            try {
                styrke = Integer.parseInt(sc.next());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Ugyldig styrke >:(");
            }   
        }
        
        if (valg.toLowerCase().equals("vd")){
            nyttLegemiddel = new Vanedannende(navn, pris, dose, styrke);
        } else if (valg.toLowerCase().equals("n")){
            nyttLegemiddel = new Narkotisk(navn, pris, dose, styrke);
        } else {
            nyttLegemiddel = new Vanlig(navn, pris, dose);
        }

        legemidler.leggTil(nyttLegemiddel);
        System.out.println(navn + " lagt til i system.");
    }
    

    public static void brukResept(Scanner sc) {        
        Pasient valgtPasient=null;
        Resept valgtResept=null;
        int inp;
        String inpStr;
        boolean pasientFunnet=false;
        boolean reseptFunnet=false;
        //Scanner sc = new Scanner(System.in);

        System.out.println("\nHvilken pasient vil du se resepter for?");
        for (Pasient pasient : pasienter) {
            if (pasient.hentReseptStabel().stoerrelse() > 0){      
            System.out.println(pasient.hentID()+ ":" + pasient.hentNavn() + " (fnr " + pasient.hentFodselsnr() + ")");
        }  
        }
        inpStr = sc.next();
        while (!sjekkOmNumerisk(inpStr) || (sjekkOmNumerisk(inpStr) && Integer.parseInt(inpStr) > pasienter.stoerrelse()) || (sjekkOmNumerisk(inpStr) && Integer.parseInt(inpStr) <= 0)){
            System.out.println("\nVennligst oppgi et gyldig heltall");
            inpStr = sc.next();
        }
        inp = Integer.parseInt(inpStr);
        for (Pasient pasient : pasienter) {
            if (pasient.hentID()==inp && !pasientFunnet){
                valgtPasient=pasient;
                pasientFunnet=true;
            }
        }   
        
        System.out.println("Valgt pasient: " + valgtPasient.hentNavn() + " (fnr " + valgtPasient.hentFodselsnr() + ")\n"); //Navn + fodselsnummer
        Stabel<Resept> pasientResepter = valgtPasient.hentReseptStabel();
        if (pasientResepter.stoerrelse()==0) {
            System.out.println("Ingen resepter");
        } else {
            System.out.println("\nHvilken resept vil du bruke?");
            
            int teller=1; 
            for (Resept resept : pasientResepter) {
                System.out.println(teller+ ":" + resept.hentLegemiddel().hentNavn() + " ("+ resept.hentReit() + " reit)");
                teller++;
            }
            inpStr = sc.next();
            while(!sjekkOmNumerisk(inpStr) || (sjekkOmNumerisk(inpStr) && Integer.parseInt(inpStr) > pasientResepter.stoerrelse())|| (sjekkOmNumerisk(inpStr) && Integer.parseInt(inpStr) <= 0)){
                System.out.println("\nVennligst oppgi et gyldig heltall");
                inpStr = sc.next();
            }
            inp = Integer.parseInt(inpStr);
            teller=1;
            for (Resept resept : pasientResepter) {
                if (teller==inp && !reseptFunnet){
                    valgtResept=resept;
                    reseptFunnet=true;
                }
                teller++;
            }
            if (valgtResept.bruk()){
                System.out.println("\nBrukte resept paa " + valgtResept.hentLegemiddel().hentNavn()+ ". Antall gjenvaerende reit: " + valgtResept.hentReit() + "\n");
            } else {
                System.out.println("\nKunne ikke bruke resept paa "+ valgtResept.hentLegemiddel().hentNavn()+ " (ingen gjenvaerende reit).");
            }
        }
    }
    
    public static void skrivStatistikk(Scanner sc) {

        String valg = "";

        while (!valg.equals("5")) {

        System.out.println("\nHvilken statistikk vil du se?\n" +
        "1: Totalt antall resepter paa vanedannende legemidler\n" +
        "2: Totalt antall resepter paa narkotiske legemidler\n" +
        "3: Navn paa alle leger som har skrevet ut minst en resept paa narkotiske legemidler\n" +
        "4: Navn paa alle pasienter som har minst en gyldig resept paa narkotiske legemidler\n" +
        "5: Tilbake");

            valg = sc.next();

            if (valg.equals("1")) {         // Totalt antall resepter paa vanedannende legemidler
            
                int teller = 0;
                for (Resept resept : resepter) {
                    if (resept.hentLegemiddel() instanceof Vanedannende) {
                        teller += 1;
                    }
                }

                System.out.println("\nTotalt antall resepter paa vanedannende legemidler: " + teller);

            } else if (valg.equals("2")) {  // Totalt antall resepter paa narkotiske legemidler
            
                int teller = 0;
                for (Resept resept : resepter) {
                    if (resept.hentLegemiddel() instanceof Narkotisk) {
                        teller += 1;
                    }
                }

                System.out.println("\nTotalt antall resepter paa narkotiske legemidler: " + teller);

            } else if (valg.equals("3")) {  // Navn paa leger som har skrevet ut minst en resept paa narkotisk + antall slike resepter per lege

                System.out.println("\nFoelgende leger har skrevet ut minst en resept paa narkotiske legemidler:\n");

                for (Lege lege : leger) {
                    int teller = 0;
                    if (!(lege instanceof Spesialist)) {
                        continue;
                    }
                    for (Resept resept : lege.hentUtskrevneResepter()) {
                        if (resept.hentLegemiddel() instanceof Narkotisk) {
                            teller += 1;
                        }
                    }
                    if (teller == 0) {
                        continue;
                    }

                    System.out.println("Navn:\t\t\t\t\t" + lege.hentNavn() + "\nAntall utskrevne narkotiske resepter:\t" + teller);
                }
                
            } else if (valg.equals("4")) {  // Navn paa alle pasienter som har minst en gyldig resept paa narkotisk + antallet per pasient

                System.out.println("\nFoelgende pasienter har minst en gyldig resept paa narkotiske legemidler:\n");

                for (Pasient pasient : pasienter) {
                    int teller = 0;

                    for (Resept resept : pasient.hentReseptStabel()) {
                        if (resept.hentLegemiddel() instanceof Narkotisk) {
                            teller += 1;
                        }
                    }
                    if (teller == 0) {
                        continue;
                    }

                    System.out.println("Navn:\t\t\t\t\t" + pasient.hentNavn() + "\nAntall utskrevne narkotiske resepter:\t" + teller);
                }

            } else {
                System.out.println("Skriv et tall 1-5");
            }
        }
    }

    public static void skrivTilFil(String filnavn) {
        String s;
        PrintWriter f = null;

        try {
            f = new PrintWriter(filnavn); 
        } catch (Exception e) {
            System.out.println("Noe gikk galt :(");
            System.exit(1);
        }

        f.println("# Pasienter (navn, fnr)");
        for(Pasient pasient : pasienter){
            f.println(pasient.hentNavn() + "," + pasient.hentFodselsnr());
        }
        f.println("# Legemidler (navn,type,pris,virkestoff,[styrke])");
        for(Legemiddel legemiddel : legemidler){
            s=legemiddel.hentNavn();
            if(legemiddel instanceof Narkotisk){
                Narkotisk narkotisk = (Narkotisk)legemiddel;
                s+=",narkotisk," + narkotisk.hentPris() + "," + narkotisk.hentVirkestoff() + "," + narkotisk.hentNarkotiskStyrke();
            } else if(legemiddel instanceof Vanedannende){
                Vanedannende vanedannende = (Vanedannende)legemiddel;
                s += ",vanedannende,"+ vanedannende.hentPris() + "," + vanedannende.hentVirkestoff() + "," + vanedannende.hentVanedannendeStyrke();
            } else {
                s+=",vanlig"+ legemiddel.hentPris() + "," + legemiddel.hentVirkestoff();
            }
            f.println(s);

        }

        f.println("# Leger (navn,kontrollid / 0 hvis vanlig lege)");
        for(Lege lege : leger){
            s=lege.hentNavn();
            if(lege instanceof Spesialist){
                Spesialist spesialist = (Spesialist) lege;
                s+= "," + spesialist.hentKontrollID();
            } else{
                s+= ",0";
            }
            f.println(s);
        }
        f.println("# Resepter (legemiddelNummer,legeNavn,pasientID,type,[reit])");
        for(Resept resept : resepter){
            s = resept.hentId() + "," + resept.hentLege().hentNavn() +","+ resept.hentPasient().hentID();
            if (resept instanceof BlaaResept){
                s += ",blaa," + resept.hentReit();
            } else if(resept instanceof MilResept){
                s += ",militaer";
            } else if (resept instanceof PResept){
                s +=",p," + resept.hentReit();
            } else{
                s +=",hvit," + resept.hentReit();
            }
            f.println(s);
        }

        f.close();
    }
}