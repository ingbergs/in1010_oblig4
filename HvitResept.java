
public class HvitResept extends Resept {
    
    public HvitResept(Legemiddel legemiddel, Lege utskrivendeLege, Pasient pasient, int reit) {
        super(legemiddel, utskrivendeLege, pasient, reit);
    }

    @Override
    public String farge() {return "Hvit";}

    @Override
    public int prisAaBetale() { //Kun hvis det er en instans av HvitResept og ikke subklassene.
        return legemiddel.hentPris();
    }

    @Override
    public String toString() {
        return super.toString()
        + "\n\tType/Farge: " + farge()
        + "\n\tPris: " + prisAaBetale() + "kr";
    }
}
