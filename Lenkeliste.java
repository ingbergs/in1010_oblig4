import java.util.Iterator;
abstract class Lenkeliste<T> implements Liste<T> { 
    protected Node foerste = null;

    @Override
    public int stoerrelse() {
        int teller = 0;
        Node node = foerste;
        while (node != null){
            teller++;
            node = node.neste;
        }
        return teller; 
    }

    @Override
    public void leggTil(T x) {
        Node nyNode = new Node(x);
        if (foerste == null) {
            foerste = nyNode;
        } else {
            Node node = foerste;
            while (node.neste != null) {
                node = node.neste;
            }
            node.neste = nyNode;
        }
    }

    @Override
    public T hent() {
        return foerste.data;
    }

    @Override
    public T fjern() {
        Node node = foerste;
        if (foerste == null) {
            throw new UgyldigListeindeks(-1);
        }
        foerste = foerste.neste;
        return node.data;
    }

    @Override
    public String toString() {
        String s = "";
        Node node = foerste;
        while (node != null) {
            s+= node.data.toString() + " -> "; // node.data.toString() i tilfelle node ikke er String
            node = node.neste;
        }
        return s;
    }

    @Override
    public Iterator<T> iterator() {
        return new LenkelisteIterator();
    }

    protected class Node {
        Node neste = null;
        T data;
        public Node(T x) {
            data = x;
        }
    }

    protected class LenkelisteIterator implements Iterator<T> {
        //int indeks = 0
        Node node = foerste;
        public boolean hasNext() {
            return node != null;
        }

        public T next() {
            T x = node.data;
            node = node.neste;
            return x;
        }
    }
}