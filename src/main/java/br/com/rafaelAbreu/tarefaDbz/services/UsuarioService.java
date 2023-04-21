package br.com.rafaelAbreu.tarefaDbz.services;

import java.util.List;
import java.util.Optional;

import br.com.rafaelAbreu.tarefaDbz.entities.Usuario;
import br.com.rafaelAbreu.tarefaDbz.repositories.UsuarioRepository;

public class UsuarioService {
	
	private UsuarioRepository usuarioRepository;
	
	public List<Usuario> findAll() {
		return usuarioRepository.findAll();
	}
	
	public Usuario findById(Long id) {
		Optional<Usuario> obj = usuarioRepository.findById(id);
		return obj.get();
	}
	
	public Usuario insert(Usuario obj) {
		return usuarioRepository.save(obj);
	}
	
	public Usuario update(Long id, Usuario obj) {
	    Usuario entity = usuarioRepository.getReferenceById(id);
	    updateData(entity, obj);
	    return usuarioRepository.save(entity);
	}
	
	private void updateData(Usuario entity, Usuario obj) {
		entity.setNome(obj.getNome());
		entity.setEmail(obj.getEmail());
		entity.setSenha(obj.getSenha());
	}

}
