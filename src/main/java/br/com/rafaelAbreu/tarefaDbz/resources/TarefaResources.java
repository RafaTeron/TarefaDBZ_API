package br.com.rafaelAbreu.tarefaDbz.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.repositories.TarefaRepository;
import br.com.rafaelAbreu.tarefaDbz.services.TarefaService;

@RestController
@RequestMapping(value = "/tarefas")
public class TarefaResources {
	
	@Autowired
	private TarefaService tarefaService;
	
	@Autowired
	private TarefaRepository tarefaRepository;
	
	@GetMapping
	public ResponseEntity<List<Tarefa>> findAll() {
		List<Tarefa> list = tarefaService.findAll();
		return ResponseEntity.ok().body(list);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<Tarefa> findById(@PathVariable Long id) {
		Tarefa obj = tarefaService.findById(id);
		return ResponseEntity.ok().body(obj);
	}

	@PostMapping
	public ResponseEntity<Tarefa> insert(@RequestBody Tarefa pessoa) {
		Tarefa obj = tarefaService.insert(pessoa);
		return ResponseEntity.ok().body(obj);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<Tarefa> update(@PathVariable Long id, @RequestBody Tarefa obj) {
		obj = tarefaService.update(id, obj);
		return ResponseEntity.ok().body(obj);
	}
	
	@PutMapping("/{id}/status")
    public ResponseEntity<Tarefa> updateStatus(@PathVariable Long id, @RequestBody Tarefa updatedTarefa) {
        Tarefa existingTarefa = tarefaService.findById(id);
        if (existingTarefa == null) {
            return ResponseEntity.notFound().build();
        }

        Tarefa updatedTarefaStatus = tarefaService.updateStatus(id, existingTarefa, updatedTarefa);
        return ResponseEntity.ok(updatedTarefaStatus);
    }
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		tarefaService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/listaTarefasDisponiveis")
	public ResponseEntity<List<String>> listarTarefasDisponiveis() {
	    List<String> nomesTarefasDisponiveis = tarefaService.tarefasDisponiveis();
	    return ResponseEntity.ok()
	            .contentType(MediaType.APPLICATION_JSON)
	            .body(nomesTarefasDisponiveis);
	}
	
	@GetMapping(value = "/{id}/encontrarTarefasPorUsuario")
	public ResponseEntity<List<Tarefa>> encontrarTarefasPorUsuario(@PathVariable Long id) {
		List<Tarefa> nomesTarefasDisponiveis = tarefaRepository.encontrarTarefasPorUsuario(id);
		return ResponseEntity.ok()
	            .contentType(MediaType.APPLICATION_JSON)
	            .body(nomesTarefasDisponiveis);
	}

}
