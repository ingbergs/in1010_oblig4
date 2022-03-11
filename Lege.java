

//Del D oblig 3.
public class Lege implements Comparable<Lege>{
    protected String navn;
    IndeksertListe<Resept> utskrevneResepter = new IndeksertListe<>();

    public Lege(String navn) {
        this.navn = navn;
    }

    public String hentNavn() {return navn;}

    //D2
    public IndeksertListe<Resept> hentUtskrevneResepter() {
        return utskrevneResepter;
    }


    //D3
    //Kun spesialister kan skrive ut Narkotiske legemiddler og kun med blaa resepter.
    //Noe maa catche excetions i hovedprogrammet!
    public HvitResept skrivHvitResept (Legemiddel legemiddel, Pasient pasient, int reit) throws UlovligUtskrift {
        if (legemiddel instanceof Narkotisk) {
            throw new UlovligUtskrift(this, legemiddel);
        }
        else {            
            HvitResept nyHvit = new HvitResept(legemiddel, this, pasient, reit);
            utskrevneResepter.leggTil(nyHvit);
            return nyHvit;
        }
    }

    MilResept skrivMilResept (Legemiddel legemiddel, Pasient pasient) throws UlovligUtskrift {
        if (legemiddel instanceof Narkotisk) {
            throw new UlovligUtskrift(this, legemiddel);
        }
        else {
            MilResept nyMil = new MilResept(legemiddel, this, pasient);
            utskrevneResepter.leggTil(nyMil);
            return nyMil;}
    }

    PResept skrivPResept (Legemiddel legemiddel, Pasient pasient, int reit) throws UlovligUtskrift {
        if (legemiddel instanceof Narkotisk) {
            throw new UlovligUtskrift(this, legemiddel);
        }
        else {
            PResept nyP = new PResept(legemiddel, this, pasient, reit);
            utskrevneResepter.leggTil(nyP);
            return nyP;
        }
    }

    BlaaResept skrivBlaaResept (Legemiddel legemiddel, Pasient pasient, int reit) throws UlovligUtskrift {
        if (legemiddel instanceof Narkotisk && !(this instanceof Spesialist)) {
            throw new UlovligUtskrift(this, legemiddel);
        }
        else {
            BlaaResept nyBlaa = new BlaaResept(legemiddel, this, pasient, reit);
            utskrevneResepter.leggTil(nyBlaa);
            return nyBlaa;
        }
    }


    //D1
    @Override
    public int compareTo(Lege SkalSammenlignes) {
        return navn.compareTo(SkalSammenlignes.hentNavn());
    }

    @Override
    public String toString() {
        return "\n__Lege__\nNavn: " + navn;
    }
}
