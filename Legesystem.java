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

    public static void main(String[] args) {

        // lesFraFil("legedata.txt");
        lesFraFil("nyStorFil.txt");

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

        if (flagg == 0) {                                                           // Sjekker pasientformat
            return (deler.length == 2);

        } else if (flagg == 1) {                                                    // Sjekker legemiddelformat

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

        } else if (flagg == 2) {                                                    // Sjekker legeformat
            
            if (deler.length == 2) {

                return (sjekkOmNumerisk(deler[1]));

            } else {
                return false;
            }

        } else if (flagg == 3) {                                                    // Sjekker reseptformat

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

            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) {
                    return false;
                } else {
                    continue;
                }
            }

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
                System.out.println("Hva vil du at filen skal hete?\n");
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
    public static void nyttElement(Scanner sc) {
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
                leggTilResept(sc);   
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
        System.out.println("Spesialist (Y/N): ");
        spesialist = sc.next();
        if (spesialist.equals("Y") || spesialist.equals("y")){
            System.out.println("KontrollID: ");
            kontID = sc.next();
            nyLege = new Spesialist(navn, kontID);
            } else{
                nyLege = new Lege(navn);
            }
        leger.leggTil(nyLege);
        System.out.println("\n" + navn + " ble lagt til.\n");
    }

    public static void leggTilPasient(Scanner sc){
        String navn;
        String fodselsnummer;
        Pasient nyPasient;

        System.out.println("Navn: ");
        sc.nextLine();                                          // Leser den gjenvaerende newline-characteren (\n) fra forrige .next()-kall
        navn = sc.nextLine();
        System.out.println("Fodselsnummer: ");
        fodselsnummer = sc.next();
        nyPasient = new Pasient(navn, fodselsnummer);
        pasienter.leggTil(nyPasient);
        
    }

    public static void leggTilResept(Scanner sc){
        String farge = "";
        String type = "";
        String s = "Hvilken type resept: ";
        String hb = "\nH: Hvit \nB: Blaa";
        String mp = "\nM: Militaerresept \nP: P-resept \nV: Vanlig hvit";
        int tmp;

        Pasient pasient = null;
        Lege lege = null;
        Legemiddel legemiddel = null;
        int reit;

        sc.nextLine();                                          // Leser den gjenvaerende newline-characteren (\n) fra forrige .next()-kall

        String legeNavn = sc.nextLine();//TODO hvis tid. Mulighet for aa legge til lege hvis ikke i system.
        while (lege != null) {
            System.out.println("Navn paa utskrivende lege:");
            for (Lege l : leger) {
                if (l.hentNavn().toLowerCase().equals(legeNavn.toLowerCase())) {
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
            tmp = sc.nextInt();
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

    public static void leggTilLegemiddel(Scanner sc){
        String navn;
        int pris;
        double dose;
        int styrke=0;
        Legemiddel nyttLegemiddel;

        String s = "Hva slags legemiddel?\nV: Vanlig\nVD: Vanedannende\nN: Narkotisk";

        System.out.println(s);
        String valg = sc.next();
        while (!(valg.equals("V") || valg.equals("VD") || valg.equals("N"))){
            System.out.println("Velg V, VD eller N.\n");
            System.out.println(s);
            valg = sc.next();
        }

        System.out.print("Navn: ");

        sc.nextLine();                                          // Leser den gjenvaerende newline-characteren (\n) fra forrige .next()-kall
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

    public static void brukResept(Scanner sc) {        
        Pasient valgtPasient=null;
        Resept valgtResept=null;
        int inp;
        boolean pasientFunnet=false;
        boolean reseptFunnet=false;
        //Scanner sc = new Scanner(System.in);

        System.out.println("Hvilken pasient vil du se resepter for?");
        for (Pasient pasient : pasienter) {
            System.out.println(pasient.hentID()+ ":" + pasient.hentNavn() + " (fnr " + pasient.hentFodselsnr() + ")");
        }
        inp = sc.nextInt();
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
            System.out.println("Hvilken resept vil du bruke?");
            
            int teller=0;
            for (Resept resept : pasientResepter) {
                System.out.println(teller+ ":" + resept.hentLegemiddel().hentNavn() + " ("+ resept.hentReit() + " reit)");
                teller++;
            }
            inp = sc.nextInt();
            teller=0;
            for (Resept resept : pasientResepter) {
                if (teller==inp && !reseptFunnet){
                    valgtResept=resept;
                    reseptFunnet=true;
                }
                teller++;
            }
            if (valgtResept.bruk()){
                System.out.println("Brukte resept paa " + valgtResept.hentLegemiddel().hentNavn()+ ". Antall gjenvaerende reit: " + valgtResept.hentReit() + "\n");
            } else {
                System.out.println("Kunne ikke bruke resept paa "+ valgtResept.hentLegemiddel().hentNavn()+ " (ingen gjenvaerende reit).");
            }
        }
    }
    
    // Oppgave E6
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

            if (valg.equals("1")) {                     // Totalt antall resepter paa vanedannende legemidler
            
                int teller = 0;
                for (Resept resept : resepter) {
                    if (resept.hentLegemiddel() instanceof Vanedannende) {
                        teller += 1;
                    }
                }

                System.out.println("\nTotalt antall resepter paa vanedannende legemidler: " + teller);

            } else if (valg.equals("2")) {              // Totalt antall resepter paa narkotiske legemidler
            
                int teller = 0;
                for (Resept resept : resepter) {
                    if (resept.hentLegemiddel() instanceof Narkotisk) {
                        teller += 1;
                    }
                }

                System.out.println("\nTotalt antall resepter paa narkotiske legemidler: " + teller);

            } else if (valg.equals("3")) {              // Navn paa leger som har skrevet ut minst en resept paa narkotisk + antall slike resepter per lege

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
                
            } else if (valg.equals("4")) {              // Navn paa alle pasienter som har minst en gyldig resept paa narkotisk + antallet per pasient

                System.out.println("\nFoelgende pasienter har minst en gyldig resept paa narkotiske legemidler:\n");

                for (Pasient pasient : pasienter) {
                    int teller = 0;

                    for (Resept resept : pasient.hentReseptStabel()) {
                        if (resept.hentLegemiddel() instanceof Narkotisk) {
                            if (resept.hentReit() > 0) {
                                teller += 1;
                            }
                        }
                    }
                    if (teller == 0) {
                        continue;
                    }

                    System.out.println("Navn:\t\t\t\t\t" + pasient.hentNavn() + "\nAntall utskrevne narkotiske resepter:\t" + teller);
                }

            } else if (!valg.equals("5")) {
                System.out.println("Vennligst skriv et tall mellom 1 og 5");
            }
        }
    }

    public static void skrivTilFil(String filnavn) {
        String s;
        PrintWriter f = null;
        //Narkotisk narkotisk;
        //Vanedannende vanedannede;
        //Spesialist spesialist;
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
                s += ",vanedannende"+ vanedannende.hentPris() + "," + vanedannende.hentVirkestoff() + "," + vanedannende.hentVanedannendeStyrke();
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
