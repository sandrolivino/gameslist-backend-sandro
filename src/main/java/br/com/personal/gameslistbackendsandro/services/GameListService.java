package br.com.personal.gameslistbackendsandro.services;

import br.com.personal.gameslistbackendsandro.dtos.GameDTO;
import br.com.personal.gameslistbackendsandro.dtos.GameListDTO;
import br.com.personal.gameslistbackendsandro.entities.GameList;
import br.com.personal.gameslistbackendsandro.repositories.GameListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameListService {
    @Autowired
    GameListRepository gameListRepository;

    @Transactional(readOnly = true)
    public List<GameListDTO> findAll() {
        List<GameList> result = gameListRepository.findAll();
        return result.stream().map(GameListDTO::new).toList();
    }
}
