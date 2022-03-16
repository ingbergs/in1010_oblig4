
public class BlaaResept extends Resept {
    
    public BlaaResept(Legemiddel legemiddel, Lege utskrivendeLege, Pasient pasient, int reit) {
        super(legemiddel, utskrivendeLege, pasient, reit);
    }

    @Override
    public String farge() {return "Blaa";}

    @Override
    public int prisAaBetale() {
        double nyPris = legemiddel.hentPris() * 0.25;
        int RabbattertPris = (int) Math.round(nyPris);
        return RabbattertPris;
    }

    @Override
    public String toString() {
        return super.toString()
         + "\n\tType/Farge: " + farge()
         + "\n\tPris: " + prisAaBetale() + "kr";
    }
}
