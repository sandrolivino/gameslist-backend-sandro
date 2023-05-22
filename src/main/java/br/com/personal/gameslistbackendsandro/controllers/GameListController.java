package br.com.personal.gameslistbackendsandro.controllers;

import br.com.personal.gameslistbackendsandro.dtos.GameListDTO;
import br.com.personal.gameslistbackendsandro.dtos.GameMinDTO;
import br.com.personal.gameslistbackendsandro.dtos.ReplacementDTO;
import br.com.personal.gameslistbackendsandro.services.GameListService;
import br.com.personal.gameslistbackendsandro.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/lists")
public class GameListController {
    @Autowired
    GameListService gameListService;

    @Autowired
    GameService gameService;

    @GetMapping
    public List<GameListDTO> findAll() {
        return gameListService.findAll();
    }

    @GetMapping(value = "/{listId}/games")
    public List<GameMinDTO> findGamesByGameListId(@PathVariable Long listId) {
        List<GameMinDTO> result = gameService.findGamesByGameListId(listId);
        return result;
    }

    @PostMapping(value = "/{listId}/replacement")
    public void move(@PathVariable Long listId, @RequestBody ReplacementDTO body) {
        gameListService.move(listId, body.getSourceIndex(), body.getDestinationIndex());
    }
}
