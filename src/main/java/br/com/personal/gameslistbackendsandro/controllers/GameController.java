package br.com.personal.gameslistbackendsandro.controllers;

import br.com.personal.gameslistbackendsandro.dtos.GameDTO;
import br.com.personal.gameslistbackendsandro.dtos.GameMinDTO;
import br.com.personal.gameslistbackendsandro.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/games")
public class GameController {
    @Autowired
    GameService gameService;

    @GetMapping
    public List<GameMinDTO> findAll(){
        return gameService.findAll();
    }

    @GetMapping(value = "/full")
    public List<GameDTO> findAllFull(){
        return gameService.findAllFull();
    }

    @GetMapping(value = "/{id}")
    public GameDTO findById(@PathVariable Long id) {
        GameDTO result = gameService.findById(id);
        return result;
    }
}