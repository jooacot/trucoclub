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

        while (partida.getEstadoActual() != EstadoJuego.TERMINADO) {
            
            Jugador actual = (partida.getQuienDebeResponder() != null) 
                             ? partida.getQuienDebeResponder() 
                             : partida.getTurnoActual();

            if (actual == null) break;

            // --- PANEL DE CONTROL PARA PROGRAMADORES ---
            System.out.println("\n" + "=".repeat(40));
            System.out.println(" DEBUG INFO:");
            System.out.println(" > Estado Actual: [" + partida.getEstadoActual() + "]");
            System.out.println(" > Envido Cerrado: " + (partida.isEnvidoCerrado() ? "SÍ" : "NO"));
            System.out.println(" > Puntos en juego (Truco): " + partida.getPuntosEnJuegoTruco());
            System.out.println("=".repeat(40));

            System.out.println(" MARCADOR: " + j1.getNombre() + " [" + j1.getPuntos() + "] - " 
                                           + j2.getNombre() + " [" + j2.getPuntos() + "]");
            System.out.println("-" + " ".repeat(38) + "-");
            System.out.println("TURNO DE: " + actual.getNombre());
            
            System.out.print("TUS CARTAS: ");
            for (int i = 0; i < actual.getMano().size(); i++) {
                System.out.print("[" + i + "] " + actual.getMano().get(i) + "  ");
            }
            System.out.println();

            // --- GUÍA DE ACCIONES POSIBLES ---
            imprimirAccionesPosibles(partida.getEstadoActual());
            
            System.out.print("\nAcción: ");
            String entrada = sc.nextLine().toLowerCase().trim();

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
                System.out.println("❌ Comando inválido o error en lógica.");
            }

            if (partida.getEstadoActual() == EstadoJuego.TERMINADO) {
                break; 
            }
        }

        System.out.println("\n****************************************");
        System.out.println("          ¡PARTIDA FINALIZADA!");
        System.out.println("   GANADOR: " + (j1.getPuntos() >= 15 ? j1.getNombre() : j2.getNombre()));
        System.out.println("****************************************");

        sc.close();
        System.exit(0);
    }

    // MÉTODO AUXILIAR PARA AYUDAR AL JUGADOR
    private static void imprimirAccionesPosibles(EstadoJuego estado) {
        System.out.print("ACCIONES: ");
        switch (estado) {
            case ESPERANDO_CARTA:
                System.out.println("[t 0, t 1, t 2] (tirar), [envido], [truco], [mazo]");
                break;
            case ESPERANDO_RESPUESTA_ENVIDO:
                System.out.println("[quiero], [no quiero], [real envido], [falta envido]");
                break;
            case ESPERANDO_RESPUESTA_TRUCO:
                System.out.println("[quiero], [no quiero], [retruco], [vale cuatro]");
                break;
            default:
                System.out.println("[No hay acciones definidas para este estado]");
        }
    }
}