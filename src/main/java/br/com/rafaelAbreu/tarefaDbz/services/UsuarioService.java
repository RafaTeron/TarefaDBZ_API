package br.com.rafaelAbreu.tarefaDbz.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.entities.Usuario;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.TarefaStatus;
import br.com.rafaelAbreu.tarefaDbz.repositories.TarefaRepository;
import br.com.rafaelAbreu.tarefaDbz.repositories.UsuarioRepository;

@Service
public class UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private TarefaRepository tarefaRepository;
	
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
	
	public void adicionarTarefa(Long id, int opcao) {
	    Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

	    if(usuarioOpt.isPresent()) {
	        Usuario usuario = usuarioOpt.get();
	        List<Tarefa> tarefas = usuario.getTarefa();

	        if(tarefas.isEmpty()) {
	            Tarefa tarefaAleatoria = tarefaRepository.encontrarTarefaAleatoria();
	            tarefas.add(tarefaAleatoria);
	            tarefaAleatoria.setStatus(TarefaStatus.EM_ANDAMENTO);
	            tarefaAleatoria.setUsuario(usuario);
	            tarefaRepository.save(tarefaAleatoria);
	            usuario.setTarefa(tarefas);
	            usuarioRepository.save(usuario);
	        } else {
	            List<Tarefa> tarefasDisponiveis = tarefaRepository.encontrarTarefasDisponiveis();
	            
	            if(opcao > 0 && opcao <= tarefasDisponiveis.size()) {
	                Tarefa tarefaEscolhida = tarefasDisponiveis.get(opcao-1);
	                tarefas.add(tarefaEscolhida);
	                tarefaEscolhida.setStatus(TarefaStatus.EM_ANDAMENTO);
	                tarefaEscolhida.setUsuario(usuario);
	                tarefaRepository.save(tarefaEscolhida);
	                usuario.setTarefa(tarefas);
	                usuarioRepository.save(usuario);

	                if (tarefas.size() > 1) {
	                    Tarefa tarefaAnterior = tarefas.get(tarefas.size() - 2);
	                    tarefaAnterior.setStatus(TarefaStatus.PENDENTE);
	                    tarefaRepository.save(tarefaAnterior);
	                }
	            } else  {
	            	throw new IllegalArgumentException("Opção inválida! Digite um número inteiro correspondente a uma tarefa disponível.");
	            }
	        }

	    } else {
	    	throw new IllegalArgumentException ("Usuário não encontrado!");
	    }
	}

}
