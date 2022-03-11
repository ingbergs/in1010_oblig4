
abstract class Resept {
    protected int id;
    protected static int idTeller = 1;
    protected Legemiddel legemiddel;
    protected Lege utskrivendeLege;
    protected Pasient pasient;
    protected int reit; //Antall ganger respten kan brukes.

    public Resept(Legemiddel legemiddel, Lege utskrivendeLege, Pasient pasient, int reit) {
        this.legemiddel = legemiddel;
        this.utskrivendeLege = utskrivendeLege;
        this.pasient = pasient;
        this.reit = reit;
        id = idTeller;
        idTeller++;
    }

    public int hentId() {
        return id;
    }

    public Legemiddel hentLegemiddel() {
        return legemiddel;
    }

    public Lege hentLege() {
        return utskrivendeLege;
    }

    public Pasient hentPasient() { //Denne var originalt hentPasientID.
        return pasient;
    }

    public int hentReit() {
        return reit;
    }

    public boolean bruk() {
        if (reit <= 0) {
            return false;
        }
        else {
            reit--;
            return true;
        }
    }

    abstract public String farge(); //Om det er en Blaa eller Hvit resept.
    abstract public int prisAaBetale();

    @Override
    public String toString() {
        return "\n__Reseptinfo__"
        + "\nResept-ID: " + id
        + "\nLegemiddel: " + legemiddel.hentNavn()
        + "\nUtskrevet av: Dr." + utskrivendeLege.hentNavn()
        + "\nPasient: " + pasient
        + "\nAntall reit igjen: " + reit;
    }
}
