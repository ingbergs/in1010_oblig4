public class Pasient {
    private String navn;
    private String fodselsnummer;
    private int id;
    static int tellerID = 1;
    private Stabel<Resept> reseptStabel = new Stabel<>();


    public Pasient(String navn, String fodselsnummer){
        this.navn = navn;
        this.fodselsnummer = fodselsnummer;
        id = tellerID;
        tellerID ++;
    }

    public String hentNavn(){
        return navn;
    }

    public String hentFodselsnr(){
        return fodselsnummer;
    }

    public int hentID(){
        return id;
    }

    public void nyResept(Resept resept){
        reseptStabel.leggTil(resept);
    }

    public Stabel<Resept> hentReseptStabel(){
        return reseptStabel;
    }

    @Override
    public String toString(){
        return "\nID:"+ id + "\nNavn: " + navn + "\nFodselsnummer: "+ fodselsnummer + "\nResepter: " + reseptStabel;
    }
}
