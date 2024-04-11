package br.com.alura.screenmatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.screenmatch.model.Serie;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    // Derived queries
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAutor, Double avaliacao);
}
