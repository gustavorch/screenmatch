package br.com.alura.screenmatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    // Derived queries
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAutor, Double avaliacao);

    List<Serie> findTop3ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    // Filtra as séries que têm até uma quantidade X de temporadas, e no mínimo Y de avaliação
    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(Integer numMaxTemporadas,
            Double avaliacaoMinima);

    // JPQL: Java Persistence Query Language
    @Query("select s from Serie s WHERE s.totalTemporadas <= :maxTemporadas AND s.avaliacao >= :avaliacaoMinima")
    List<Serie> seriesPorTemporadaEAvaliacao(Integer maxTemporadas, Double avaliacaoMinima);

    // 'ILIKE' é similar ao IgnoreCase
    // O que está entre %% representa um Contains
    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trecho%")
    List<Episodio> episodiosPorTrecho(String trecho);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> top5EpisodiosPorSerie(Serie serie);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataLancamento) >= :anoLancamento")
    List<Episodio> episodiosPorSerieEAno(Serie serie, int anoLancamento);
}
