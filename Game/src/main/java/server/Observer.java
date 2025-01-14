package server;

/**
 * Interfejs obserwatora.
 */
public interface Observer {
    /**
     * Metoda update. Odbiera wiadomości od GameManagera.
     *
     * @param message Treść wiadomości.
     */
    void update(String message); // Powiadomienie o zmianach
}
