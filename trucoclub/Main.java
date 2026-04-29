package trucoclub;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Jugador j1 = new Jugador("Jugador 1");
        Jugador j2 = new Jugador("Jugador 2");
        Partida partida = new Partida(j1, j2, 15);

        partida.empezarRonda();
        System.out.println("--- BIENVENIDO AL TRUCO CLUB ---");

        // 1. EL BUCLE PRINCIPAL: Se detiene si el estado es TERMINADO
        while (partida.getEstadoActual() != EstadoJuego.TERMINADO) {
            
            Jugador actual = (partida.getQuienDebeResponder() != null) 
                             ? partida.getQuienDebeResponder() 
                             : partida.getTurnoActual();

            // SEGURIDAD: Si no hay nadie para jugar, rompemos el bucle
            if (actual == null) break;

            System.out.println("\n========================================");
            System.out.println(" MARCADOR: " + j1.getNombre() + "[" + j1.getPuntos() + "] - " 
                                           + j2.getNombre() + "[" + j2.getPuntos() + "]");
            System.out.println("========================================");
            System.out.println("Turno de: " + actual.getNombre());
            
            System.out.println("Tus cartas:");
            for (int i = 0; i < actual.getMano().size(); i++) {
                System.out.println("[" + i + "] " + actual.getMano().get(i));
            }
            
            System.out.print("\nAcción: ");
            String entrada = sc.nextLine().toLowerCase().trim();

            // 2. PROCESAMIENTO: Después de CADA comando, el bucle volverá arriba 
            // y chequeará si el estado cambió a TERMINADO.
            try {
                if (entrada.startsWith("t ")) {
                    String[] partes = entrada.split("\\s+");
                    if (partes.length == 2) {
                        partida.realizarJugada(actual, Integer.parseInt(partes[1]));
                    }
                } 
                else if (entrada.equals("envido") || entrada.equals("real envido") || entrada.equals("falta envido")) {
                    partida.cantarEnvido(actual, entrada);
                } 
                else if (entrada.equals("truco") || entrada.equals("retruco") || entrada.equals("vale cuatro")) {
                    partida.cantarTruco(actual, entrada);
                } 
                else if (entrada.equals("quiero") || entrada.equals("no quiero")) {
                    partida.responder(actual, entrada);
                } 
                else if (entrada.equals("mazo")) {
                    partida.irseAlMazo(actual);
                }
            } catch (Exception e) {
                System.out.println("❌ Error. Intentá de nuevo.");
            }

            // 3. SEGUNDO CANDADO: Si la acción anterior terminó el partido, 
            // forzamos la salida del bucle para no pedir otra entrada.
            if (partida.getEstadoActual() == EstadoJuego.TERMINADO) {
                break; 
            }
        }

        // 4. CIERRE TOTAL DEL PROCESO
        System.out.println("\n****************************************");
        System.out.println("          ¡PARTIDA FINALIZADA!");
        System.out.println("   GANADOR: " + (j1.getPuntos() >= 15 ? j1.getNombre() : j2.getNombre()));
        System.out.println("****************************************");

        sc.close();
        System.exit(0); // Esto mata el proceso de Java por completo
    }
}