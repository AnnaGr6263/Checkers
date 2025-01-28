package data.repositories;

import data.entities.Setup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetupRepository extends JpaRepository<Setup, Long> {
    List<Setup> findByGameId(Long gameId); // Pobierz wszystkie ustawienia dla danej gry
}
