package trucoclub;
public class ValidadorTruco {

    public static int obtenerPoder(int numero, String palo) {
        // 1. Casos Especiales (Las cartas "Bravas")
        if (numero == 1 && palo.equals("ESPADA")) return 14;
        if (numero == 1 && palo.equals("BASTO")) return 13;
        if (numero == 7 && palo.equals("ESPADA")) return 12;
        if (numero == 7 && palo.equals("ORO")) return 11;

        // 2. Casos Generales (Jerarquía por número)
        return switch (numero) {
            case 3 -> 10;
            case 2 -> 9;
            case 1 -> 8;  // Anchos falsos (Copa y Oro)
            case 12 -> 7;
            case 11 -> 6;
            case 10 -> 5;
            case 7 -> 4;  // Sietes falsos (Copa y Basto)
            case 6 -> 3;
            case 5 -> 2;
            case 4 -> 1;  // La más débil
            default -> 0;
        };
    }

    public static int obtenerValorEnvido(int numero) {
        // Las figuras valen 0, el resto su número
        return (numero >= 10) ? 0 : numero;
    }
}