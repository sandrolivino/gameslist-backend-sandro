## D1 - Configurar o GitHub
* Criar o repositório gameslist-backend-levi no GitHub
  * Será gerado o link para o repositório como nesse exemplo:
    https://github.com/levigoncalves/gameslist-backend-levi.git


## D1 - Configurar o Git Local
* Clonar o repositório do GitHub
  * abrir o Git Bash de dentro da pasta do projeto e:
```
  $ git clone https://github.com/levigoncalves/gameslist-backend-levi.git
```


## D1 - Criação do projeto base

Entrar no spring initializer e criar o projeto com os seguintes dados:
* Project: Maven
* Language: Java 
* Spring Boot: 3.1.0 
* Project Metadata:
  * Group: br.com.personal (vamos adotar essa estrutura para projetos pessoais)
  * Artifact: gameslist-backend-levi
  * Name: gameslist-backend-levi
  * Description: Catálogo de jogos
  * Package name: br.com.personal.gameslist-backend-levi
  * Packaging: Jar
  * Java: 17 
* Dependencies 
  * Spring Web
  * Spring JPA
  * H2
  * PostgreSQL

GERAR O PROJETO e COPIAR PARA A PASTA DO GIT LOCAL

## D1 - Configurar o projeto no IntelliJ
* Abrir o IntelliJ e criar um novo projeto a partir do projeto criado no spring initializer
* Configurar o arquivo application.properties para que ele faça o redirecionamento dos Profiles
### application.properties
```
spring.profiles.active=${APP_PROFILE:dev}
spring.jpa.open-in-view=false
```

* Criar e configurar o arquivo application-dev.properties para que ele seja o Profile Dev (com H2)
### application-dev.properties
```
# H2 Connection
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=

# H2 Client
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Show SQL
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## D1 - Inicializar o projeto:
- Refatorar o arquivo GameslistBackendSandroApplication para GameslistBackendStartup (também nas classes de testes)
- Rodar o projeto por esse arquivo: GameslistBackendStartup
- Verificar se o H2 subiu:
  -- http://localhost:8080/h2-console
  -- JDBC URL: jdbc:h2:mem:testdb (A mesma que colocamos no arquivo application-dev.properties)

## D1 - COMMITAR
Apenas para manter o projeto base disponível para futuros projetos que utilizem as mesmas dependências.


## D2 - Entendendo o projeto
O projeto tem como objetivo armazenar um catálogo de jogos e classificá-los conforme a lista a qual cada jogo pertença. Veja o modelo de domínio abaixo
## Modelo de domínio _GamesList_

![Modelo de domínio DSList](https://raw.githubusercontent.com/devsuperior/java-spring-dslist/main/resources/dslist-model.png)

Perceba que entre Game e GameList, há uma tabela associativa chamada Belonging, isso se faz necessário porque nessa relação de pertencimento "Belonging"
há um atributo a mais, chamado "position". Ou seja, no banco de dados, para cada associação entre Game e GameList, haverá um registro em Belonging, indicando
em qual "position" da lista o game se encontra.

## D2 - Criar os pacotes padrões de qualquer projeto spring WEB
* entities (ou models)
* services
* repositories
* controllers
* dtos

## D2 - Criar as entidades (classes) Game e GameList
* Gerar construtores: remover o super
* Gerar os getters e setters
* Gerar hashCode & equals: só com o id é suficiente

## D2 - Criar a entidade (classe) Belonging
Essa classe é uma relação intermediária do relacionamento N to N entre Game e GameList
* Como o JpaRepository exige um id único "JpaRepository<T, ID>", não é possível criar uma chave primária em Belonging com os atributos:
  * private Game game;
  * private GameList list;
* Portanto, será preciso criar uma classe auxiliar que conterá uma referência para Game e para GameList:

## D2 - Criar a classe de apoio BelongingPK
* Essa classe será a chava primária da classe Belonging
```
@Embeddable
public class BelongingPK {

	@ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "list_id")
    private GameList list;

	// Construtores

	// Getters and Setters

	// Equals & HashCodes: aqui tem que usar os dois atributos, pois serão a chave primária em Belonging
}
```
* Observação: como essa classe será embutida como um ID de outra, devemos usar a annotation @Embeddable

## D2 - Inserir a chave primária BelongingPK em Belonging
Classe Belonging após a criação de BelongingPK
```
@Entity
@Table(name = "tb_belonging")
public class Belonging {

	// @EmbeddedId porque estamos a clesse BelongingPK que contém ID composto entre Game e GameList
	@EmbeddedId
	private BelongingPK id = new BelongingPK();

	private Integer position;

	// Construtores: no construtor com parâmetros, passar o Game e o GameList em separado ao invés de passar o BelongingPK
	// Ao invés disso:
	// public Belonging(BelongingPK id, Integer position) {
	// 	this.id = id;
	//	this.position = position;
	// }
	// Isso:
	public Belonging(Game game, GameList list, Integer position) {
		this.id.setGame(game);
		this.id.setList(list);
		this.position = position;
	}

	// Gerar Getters and Setters

	// HashCodes && Equals (com id e position)
}
```
* Obvervação: não usamos a annotation @Id, mas sim @EmbeddedId, pois estamos usando agora a classe ID criada anteriormente como @Embeddable

## D2 - Rodar a aplicação e verificar o erro de sintaxe por conta do atributo year, que é uma palavra reservada do SQL.
Para corrigir, alterar o nome da coluna com a annotation @Column
```
    @Column(name = "game_year")
    private Integer year;
```
## D2 - Rodar novamente a aplicação e verificar se as tabelas e seus respectivos relacionamentos foram criados corretamente.
```
TB_GAME
ID, GENRE, IMG_URL, LONG_DESCRIPTION, PLATFORMS, SCORE, SHORT_DESCRIPTION, TITLE, GAME_YEAR

TB_GAME_LIST
ID, NAME  

TB_BELONGING
POSITION, LIST_ID, GAME_ID 
```


## D2 - Fazer o seed do banco em Game, GameList e Belonging
* Criar o arquivo import.sql dentro de resources, com o seguinte conteúdo:
```
INSERT INTO tb_game_list (name) VALUES ('Aventura e RPG');
INSERT INTO tb_game_list (name) VALUES ('Jogos de plataforma');

INSERT INTO tb_game (title, score, game_year, genre, platforms, img_url, short_description, long_description) VALUES ('Mass Effect Trilogy', 4.8, 2012, 'Role-playing (RPG), Shooter', 'XBox, Playstation, PC', 'https://raw.githubusercontent.com/devsuperior/java-spring-dslist/main/resources/1.png', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Odit esse officiis corrupti unde repellat non quibusdam! Id nihil itaque ipsum!', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Delectus dolorum illum placeat eligendi, quis maiores veniam. Incidunt dolorum, nisi deleniti dicta odit voluptatem nam provident temporibus reprehenderit blanditiis consectetur tenetur. Dignissimos blanditiis quod corporis iste, aliquid perspiciatis architecto quasi tempore ipsam voluptates ea ad distinctio, sapiente qui, amet quidem culpa.');
INSERT INTO tb_game (title, score, game_year, genre, platforms, img_url, short_description, long_description) VALUES ('Red Dead Redemption 2', 4.7, 2018, 'Role-playing (RPG), Adventure', 'XBox, Playstation, PC', 'https://raw.githubusercontent.com/devsuperior/java-spring-dslist/main/resources/2.png', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Odit esse officiis corrupti unde repellat non quibusdam! Id nihil itaque ipsum!', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Delectus dolorum illum placeat eligendi, quis maiores veniam. Incidunt dolorum, nisi deleniti dicta odit voluptatem nam provident temporibus reprehenderit blanditiis consectetur tenetur. Dignissimos blanditiis quod corporis iste, aliquid perspiciatis architecto quasi tempore ipsam voluptates ea ad distinctio, sapiente qui, amet quidem culpa.');
INSERT INTO tb_game (title, score, game_year, genre, platforms, img_url, short_description, long_description) VALUES ('The Witcher 3: Wild Hunt', 4.7, 2014, 'Role-playing (RPG), Adventure', 'XBox, Playstation, PC', 'https://raw.githubusercontent.com/devsuperior/java-spring-dslist/main/resources/3.png', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Odit esse officiis corrupti unde repellat non quibusdam! Id nihil itaque ipsum!', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Delectus dolorum illum placeat eligendi, quis maiores veniam. Incidunt dolorum, nisi deleniti dicta odit voluptatem nam provident temporibus reprehenderit blanditiis consectetur tenetur. Dignissimos blanditiis quod corporis iste, aliquid perspiciatis architecto quasi tempore ipsam voluptates ea ad distinctio, sapiente qui, amet quidem culpa.');
INSERT INTO tb_game (title, score, game_year, genre, platforms, img_url, short_description, long_description) VALUES ('Sekiro: Shadows Die Twice', 3.8, 2019, 'Role-playing (RPG), Adventure', 'XBox, Playstation, PC', 'https://raw.githubusercontent.com/devsuperior/java-spring-dslist/main/resources/4.png', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Odit esse officiis corrupti unde repellat non quibusdam! Id nihil itaque ipsum!', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Delectus dolorum illum placeat eligendi, quis maiores veniam. Incidunt dolorum, nisi deleniti dicta odit voluptatem nam provident temporibus reprehenderit blanditiis consectetur tenetur. Dignissimos blanditiis quod corporis iste, aliquid perspiciatis architecto quasi tempore ipsam voluptates ea ad distinctio, sapiente qui, amet quidem culpa.');
INSERT INTO tb_game (title, score, game_year, genre, platforms, img_url, short_description, long_description) VALUES ('Ghost of Tsushima', 4.6, 2012, 'Role-playing (RPG), Adventure', 'XBox, Playstation, PC', 'https://raw.githubusercontent.com/devsuperior/java-spring-dslist/main/resources/5.png', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Odit esse officiis corrupti unde repellat non quibusdam! Id nihil itaque ipsum!', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Delectus dolorum illum placeat eligendi, quis maiores veniam. Incidunt dolorum, nisi deleniti dicta odit voluptatem nam provident temporibus reprehenderit blanditiis consectetur tenetur. Dignissimos blanditiis quod corporis iste, aliquid perspiciatis architecto quasi tempore ipsam voluptates ea ad distinctio, sapiente qui, amet quidem culpa.');
INSERT INTO tb_game (title, score, game_year, genre, platforms, img_url, short_description, long_description) VALUES ('Super Mario World', 4.7, 1990, 'Platform', 'Super Ness, PC', 'https://raw.githubusercontent.com/devsuperior/java-spring-dslist/main/resources/6.png', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Odit esse officiis corrupti unde repellat non quibusdam! Id nihil itaque ipsum!', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Delectus dolorum illum placeat eligendi, quis maiores veniam. Incidunt dolorum, nisi deleniti dicta odit voluptatem nam provident temporibus reprehenderit blanditiis consectetur tenetur. Dignissimos blanditiis quod corporis iste, aliquid perspiciatis architecto quasi tempore ipsam voluptates ea ad distinctio, sapiente qui, amet quidem culpa.');
INSERT INTO tb_game (title, score, game_year, genre, platforms, img_url, short_description, long_description) VALUES ('Hollow Knight', 4.6, 2017, 'Platform', 'XBox, Playstation, PC', 'https://raw.githubusercontent.com/devsuperior/java-spring-dslist/main/resources/7.png', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Odit esse officiis corrupti unde repellat non quibusdam! Id nihil itaque ipsum!', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Delectus dolorum illum placeat eligendi, quis maiores veniam. Incidunt dolorum, nisi deleniti dicta odit voluptatem nam provident temporibus reprehenderit blanditiis consectetur tenetur. Dignissimos blanditiis quod corporis iste, aliquid perspiciatis architecto quasi tempore ipsam voluptates ea ad distinctio, sapiente qui, amet quidem culpa.');
INSERT INTO tb_game (title, score, game_year, genre, platforms, img_url, short_description, long_description) VALUES ('Ori and the Blind Forest', 4, 2015, 'Platform', 'XBox, Playstation, PC', 'https://raw.githubusercontent.com/devsuperior/java-spring-dslist/main/resources/8.png', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Odit esse officiis corrupti unde repellat non quibusdam! Id nihil itaque ipsum!', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Delectus dolorum illum placeat eligendi, quis maiores veniam. Incidunt dolorum, nisi deleniti dicta odit voluptatem nam provident temporibus reprehenderit blanditiis consectetur tenetur. Dignissimos blanditiis quod corporis iste, aliquid perspiciatis architecto quasi tempore ipsam voluptates ea ad distinctio, sapiente qui, amet quidem culpa.');
INSERT INTO tb_game (title, score, game_year, genre, platforms, img_url, short_description, long_description) VALUES ('Cuphead', 4.6, 2017, 'Platform', 'XBox, Playstation, PC', 'https://raw.githubusercontent.com/devsuperior/java-spring-dslist/main/resources/9.png', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Odit esse officiis corrupti unde repellat non quibusdam! Id nihil itaque ipsum!', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Delectus dolorum illum placeat eligendi, quis maiores veniam. Incidunt dolorum, nisi deleniti dicta odit voluptatem nam provident temporibus reprehenderit blanditiis consectetur tenetur. Dignissimos blanditiis quod corporis iste, aliquid perspiciatis architecto quasi tempore ipsam voluptates ea ad distinctio, sapiente qui, amet quidem culpa.');
INSERT INTO tb_game (title, score, game_year, genre, platforms, img_url, short_description, long_description) VALUES ('Sonic CD', 4, 1993, 'Platform', 'Sega CD, PC', 'https://raw.githubusercontent.com/devsuperior/java-spring-dslist/main/resources/10.png', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Odit esse officiis corrupti unde repellat non quibusdam! Id nihil itaque ipsum!', 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Delectus dolorum illum placeat eligendi, quis maiores veniam. Incidunt dolorum, nisi deleniti dicta odit voluptatem nam provident temporibus reprehenderit blanditiis consectetur tenetur. Dignissimos blanditiis quod corporis iste, aliquid perspiciatis architecto quasi tempore ipsam voluptates ea ad distinctio, sapiente qui, amet quidem culpa.');

INSERT INTO tb_belonging (list_id, game_id, position) VALUES (1, 1, 0);
INSERT INTO tb_belonging (list_id, game_id, position) VALUES (1, 2, 1);
INSERT INTO tb_belonging (list_id, game_id, position) VALUES (1, 3, 2);
INSERT INTO tb_belonging (list_id, game_id, position) VALUES (1, 4, 3);
INSERT INTO tb_belonging (list_id, game_id, position) VALUES (1, 5, 4);

INSERT INTO tb_belonging (list_id, game_id, position) VALUES (2, 6, 0);
INSERT INTO tb_belonging (list_id, game_id, position) VALUES (2, 7, 1);
INSERT INTO tb_belonging (list_id, game_id, position) VALUES (2, 8, 2);
INSERT INTO tb_belonging (list_id, game_id, position) VALUES (2, 9, 3);
INSERT INTO tb_belonging (list_id, game_id, position) VALUES (2, 10, 4);
```

## D3 - Criar o repositório para Game, chamado GameRepository, no pacote repositories
```
public interface GameRepository extends JpaRepository<Game, Long> {
}
```

## D3 - Criar o DTO GameMinDTO
O objetivo desse DTO é trazer os dados mínimos, com apenas alguns campos do GAME para uma exibição rápida na tela inicial da aplicação. A exibição completa, com todos os dados do GAME será feita em outro DTO
* Observar que não usamos mapper, como fizemos com o Dozer no projeto anterior, fizemos o mapeamento simplesmente "setando" os valores da Entity no DTO e vice-versa.
```
public class GameMinDTO {

	private Long id;
	private String title;
	private Integer year;
	private String imgUrl;
	private String shortDescription;
	
	public GameMinDTO(Game entity) {
		id = entity.getId();
		title = entity.getTitle();
		year = entity.getYear();
		imgUrl = entity.getImgUrl();
		shortDescription = entity.getShortDescription();
	}
	
	// Somente GETTERS
}
```
* **Atenção:** o construtor de GameMinDTO recebe uma entidade Game, onde é feito o mapeamento dos campos da entidade para os campos do DTO. 

## D3 - Criar o serviço GameService para realizar as transações baásicas: findAll, findById, update, create etc.
* Criar o primeiro método: findAll()
```
    @Autowired
    GameRepository gameRepository;

    @Transactional(readOnly = true)
    public List<GameMinDTO> findAll(){
        List<Game> result = gameRepository.findAll();
        return result.stream().map(GameMinDTO::new).toList();
    }
```
* **Atenção:** não esquecer de fazer o import do repositório e de anotar o método com @Transactional(readOnly = true) 

## D3 - Criar o controller de Games. Essa classe será a responsável por expor a entidade Game para os usuários finais.
```
@RestController
@RequestMapping(value = "/games")
public class GameController {
  // TODO Métodos aqui!
}
```

## D3 - Criar o método findAll no controller de Games para trazer todos os GAMES, mas apenas com os dados mínimos do DTO que criamos há pouco.
```
	@GetMapping
	public List<GameMinDTO> findAll() {
		List<GameMinDTO> result = gameService.findAll();
		return result;
	}
```

## D3 - Criar o DTO GameDTO: para buscar todas os atributos do Game
* Lembrar que até então o DTO existente (GameMinDTO) trazia apenas dados resumidos do Game
* Nesse caso, como a cópia será exata (Entity to DTO), usar BeanUtils direto no construtor.
  * Observação: para o BeanUtils funcionar, o DTO tem que ter todos os Getters e Setters.

```
public class GameDTO {

	private Long id;
	private String title;
	private Integer year;
	private String genre;
	private String platforms;
	private Double score;
	private String imgUrl;
	private String shortDescription;
	private String longDescription;
	
	public GameDTO(Game entity) {
		BeanUtils.copyProperties(entity, this);
	}
	
    // GETTERS e SETTERS
    
}
```

## D3 - Criar o método findById em GameService (Retornando um GameDTO que será usado pelo controller)
* Lembrar de injetar GameRepository com @Autowired
* Lembrar que o findById do repositório retorna um Optional, então, ao final, inserir ".get()"
```
	@Transactional(readOnly = true)
	public GameDTO findById(@PathVariable Long listId) {
		Game result = gameRepository.findById(listId).get();
		return new GameDTO(result);
	}
```
* Lembrar também que, como o id pode não existir, devemos fazer um tratamento de exceções: FAZER DEPOIS

## D3 - Criar o método findById no controller e no service
* Lembrar de passar o value "{id}" no @GetMapping.
```
	@GetMapping(value = "/{id}")
	public GameDTO findById(@PathVariable Long id) {
		GameDTO result = gameService.findById(id);
		return result;
	}
```

## D3 - Criar a classe GameListDTO no pacote dtos
* Somente com getters, pois não usou o BeanUtils
```
public class GameListDTO {
	private Long id;
	private String name;
	
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
```

## D4 - Criar o repositório de GameList GameListRepository
## D4 - Criar o serviço de GameList GameListService
## D4 - Criar o controller GameListControler

## D4 - Criar uma consulta SQL Nativa no repositório de Games, GameRepository
* Deve ser implementado em GameRepository, pois todos os acessos a dados DEVEM partir do repository:
```
	@Query(nativeQuery = true, value = """
			SELECT tb_game.id, tb_game.title, tb_game.game_year AS gameYear, tb_game.img_url AS imgUrl,
			tb_game.short_description AS shortDescription, tb_belonging.position
			FROM tb_game
			INNER JOIN tb_belonging ON tb_game.id = tb_belonging.game_id
			WHERE tb_belonging.list_id = :listId
			ORDER BY tb_belonging.position
				""")
	List<GameMinProjection> searchByList(Long listId);
```
* Aqui entra o conceito de PROJECTION (List<GameMinProjection> searchByList(Long listId);), que nada mais é que uma interface, que "mapeia" os campos da minha consulta precedidos de "get", ou seja:
  * Se na pesquisa eu retorno "id" na projection eu devo ter um campo "getId", se eu retorno "game", devo ter na projection um campo "getGame" e assim sucessivamente.
  * Observação: para queries nativas o resultado OBRIGATORIAMENTE deve ser uma INTERFACE.
* Observe que o parâmetro tb_belonging.list_id = :listId da consulta é passado na projection como um Long listId.


## D4 - Criar a projection (interface) em um novo pacote chamado projections
* Não esquecer, a interface DEVE ter métodos GETTERS correspondentes à consulta.
```
public interface GameMinProjection {

	Long getId();
	String getTitle();
	Integer getGameYear();
	String getImgUrl();
	String getShortDescription();
	Integer getPosition();
}
```

## D4 - Criar um endpoint no controller para usar a consulta com a projection
* Esse endpoint é uma busca de games por lista
* **Atenção:** DEVE ser criado em GameService, pois é uma consulta que retorna Games
```
	@Transactional(readOnly = true)
	public List<GameMinDTO> findByGameList(Long listId) {
		List<GameMinProjection> games = gameRepository.searchByList(listId);
		return games.stream().map(GameMinDTO::new).toList();
	}
```
* Essa construção "GameMinDTO::new" é uma forma resumida de uma expressão lambda, que o IntelliJ gera automaticamente. (estudar sobre)

## D4 - Criar um novo construtor em GameMinDTO, que irá receber a projection como argumento

```
	public GameMinDTO(GameMinProjection projection) {
		id = projection.getId();
		title = projection.getTitle();
		year = projection.getGameYear();
		imgUrl = projection.getImgUrl();
		shortDescription = projection.getShortDescription();
	}
```

## D4 - Criação do endpoint findGames 
DEVE ser criado no Controller de Listas (Apesar de retornar Games)

```
	@GetMapping(value = "/{listId}/games")
	public List<GameMinDTO> findGames(@PathVariable Long listId) {
		List<GameMinDTO> result = gameService.findByGameList(listId);
		return result;
	}
```
* Não esquecer de injetar o GameService no controller de Listas
```
  @Autowired
  private GameListService gameListService;
```


## D5 - Criar demais perfis de projeto
* application-testpg.properties: Banco Postgres para testes
* application-testmy.properties: Banco Postgres para testes
* application-prod.properties: Sem passar informações de usuário e senha, pois estamos falando de ambiente de produção.

## D5 - Configurar o arquivo system.properties, com o conteúdo: java.runtime.version=17 (o mesmo do pom.xml)
- Fica na pasta raiz do projeto
- Algumas plataformas de nuvem, como o Hiroku, exigem esse arquivo.

## D5 - Gerar script da base de dados para rodar no postgresql
Não vamos deixar que o JPA crie o banco para nós, faremos manualmente.
* O spring gera isso automaticamente pra gente
* Descomentar as 4 primeiras linhas de application-dev.properties
* Mudar o perfil de test para dev em spring.profiles.active=${APP_PROFILE:test}
* Rodar a aplicação
  * Verificar que o spring criou na pasta do projeto (C:\Sistemas\Estudos\dslist-backend-aula3) um arquivo chamado create.sql
  * Copiar o conteúdo do arquivo e rodar do pgAdmin
* Testar a aplicação no Postman para ver se tudo está bem
  * Nesse momento, verificar que a consulta customizada, que funcionou no H2, não funcionou no PostgreSQL, pois a palavra year é reservada no PostgreSQL 
  * Ajustar a consulta customizada, tirando as aspas simples de year e renomeando para gameYear
  * Alterar o campo também na projection e no DTO GameMinDTO.

## D5 - Voltar o ambiente para test
spring.profiles.active=${APP_PROFILE:test}

## D5 - Configurar o CORS_ORIGINS
O que é o CORS? O CORS (Cross-origin Resource Sharing) é um mecanismo usado para adicionar cabeçalhos HTTP que informam aos navegadores para permitir que uma aplicação Web seja executada em uma origem e acesse recursos de outra origem diferente.
Criar a classe WebConfig no pacote config 
* O conteúdo é padrão (CTRL C + CTRL V)
* Não testei as URLs, por isso não configurei o CORS_ORIGINS no RailWay

## D4 - Testar esteira de CI/CD. RailWay integrado com GitHub
* Mudar alguma coisa no código e fazer o push no github pra ver a aplicação subindo no RailWay


## D4 - Configurar os métodos de ordenação da lista
* Criar um método update customizado no repositório de listas para atualizar a reordenação no BD
```
	@Modifying
	@Query(nativeQuery = true, 
		value = "UPDATE tb_belonging SET position = :newPosition WHERE list_id = :listId AND game_id = :gameId")
	void updateBelongingPosition(Long listId, Long gameId, Integer newPosition);
```

* Criar o método move em GameLisService para fazer a reordenação do Game na lista
```
	@Transactional
	public void move(Long listId, int sourceIndex, int destinationIndex) {

		List<GameMinProjection> list = gameRepository.searchByList(listId);

		GameMinProjection obj = list.remove(sourceIndex);
		list.add(destinationIndex, obj);

		int min = sourceIndex < destinationIndex ? sourceIndex : destinationIndex;
		int max = sourceIndex < destinationIndex ? destinationIndex : sourceIndex;

		for (int i = min; i <= max; i++) {
			gameListRepository.updateBelongingPosition(listId, list.get(i).getId(), i);
		}
	}
```
* Lembrar que a atualização da lista é imdepotente, ou seja, todas as vezes que for rodada gerará um resultado diferente

## D5 - Pré-requisitos para deploy CI/CD na nuvem
* Criar conta no RailWay - http://railway.app
* Login com o GitHub (conta com mais de 90 dias)
* Projeto Spring Boot salvo no GitHub
* Script do banco disponível
* Aplicativo de gestão de banco instalado (local ou docker)

## D5 - Passos RailWay
* Prover um servidor de BD no RailWay
  * Start a new project
  * Provision PostgreSQL
  * Conectar nosso PgAdmin (Docker) no PostgreSQL recém-criado no RailWay.
  * Copiar DATABASE_URL e preencher os campos: postgresql://postgres:z9dfZH8m60w6ux8mK3cp@containers-us-west-18.railway.app:8033/railway
  * Onde...
```
  postgresql://
  postgres = user
  :
  z9dfZH8m60w6ux8mK3cp = password
  @
  containers-us-west-18.railway.app = host
  :
  8033 = port
  /
  railway = database_name
  -- Incluir também esse database_name na aba Advanced

```

## D5 - Realizar seed de dados - com o create.sql que geramos nas aulas anteriores
- RailWay >> Schemas >> Public >> Tables: QueryTool
- Copiar e colar o conteúdo de create.sql

## D5 - Criar uma aplicação RailWay vinculada a um repositório GitHub
- Start a new project
- Deploy from GitHub
- Conectar no GitHub e escolher o repositório do projeto
- Configurar variáveis de ambiente...

## D5 - Configurar variáveis de ambiente
- APP_PROFILE
- DB_URL (Formato: jdbc:postgresql://host:porta/nome-da-base
- DB_USERNAME
- DB_PASSWORD
- CORS_ORIGINS

## D5 - Gerar um domínio público no RailWay
* Settings >> Domain: Gennerate
  * resultado (muda de app para app): dslist-backend-completo-production.up.railway.app
  * Testar no browser: dslist-backend-completo-production.up.railway.app/games

## D5 - Configurar o Postman com o endpoint na nuvem
* Criar variável de ambiente no postman