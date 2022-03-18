
//Del A2

abstract class Legemiddel {
    protected String navn;
    protected int id;
    private static int idTeller = 1;
    protected int pris; //Hele kroner
    protected double mg; //Hvor mye virkestoff

    protected Legemiddel(String navn, int pris, double mg) {
        this.navn = navn;
        this.pris = pris;
        this.mg = mg;
        id = idTeller;
        idTeller++;

    }

    public int hentId() {
        return id;
    }

    public String hentNavn() {
        return navn;
    }

    public int hentPris() {
        return pris;
    }

    public double hentVirkestoff() {
        return mg;
    }

    public void settNyPris(int nyPris) {
        pris = nyPris;
    }

    @Override
    public String toString() {
        return "\nID: " + hentId() 
        + "\nNavn: " + hentNavn()
        + "\nPris: " + hentPris() + "kr"
        + "\nVirkestoff: " + hentVirkestoff() +"mg";
    }
}
