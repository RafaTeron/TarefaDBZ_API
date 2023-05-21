package br.com.rafaelAbreu.tarefaDbz.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.Nivel;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.TarefaStatus;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

	@Query(value = "SELECT * FROM tb_tarefa ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
	Tarefa encontrarTarefaAleatoria();

	@Query(value = "SELECT * FROM tb_tarefa WHERE usuario_id IS NULL", nativeQuery = true)
	List<Tarefa> encontrarTarefasDisponiveis();

	@Query(value = "SELECT * FROM tb_tarefa WHERE usuario_id = :id", nativeQuery = true)
	List<Tarefa> encontrarTarefasPorUsuario(@Param("id") Long idUsuario);

	@Query("SELECT t FROM Tarefa t WHERE t.status = :status")
	List<Tarefa> findByStatus(@Param("status") TarefaStatus status);
	
	@Query("SELECT t FROM Tarefa t WHERE t.status = :status AND t.nivel = :nivel")
    List<Tarefa> findByStatusAndNivel(@Param("status") TarefaStatus status, @Param("nivel") Nivel nivel);

}
