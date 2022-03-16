
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
        return "\n\tResept-ID: " + id
        + "\n\tLegemiddel: " + legemiddel.hentNavn()
        + "\n\tUtskrevet av: " + utskrivendeLege.hentNavn()
        //+ "\nPasient: " + pasient.hentNavn()
        + "\n\tAntall reit igjen: " + reit;
    }
}
