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
import br.com.rafaelAbreu.tarefaDbz.entities.Usuario;

import br.com.rafaelAbreu.tarefaDbz.entities.enums.TarefaStatus;
import br.com.rafaelAbreu.tarefaDbz.services.TarefaService;
import br.com.rafaelAbreu.tarefaDbz.services.UsuarioService;

@RestController
@RequestMapping(value = "/usuarios")
public class UsuarioResources {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private TarefaService tarefaService;
	
	@GetMapping
	public ResponseEntity<List<Usuario>> findAll() {
		List<Usuario> list = usuarioService.findAll();
		return ResponseEntity.ok().body(list);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<Usuario> findById(@PathVariable Long id) {
		Usuario obj = usuarioService.findById(id);
		return ResponseEntity.ok().body(obj);
	}

	@PostMapping
	public ResponseEntity<Usuario> insert(@RequestBody Usuario pessoa) {
		Usuario obj = usuarioService.insert(pessoa);
		return ResponseEntity.ok().body(obj);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		usuarioService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<Usuario> update(@PathVariable Long id, @RequestBody Usuario obj) {
		obj = usuarioService.update(id, obj);
		return ResponseEntity.ok().body(obj);
	}
		
	@PostMapping("/{id}/adicionarTarefa")
    public ResponseEntity<String> adicionarTarefa(@PathVariable Long id, @RequestBody int opcao) {
		try {
	        usuarioService.adicionarTarefa(id, opcao);
	        return ResponseEntity.ok("Tarefa adicionada com sucesso!");
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
    }
	
	@GetMapping("/{id}/tarefas/status/{status}")
	public ResponseEntity<List<Tarefa>> filtrarTarefasPorStatus(@PathVariable Long id, @PathVariable String status) {
	   List<Tarefa> tarefas = tarefaService.encontrarTarefasPorStatus(id ,TarefaStatus.valueOf(status.toUpperCase()));
	   return ResponseEntity.ok()
	            .contentType(MediaType.APPLICATION_JSON)
	            .body(tarefas);
	}
	
	@GetMapping("/{id}/nivel-permissao")
    public ResponseEntity<String> nivelUsuario(@PathVariable Long id) {
        String nivelString = usuarioService.nivelUsuario(id);
        return ResponseEntity.ok(nivelString);
    }
}
	


