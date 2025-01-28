package server;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = {
        "server",               // Pakiet z klasami serwera i logiką gry
        "data",                 // Pakiet z encjami i repozytoriami
        "board",                // Pakiet z logiką dotyczącą planszy
        "GUI"                   // Pakiet GUI, jeśli Spring zarządza jego komponentami
})
@EnableJpaRepositories(basePackages = "data.repositories") // Lokalizacja repozytoriów JPA
@EnableTransactionManagement // Włączenie obsługi transakcji w JPA
public class AppConfig {
}
