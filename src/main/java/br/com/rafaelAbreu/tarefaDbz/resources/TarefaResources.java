package br.com.rafaelAbreu.tarefaDbz.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.services.TarefaService;

@RestController
@RequestMapping(value = "/tarefas")
public class TarefaResources {
	
	@Autowired
	private TarefaService tarefaService;
	
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

	@PostMapping(value = "/{id}")
	public ResponseEntity<Tarefa> insert(@RequestBody Tarefa pessoa) {
		Tarefa obj = tarefaService.insert(pessoa);
		return ResponseEntity.ok().body(obj);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<Tarefa> update(@PathVariable Long id, @RequestBody Tarefa obj) {
		obj = tarefaService.update(id, obj);
		return ResponseEntity.ok().body(obj);
	}

}
