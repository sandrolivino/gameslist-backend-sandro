package br.com.personal.gameslistbackendsandro.services;

import br.com.personal.gameslistbackendsandro.dtos.GameMinDTO;
import br.com.personal.gameslistbackendsandro.entities.Game;
import br.com.personal.gameslistbackendsandro.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // Opção 03
        // return result.stream().map(GameMinDTO::new).toList();

        // Opção 02
        // return result.stream().map(Game -> new GameMinDTO(Game)).toList();
    }
}
