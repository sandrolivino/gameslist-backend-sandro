package br.com.personal.gameslistbackendsandro.repositories;

import br.com.personal.gameslistbackendsandro.entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
