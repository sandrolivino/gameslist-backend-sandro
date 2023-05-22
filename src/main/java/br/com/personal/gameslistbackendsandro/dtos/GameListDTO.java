package br.com.personal.gameslistbackendsandro.dtos;

import br.com.personal.gameslistbackendsandro.entities.GameList;

public class GameListDTO {
    private Long id;
    private String name;

    // Mesma "solução" de GameDTO e GameMinDTO, passar a entidade no construtor do DTO.
    public GameListDTO(GameList entity) {
        id = entity.getId();
        name = entity.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}