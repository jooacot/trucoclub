package trucoclub;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mazo {
    private List<Carta> cartas;

    public Mazo() { 
        this.cartas = new ArrayList<>();
        String[] palos = {"ESPADA", "BASTO", "ORO", "COPA"};

        for (String palo : palos) {
            for (int n = 1; n <= 12; n++) {
                // El Truco Argentino no usa 8 ni 9
                if (n == 8 || n == 9) continue;

                // Consultamos al validador antes de crear la carta
                int poder = ValidadorTruco.obtenerPoder(n, palo);
                int envido = ValidadorTruco.obtenerValorEnvido(n);

                cartas.add(new Carta(palo, n, poder, envido));
            }
        }
    }

    public void barajar() {
        Collections.shuffle(cartas);
    }

    public Carta repartir() {
        if (cartas.isEmpty()) {
            throw new IllegalStateException("No hay más cartas en el mazo");
        }
        return cartas.remove(0);
    }
}