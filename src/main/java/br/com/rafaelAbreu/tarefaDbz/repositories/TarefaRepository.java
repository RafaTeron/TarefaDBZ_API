package br.com.rafaelAbreu.tarefaDbz.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;

public interface TarefaRepository extends JpaRepository<Tarefa, Long>{

	@Query(value = "SELECT * FROM tb_tarefa ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Tarefa encontrarTarefaAleatoria();
    
    @Query(value = "SELECT * FROM tb_tarefa WHERE usuario_id IS NULL", nativeQuery = true)
    List<Tarefa> encontrarTarefasDisponiveis();
}
