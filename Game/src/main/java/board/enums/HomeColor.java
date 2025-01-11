package board.enums;

public enum HomeColor {
    NONE,   // Pole nie należy do żadnego domku
    RED,    // Górny róg
    GREEN,  // Prawy górny róg
    BLACK,  // Prawy dolny róg
    BLUE,   // Dolny róg
    YELLOW, // Lewy dolny róg
    PURPLE; // Lewy górny róg

    private HomeColor opposite;

    public HomeColor getOpposite() {
        return opposite;
    }

    // przypisanie przeciwników dla kolorów
    static {
        RED.opposite = BLUE;
        GREEN.opposite = YELLOW;
        BLACK.opposite = PURPLE;
        BLUE.opposite = RED;
        YELLOW.opposite = GREEN;
        PURPLE.opposite = BLACK;
        NONE.opposite = null; // No opponent for NONE
    }
}
