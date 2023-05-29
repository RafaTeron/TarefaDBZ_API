package br.com.rafaelAbreu.tarefaDbz.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.Nivel;
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
	
	public Tarefa updateStatus(Long id, Tarefa entity ,Tarefa obj) {		
		entity = tarefaRepository.getReferenceById(id);
		entity.setStatus(obj.getStatus());
		if (obj.getStatus() == TarefaStatus.CONCLUIDA && entity.getUsuario() != null) {
	        Nivel nivel = entity.getNivel();
	        entity.getUsuario().incrementarTarefaConcluida(nivel);
	    }
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
	    List<Tarefa> tarefasConcluidas = tarefaRepository.findByStatus(TarefaStatus.CONCLUIDA);

	    List<String> nomesTarefasDisponiveis = new ArrayList<>();
	    
	    tarefasDisponiveis.addAll(tarefasConcluidas);

	    Set<String> nomesTarefasSet = new HashSet<>();

	    for (int i = 0; i < tarefasDisponiveis.size(); i++) {
	        Tarefa tarefaDisponivel = tarefasDisponiveis.get(i);
	        String nomeTarefaDisponivel = tarefaDisponivel.getNome();
	        String nivelTarefaDisponivel = tarefaDisponivel.getNivel().toString();
	        
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
	            nomesTarefasDisponiveis.add((i + 1) + ". " + nomeTarefaDisponivel + "  (" + nivelTarefaDisponivel + ")");
	        }
	    }

	    List<String> nomesTarefasDisponiveisOrdenadas = nomesTarefasDisponiveis.stream()
	        .sorted(Comparator.comparing(tarefa -> {
	            String nivel = tarefa.substring(tarefa.lastIndexOf("(") + 1, tarefa.lastIndexOf(")")).trim();
	            return Nivel.valueOf(nivel.toUpperCase()).ordinal();
	        }))
	        .collect(Collectors.toList());

	    return nomesTarefasDisponiveisOrdenadas;
	}
	

	public List<Tarefa> encontrarTarefasPorStatus(Long id, TarefaStatus status) {
		List<Tarefa> tarefasFiltradas = tarefaRepository.findByStatus(status);
	    return tarefasFiltradas.stream()
	        .filter(t -> t.getUsuario().getId().equals(id))
	        .collect(Collectors.toList());
	}
}
