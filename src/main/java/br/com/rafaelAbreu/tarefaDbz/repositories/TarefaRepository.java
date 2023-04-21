package br.com.rafaelAbreu.tarefaDbz.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;

public interface TarefaRepository extends JpaRepository<Tarefa, Long>{

}
