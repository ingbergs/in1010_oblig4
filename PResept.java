
public class PResept extends HvitResept {
    
    public PResept(Legemiddel legemiddel, Lege utskrivendeLege, Pasient pasient, int reit) {
        super(legemiddel, utskrivendeLege, pasient, reit);
    }

    @Override
    public int prisAaBetale() {
        int nyPris = legemiddel.hentPris() - 108;
        if (nyPris <= 0) {return 0;}
        else {return nyPris;}
    }
}
