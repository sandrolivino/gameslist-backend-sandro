package br.com.personal.gameslistbackendsandro.repositories;

import br.com.personal.gameslistbackendsandro.entities.GameList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameListRepository extends JpaRepository<GameList, Long> {
}
