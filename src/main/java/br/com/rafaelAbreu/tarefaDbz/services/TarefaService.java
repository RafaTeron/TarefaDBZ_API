package br.com.rafaelAbreu.tarefaDbz.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.TarefaStatus;
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
		entity.setStatus(obj.getStatus());
	}

	public void deleteById(Long id) {
		tarefaRepository.deleteById(id);
	}

	public List<String> tarefasDisponiveis() {
	    List<Tarefa> tarefasDisponiveis = tarefaRepository.encontrarTarefasDisponiveis();
	    List<Tarefa> tarefasConcluidas = tarefaRepository.findByStatus(TarefaStatus.CONCLUIDA);

	    List<String> nomesTarefasDisponiveis = new ArrayList<>();

	    tarefasDisponiveis.addAll(tarefasConcluidas);

	    Set<String> nomesTarefasSet = new HashSet<>();

	    for (int i = 0; i < tarefasDisponiveis.size(); i++) {
	        Tarefa tarefaDisponivel = tarefasDisponiveis.get(i);
	        String nomeTarefaDisponivel = tarefaDisponivel.getNome();

	        boolean tarefaComMesmoNomeEncontrada = false;
	        for (Tarefa tarefa : tarefaRepository.findAll()) {
	            if (tarefa.getStatus() != null && !tarefa.getStatus().equals(TarefaStatus.CONCLUIDA)
	                    && tarefa.getNome().equals(nomeTarefaDisponivel)) {
	                tarefaComMesmoNomeEncontrada = true;
	                break;
	            }
	        }

	        if (!tarefaComMesmoNomeEncontrada && !nomesTarefasSet.contains(nomeTarefaDisponivel)) {
	            nomesTarefasSet.add(nomeTarefaDisponivel);
	            nomesTarefasDisponiveis.add((i + 1) + ". " + nomeTarefaDisponivel);
	        }
	    }

	    return nomesTarefasDisponiveis;
	}
	

	public List<Tarefa> encontrarTarefasPorStatus(Long id, TarefaStatus status) {
		List<Tarefa> tarefasFiltradas = tarefaRepository.findByStatus(status);
	    return tarefasFiltradas.stream()
	        .filter(t -> t.getUsuario().getId().equals(id))
	        .collect(Collectors.toList());
	}
}
