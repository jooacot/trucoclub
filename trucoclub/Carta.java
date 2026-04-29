package trucoclub;
public class Carta {
    private final String palo; // "ESPADA", "BASTO", "ORO", "COPA"
    private final int numero;
    private final int jerarquiaTruco; // Ej: el 1 de espada es 14, el 4 es 1
    private final int valorEnvido;
    private final String id;

    public Carta(String palo, int numero, int jerarquiaTruco, int valorEnvido) {
        this.palo = palo;
        this.numero = numero;
        this.jerarquiaTruco = jerarquiaTruco;
        this.valorEnvido = valorEnvido;
        this.id = numero + "_" + palo.toLowerCase();
    }

    
    
    public String getPalo() {
        return palo;
    }



    public int getNumero() {
        return numero;
    }



    public int getJerarquiaTruco() {
        return jerarquiaTruco;
    }



    public int getValorEnvido() {
        return valorEnvido;
    }



    @Override
    public String toString() {
        return numero + " de " + palo;
    }



    public String getId() {
        return id;
    }
}