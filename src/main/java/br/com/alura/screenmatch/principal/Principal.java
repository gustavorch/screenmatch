package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private List<DadosSerie> dadosSeries = new ArrayList<>();

    // Injeção de dependência
    private SerieRepository repositorio;

    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries já buscadas
                    4 - Buscar série por título
                    5 - Buscar séries por ator
                    6 - Top 3 séries (entre as já buscadas)
                    7 - Buscar séries por gênero
                    8 - Buscar séries por número máximo de temporadas e mínimo de avaliação
                    9 - Buscar episódios por um trecho do título
                    10 - Top 5 episódios de uma série
                    11 - Buscar episódios a partir de uma data

                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    buscarTop3Series();
                    break;
                case 7:
                    buscarSeriePorGenero();
                    break;
                case 8:
                    seriesPorMaxTemporadasAndMinAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    top5EpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosDepoisDeUmaData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }


    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        // dadosSeries.add(dados);
        Serie serie = new Serie(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca: ");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        // Optional<Serie> serie = series.stream()
        // .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
        // .findFirst();

        // Verifica se essa série existe ou não
        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(
                        ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season="
                                + i
                                + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Serie não encontrada.");
        }
    }

    private void listarSeriesBuscadas() {
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Dados da série: " + serieBusca.get());
        } else {
            System.out.println("Série não encontrada.");
        }
    }

    private void buscarSeriesPorAtor() {
        System.out.println("Qual o nome do ator? ");
        var nomeAtor = leitura.nextLine();
        System.out.println("Exibir séries a partir de qual valor de avaliação?");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas = repositorio
                .findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Séries que têm um ator chamado" + nomeAtor);
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " - avaliação: " + s.getAvaliacao()));
    }

    private void buscarTop3Series() {
        List<Serie> topSeries = repositorio.findTop3ByOrderByAvaliacaoDesc();
        topSeries.forEach(s -> System.out.println(s.getTitulo() + " - avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorGenero() {
        System.out.println("Qual gênero?");
        var genero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(genero);
        List<Serie> seriesPorGenero = repositorio.findByGenero(categoria);
        System.out.println("Séries do gênero " + genero + ": ");
        seriesPorGenero.forEach(System.out::println);
    }

    private void seriesPorMaxTemporadasAndMinAvaliacao() {
        System.out.println("Qual o número máximo de temporadas?");
        var numMaxTemporadas = leitura.nextInt();
        System.out.println("Qual o valor mínimo da avaliação?");
        var minAvaliacao = leitura.nextDouble();
        List<Serie> seriesPorTemporadasAndAvaliacao = repositorio
                .findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(numMaxTemporadas,
                        minAvaliacao);

        System.out.println("Séries com no máximo " + numMaxTemporadas + " temporadas, e no mínimo "
                + minAvaliacao + " de avaliação: ");
        seriesPorTemporadasAndAvaliacao.forEach(
                s -> System.out.println("Título: " + s.getTitulo() + " - Número de temporadas: "
                        + s.getTotalTemporadas() + " - Avaliação: " + s.getAvaliacao()));
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Digite um trecho do título do episódio desejado");
        var trecho = leitura.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trecho);
        episodiosEncontrados.forEach(e -> System.out.println("Série: " + e.getSerie().getTitulo()
                + " - Título do episódio: " + e.getTitulo() + " - Temporada: " + e.getTemporada()
                + " - Avaliação: " + e.getAvaliacao()));
    }

    private void top5EpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.top5EpisodiosPorSerie(serie);
            topEpisodios.forEach(e -> System.out.println("Série: " + e.getSerie().getTitulo()
                    + " - Título do episódio: " + e.getTitulo() + " - Temporada: "
                    + e.getTemporada()
                    + " - Avaliação: " + e.getAvaliacao()));
        }
    }

    private void buscarEpisodiosDepoisDeUmaData() {
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            System.out.println("A partir de qual ano deseja buscar?");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serie, anoLancamento);
            episodiosAno.forEach(System.out::println);

        }
    }

}