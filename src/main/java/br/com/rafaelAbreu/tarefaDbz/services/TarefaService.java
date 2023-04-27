package br.com.rafaelAbreu.tarefaDbz.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.repositories.TarefaRepository;

@Service
public class TarefaService {
	
	@Autowired
	private TarefaRepository tarefaRepository;
	
	public List<Tarefa> findAll() {
		return tarefaRepository.findAll();
	}
	
	public Tarefa findById(Long id) {
		Optional<Tarefa> obj = tarefaRepository.findById(id);
		return obj.get();
	}
	
	public Tarefa insert(Tarefa obj) {
		return tarefaRepository.save(obj);
	}
	
	public Tarefa update(Long id, Tarefa obj) {
	    Tarefa entity = tarefaRepository.getReferenceById(id);
	    updateData(entity, obj);
	    return tarefaRepository.save(entity);
	}
	
	private void updateData(Tarefa entity, Tarefa obj) {
		entity.setNome(obj.getNome());
	}
	
	public void deleteById(Long id) {
		tarefaRepository.deleteById(id);
	}
	
	public List<String> tarefasDisponiveis() {
	    List<Tarefa> tarefasDisponiveis = tarefaRepository.encontrarTarefasDisponiveis();
	    List<String> nomesTarefasDisponiveis = new ArrayList<>();
	    for (int i = 0; i < tarefasDisponiveis.size(); i++) {
	        Tarefa tarefaDisponivel = tarefasDisponiveis.get(i);
	        nomesTarefasDisponiveis.add((i+1) + ". " + tarefaDisponivel.getNome());
	    }
	    return nomesTarefasDisponiveis;
	}

}
