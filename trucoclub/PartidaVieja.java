package trucoclub;

import java.util.ArrayList;
import java.util.List;

public class Partida {
    private Jugador jugador1;
    private Jugador jugador2;

    private Jugador repartidor; // fijo por ronda
    private Jugador mano; // fijo por ronda

    private Jugador turnoActual; // dinamico, el que tiene que tirar la carta ahora
    private Jugador elQueAbrioLaMano; // dinamico, el que abre la mano en cada ronda (puede haber hasta 3 rondas)
    private List<Carta> cartasEnMesa = new ArrayList<>(); // Para saber qué se tiró

    private Jugador ganadorPrimera = null; // el que gana primer mano
    private Jugador ganadorSegunda = null; // el que gana segunda mano
    private int manoActual = 1; // arranca en 1 son 3

    private int puntosEnJuegoEnvido = 0;
    private int puntosAnterioresEnvido = 0; // Lo que ya estaba acumulado
    private boolean envidoCantado = false;
    private boolean envidoCerrado = false; // Para que no se pueda cantar dos veces

    // --- ESTADO DEL TRUCO ---
    private int puntosEnJuegoTruco = 1; // Arranca en 1 (ronda simple)
    private boolean trucoCantado = false;
    private Jugador quienTieneElQuiero = null; // Para saber quién puede re-cantar

    private Mazo mazo;
    private int puntosPartido; // a 15 o 30

    public Partida(Jugador j1, Jugador j2, int puntos) {
        this.jugador1 = j1;
        this.jugador2 = j2;
        this.puntosPartido = puntos;
        this.mazo = new Mazo();

        // Al empezar la partida, podemos elegir uno al azar
        this.repartidor = (Math.random() > 0.5) ? j1 : j2;
        actualizarRoles(); // aca definimos quien es mano y pie dependiendo el azar que tira a lo primero

    }

    public void empezarRonda() {
        // 1. Limpiamos todo lo de la ronda anterior
        this.manoActual = 1;
        this.ganadorPrimera = null;
        this.ganadorSegunda = null;
        this.cartasEnMesa.clear();

        this.envidoCantado = false;
        this.envidoCerrado = false;
        this.puntosEnJuegoEnvido = 0;
        this.puntosAnterioresEnvido = 0;

        this.puntosEnJuegoTruco = 1;
        this.trucoCantado = false;
        this.quienTieneElQuiero = null;

        // 2. Repartimos cartas nuevas
        repartirCartas();

        // 3. Definimos quién sale jugando
        // (Siempre empieza el que es 'mano' al principio de la ronda)
        this.turnoActual = mano;
        this.elQueAbrioLaMano = mano;

        System.out.println("Nueva ronda iniciada. Es el turno de: " + turnoActual.getNombre());
        // aca esperariamos el movimiento del usuario que segun lo que haga ejecutaria
        // el realizarJugada
    }

    public void cambiarTurno() {
        this.turnoActual = (turnoActual == jugador1) ? jugador2 : jugador1;
    }

    private void actualizarRoles() {
        if (repartidor == jugador1) {
            mano = jugador2;
        } else {
            mano = jugador1;
        }
    }

    public void rotarRepartidor() {
        this.repartidor = (repartidor == jugador1) ? jugador2 : jugador1;
        actualizarRoles();
    }

    public Jugador getRepartidor() {
        return repartidor;
    }

    public Jugador getMano() {
        return mano;
    }

    public void repartirCartas() {
        // 1. Limpiamos las manos de los jugadores (por si quedó algo de la ronda
        // anterior)
        jugador1.getMano().clear();
        jugador2.getMano().clear();

        this.mazo = new Mazo();
        mazo.barajar();

        for (int i = 0; i < 3; i++) {
            mano.recibirCarta(mazo.repartir());
            repartidor.recibirCarta(mazo.repartir());
        }
    }

    public int getPuntos() {
        return puntosPartido;
    }

    public Jugador definirGanadorMano(
            Carta cartaAbre, // La carta que se tiró PRIMERO en esta vuelta
            Jugador jugadorAbre, // El jugador que tiró esa primera carta
            Carta cartaResponde, // La carta que se tiró SEGUNDO (para intentar matar)
            Jugador jugadorResponde // El jugador que tiró la segunda carta
    ) {
        if (cartaAbre.getJerarquiaTruco() > cartaResponde.getJerarquiaTruco()) {
            return jugadorAbre;
        } else if (cartaResponde.getJerarquiaTruco() > cartaAbre.getJerarquiaTruco()) {
            return jugadorResponde;
        }
        return null; // Parda
    }

    public Jugador definirGanadorEnvido() {
        int p1 = jugador1.calcularEnvido();
        int p2 = jugador2.calcularEnvido();
        if (p1 > p2)
            return jugador1;
        if (p2 > p1)
            return jugador2;
        return this.mano; // Empate gana el que es mano
    }

    public void realizarJugada(Jugador j, int indiceCarta) {
        // si hay un envido cantado y no se respondió, no se puede jugar carta
        if (envidoCantado && !envidoCerrado) {
            System.out.println("Primero hay que responder el Envido.");
            return;
        }
        if (trucoCantado) {
            System.out.println("Primero hay que responder el Truco.");
            return;
        }
        // Verificamos turno y que el índice exista en la mano actual
        if (j == turnoActual && indiceCarta >= 0 && indiceCarta < j.getMano().size()) {

            // El código solo entra acá si TODO está en orden
            Carta cartaTirada = j.jugarCarta(indiceCarta);
            cartasEnMesa.add(cartaTirada);
            System.out.println(j.getNombre() + " tiró " + cartaTirada);

            if (cartasEnMesa.size() == 2) {
                this.envidoCerrado = true;
            }

            if (cartasEnMesa.size() == 1) { // si es la primer carta jugada solo cambiamos de turno
                cambiarTurno();
            } else { // si no, hay que definir el ganador de la mano
                Jugador ganadorDeEsteDuelo = definirGanadorMano(
                        cartasEnMesa.get(0), elQueAbrioLaMano,
                        cartasEnMesa.get(1), (elQueAbrioLaMano == jugador1 ? jugador2 : jugador1));

                cartasEnMesa.clear();

                // ya teniendo el ganador de la mano procesamos a ver si termina o se sigue la
                // ronda
                procesarResultadoMano(ganadorDeEsteDuelo);
            }
        }
        // Si no se cumple la condición, el método termina sin hacer nada.
        // No hay mensajes de error, no hay rotura de programa.
    }

    public void procesarResultadoMano(Jugador ganadorMano) {
        System.out.println(
                "Resultado Mano " + manoActual + ": " + (ganadorMano == null ? "Parda" : ganadorMano.getNombre()));

        if (manoActual == 1) { // si el ganador es en la PRIMER mano !!!
            ganadorPrimera = ganadorMano;
            manoActual = 2;

            if (ganadorMano != null) {
                // Si el duelo tuvo un ganador (J1 o J2) arranca jugando la siguiente ronda
                this.turnoActual = ganadorMano;
            } else {
                // Si fue parda (ganadorMano es null), juega la siguiente ronda el que es mano
                this.turnoActual = this.mano;
            }

            // Una vez definido el turno, actualizamos quién abre esta nueva vuelta
            this.elQueAbrioLaMano = this.turnoActual;

        } else if (manoActual == 2) { // si el ganador es en la SEGUNDA mano !!!
            ganadorSegunda = ganadorMano;

            if (ganadorMano == null) { // si no hubo ganador de mano por parda
                if (ganadorPrimera != null) { // pero la primera ronda NO fue parda, entonces gana el que hizo primera
                    finalizarRonda(ganadorPrimera);
                } else {
                    // Si la 1ra fue parda y la 2da también, hay que ir a la 3ra obligatoriamente.
                    manoActual = 3;
                    this.turnoActual = mano; // arranca la tercer ronda el que es mano original
                    this.elQueAbrioLaMano = this.turnoActual;
                }
            }
            // Si NO fue parda la segunda ronda
            else {
                if (ganadorPrimera == null) { // si primera ronda fue parda
                    // ya gana el que gano la 2da mano y termina
                    finalizarRonda(ganadorMano);
                } else if (ganadorPrimera == ganadorMano) { // si el ganador de 2da ronda es el mismo que la 1ra,
                                                            // termina
                    // Ganó 1ra y 2da. Fin de ronda.
                    finalizarRonda(ganadorMano);
                } else { // si entra acá, es porque se juega si o si 3er ronda
                    // Ganaron una cada uno (1 a 1). Vamos a la tercera.
                    manoActual = 3;
                    this.turnoActual = ganadorMano; // arranca 3ra ronda el que ganó la 2da
                    this.elQueAbrioLaMano = this.turnoActual;
                }
            }

        } else if (manoActual == 3) { // si tenemos ganador en la 3er mano ya termina la mano entera
            if (ganadorMano != null) {
                finalizarRonda(ganadorMano);
            } else { // si entra acá porque sigue siendo parda o gano por primera el ganadorPrimera
                Jugador definitivo;
                if (ganadorPrimera != null) {
                    // Si hubo un ganador en la primera, ganaria por mano
                    definitivo = ganadorPrimera;
                } else {
                    // Si la primera también fue parda, el "mano" gana por derecho
                    definitivo = this.mano;
                }
                finalizarRonda(definitivo);
            }
        }
    }

    private void finalizarRonda(Jugador ganadorRonda, int puntosAñadir) {
        System.out.println("¡RONDA FINALIZADA! Ganador: " + ganadorRonda.getNombre());
        sumarPuntosJugador(ganadorRonda, puntosAñadir);

        rotarRepartidor();
        empezarRonda();
    }

    // Sobrecarga para cuando la ronda termina "naturalmente" al tirar todas las
    // cartas
    private void finalizarRonda(Jugador ganadorRonda) {
        finalizarRonda(ganadorRonda, this.puntosEnJuegoTruco);
    }

    public void cantarEnvido(Jugador elQueCanta, String tipoGrito) {
        // 1. Verificación de reglamento
        if (manoActual != 1 || envidoCerrado) {
            return;
        }

        if (puntosEnJuegoEnvido == 0) {
            puntosAnterioresEnvido = 1;
        } else {
            puntosAnterioresEnvido = puntosEnJuegoEnvido;
        }

        envidoCantado = true;

        // 2. Lógica de puntos según el grito
        switch (tipoGrito.toLowerCase()) {
            case "envido":
                puntosEnJuegoEnvido += 2;
                break;
            case "real envido":
                puntosEnJuegoEnvido += 3;
                break;
            case "falta envido":
                // Aquí la lógica es más compleja (lo que falta para ganar)
                puntosEnJuegoEnvido = calcularPuntosFaltaEnvido();
                break;
        }

        System.out.println(elQueCanta.getNombre() + " cantó " + tipoGrito);
        // aca como envidoCantado se vuelve true, la interfaz deberia mostrar
        // al otro jugador los botones para responder, y esa respuesta del jugador
        // va a llamar a responderEnvido
    }

    public void responderEnvido(Jugador j, String respuesta) {
        if (respuesta.equalsIgnoreCase("quiero")) {
            Jugador ganador = definirGanadorEnvido();
            // Se cobra la apuesta actual completa
            sumarPuntosJugador(ganador, puntosEnJuegoEnvido);
            cerrarEnvido();
        } else if (respuesta.equalsIgnoreCase("no quiero")) {
            Jugador elQueCanto = (j == jugador1) ? jugador2 : jugador1;

            // REGLA ORO: Si no quiere, se lleva lo que estaba acumulado ANTES.
            // Si no había nada acumulado (ej. Falta de una), vale 1.
            int puntosParaElQueCanto = (puntosAnterioresEnvido == 0) ? 1 : puntosAnterioresEnvido;

            sumarPuntosJugador(elQueCanto, puntosParaElQueCanto);
            cerrarEnvido();
        }
        // Agregamos el Falta Envido a los posibles re-cantos
        else if (respuesta.equalsIgnoreCase("envido") ||
                respuesta.equalsIgnoreCase("real envido") ||
                respuesta.equalsIgnoreCase("falta envido")) {

            cantarEnvido(j, respuesta);
        }
    }

    private void cerrarEnvido() {
        this.envidoCantado = false;
        this.envidoCerrado = true;
        this.puntosEnJuegoEnvido = 0;
        this.puntosAnterioresEnvido = 0;
    }

    private int calcularPuntosFaltaEnvido() {
        int puntosParaGanar = this.puntosPartido; // 15 o 30

        // Buscamos el puntaje más alto en la mesa actualmente
        int puntajeMasAlto = Math.max(jugador1.getPuntos(), jugador2.getPuntos());

        // La Falta es la distancia entre el puntero y la meta
        return puntosParaGanar - puntajeMasAlto;
    }

    private void sumarPuntosJugador(Jugador j, int cantidad) {
        // 1. Le pedimos al jugador que actualice su marcador interno
        j.sumarPuntos(cantidad);

        // 2. Avisamos por consola qué pasó
        System.out.println("📈 Puntos para " + j.getNombre() + ": +" + cantidad);
        System.out.println("📊 Marcador actual -> " + jugador1.getNombre() + ": " + jugador1.getPuntos() +
                " | " + jugador2.getNombre() + ": " + jugador2.getPuntos());

        // 3. Verificamos si alguien ya ganó el partido
        if (j.getPuntos() >= puntosPartido) {
            System.out.println("🏁🏁🏁 ¡PARTIDO FINALIZADO! 🏁🏁🏁");
            System.out.println("🏆 EL CAMPEÓN ES: " + j.getNombre().toUpperCase());

            // Aquí podrías agregar una bandera para detener el juego
            // por ejemplo: this.partidaTerminada = true;
        }
    }

    public void cantarTruco(Jugador elQueCanta, String tipoGrito) {
        // Validaciones:
        // 1. No se puede cantar si ya se aceptó un Vale Cuatro.
        // 2. Si alguien tiene el 'quiero', solo ese puede re-cantar.
        if (puntosEnJuegoTruco == 4 || (quienTieneElQuiero != null && elQueCanta != quienTieneElQuiero)) {
            System.out.println("No podés cantar en este momento.");
            return;
        }

        trucoCantado = true;
        System.out.println("📣 " + elQueCanta.getNombre() + " gritó: " + tipoGrito);

        // La interfaz debería bloquear las cartas y mostrar botones al oponente
    }

    public void responderTruco(Jugador j, String respuesta) {
        if (!trucoCantado)
            return;

        if (respuesta.equalsIgnoreCase("quiero")) {
            // Se sube el nivel de la apuesta
            if (puntosEnJuegoTruco == 1)
                puntosEnJuegoTruco = 2; // Truco
            else if (puntosEnJuegoTruco == 2)
                puntosEnJuegoTruco = 3; // Retruco
            else if (puntosEnJuegoTruco == 3)
                puntosEnJuegoTruco = 4; // Vale cuatro

            // REGLA DE ORO: El que acepta, ahora tiene el derecho a re-cantar
            this.quienTieneElQuiero = j;
            this.trucoCantado = false;
            System.out.println("✅ " + j.getNombre() + " dijo QUIERO. Ahora la ronda vale " + puntosEnJuegoTruco);
        } else if (respuesta.equalsIgnoreCase("no quiero")) {
            // Si no quiere, pierde la ronda automáticamente.
            // El ganador es el que cantó el último grito.
            Jugador ganador = (j == jugador1) ? jugador2 : jugador1;

            // Se lleva los puntos acumulados HASTA el grito anterior.
            // Si me cantás truco y no quiero, te llevás 1.
            // Si me cantás retruco (ya habiendo aceptado truco) y no quiero, te llevás 2.
            int puntosACobrar = (puntosEnJuegoTruco == 1) ? 1 : puntosEnJuegoTruco;

            System.out.println("❌ " + j.getNombre() + " dijo NO QUIERO.");
            finalizarRonda(ganador, puntosACobrar);
        } else {
            // Si elige re-cantar (ej: Truco -> Retruco)
            cantarTruco(j, respuesta);
        }
    }

}
