package trucoclub;
import java.util.ArrayList;
import java.util.List;

public class Jugador {
    private String nombre;
    private List<Carta> mano; // las 3 cartas con la que jugaria la mano
    private int puntos; //inicia en 0

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.mano = new ArrayList<>();
        this.puntos = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntos() {
        return puntos;
    }

    public List<Carta> getMano() {
        return mano;
    }

    public void recibirCarta(Carta c) {
        mano.add(c);
    }

    public int calcularEnvido() {
        int maxPuntaje = 0;

        // Si por algún motivo el jugador no tiene cartas, devolvemos 0
        if (mano == null || mano.isEmpty()) return 0;

        // 1. Comparar combinaciones de a pares usando la lista interna 'mano'
        for (int i = 0; i < mano.size(); i++) { 
            for (int j = i + 1; j < mano.size(); j++) {
                //en la primera iteracion comparo la carta 0-1, sigue con 0-2, termina con 1-2
                Carta c1 = mano.get(i);
                Carta c2 = mano.get(j);
                int puntajeCandidato;

                //comparo si son de igual palo
                if (c1.getPalo().equals(c2.getPalo())) {
                    // Mismo palo: 20 + suma de valores (figuras valen 0)
                    puntajeCandidato = 20 + c1.getValorEnvido() + c2.getValorEnvido();
                    //si no lo son, agarro la carta con mas valor
                } else {
                    // Distinto palo: solo el valor individual más alto
                    puntajeCandidato = Math.max(c1.getValorEnvido(), c2.getValorEnvido());
                }
                    
                if (puntajeCandidato > maxPuntaje) {
                    maxPuntaje = puntajeCandidato;
                }
            }
        }

        // 2. Verificación final: Por si hay una sola carta que es más alta que cualquier suma
        // (Ej: 7 de Oro, 1 de Copa y 2 de Basto -> El envido es 7)
        for (Carta c : mano) {
            if (c.getValorEnvido() > maxPuntaje) {
                maxPuntaje = c.getValorEnvido();
            }
        }

        return maxPuntaje;
    }
    
    // Método para jugar una carta y sacarla de la mano
    public Carta jugarCarta(int indice) {
        return mano.remove(indice);
    }

    public void sumarPuntos(int cantidad) {
    this.puntos += cantidad;
}

} 
    

