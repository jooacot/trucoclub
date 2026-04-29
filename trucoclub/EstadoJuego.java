package trucoclub;

public enum EstadoJuego {
    ESPERANDO_CARTA,           // Flujo normal de tirar cartas
    ESPERANDO_RESPUESTA_ENVIDO, // Alguien gritó Envido y el otro debe decidir
    ESPERANDO_RESPUESTA_TRUCO,  // Alguien gritó Truco y el otro debe decidir
    TERMINADO                  // El partido llegó a los 15 o 30 puntos
}
