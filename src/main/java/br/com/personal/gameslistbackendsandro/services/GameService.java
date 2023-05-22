package br.com.personal.gameslistbackendsandro.services;

import br.com.personal.gameslistbackendsandro.dtos.GameDTO;
import br.com.personal.gameslistbackendsandro.dtos.GameMinDTO;
import br.com.personal.gameslistbackendsandro.entities.Game;
import br.com.personal.gameslistbackendsandro.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {
    @Autowired
    GameRepository gameRepository;

    @Transactional(readOnly = true)
    public List<GameMinDTO> findAll() {
        List<Game> result = gameRepository.findAll();

        // Opção 01
        List<GameMinDTO> gameMinDTOList = new ArrayList<>();
        result.stream().forEach(Game -> {
            gameMinDTOList.add(new GameMinDTO(Game));
        });
        return gameMinDTOList;

        // Opção 02
        // return result.stream().map(Game -> new GameMinDTO(Game)).toList();

        // Opção 03
        // return result.stream().map(GameMinDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public List<GameDTO> findAllFull() {
        List<Game> result = gameRepository.findAll();

        // Opção 01
        List<GameDTO> gameDTOList = new ArrayList<>();
        result.stream().forEach(Game -> {
            gameDTOList.add(new GameDTO(Game));
        });
        return gameDTOList;

        // Opção 02
        // return result.stream().map(Game -> new GameDTO(Game)).toList();

        // Opção 03
        // return result.stream().map(GameDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public GameDTO findById(@PathVariable Long listId) {
        Game result = gameRepository.findById(listId).get();
        return new GameDTO(result);
    }
}
