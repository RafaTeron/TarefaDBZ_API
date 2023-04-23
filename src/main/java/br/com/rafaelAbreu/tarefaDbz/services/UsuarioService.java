package br.com.rafaelAbreu.tarefaDbz.services;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.entities.Usuario;
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
	
	private boolean temTarefa(Usuario usuario) {
	    List<Tarefa> tarefas = usuario.getTarefa();
	    return !tarefas.isEmpty();
	}
	
	public void adicionarTarefa(Long idUsuario) {
	    Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
	    
	    if(usuarioOpt.isPresent()) { // Verifica se o usuário existe
	        Usuario usuario = usuarioOpt.get();
	        List<Tarefa> tarefas = usuario.getTarefa();
	        
	        if(tarefas.isEmpty()) {
	            Tarefa tarefaAleatoria = tarefaRepository.encontrarTarefaAleatoria();
	            tarefas.add(tarefaAleatoria);
	            tarefaAleatoria.setUsuario(usuario);
	            tarefaRepository.save(tarefaAleatoria);
	            usuario.setTarefa(tarefas);
	            usuarioRepository.save(usuario);
	        } else if (temTarefa(usuario)) {
	            try (Scanner scanner = new Scanner(System.in)) {
					System.out.println("Escolha uma tarefa para adicionar:");
					List<Tarefa> tarefasDisponiveis = tarefaRepository.encontrarTarefasDisponiveis();
					
					for(int i = 0; i < tarefasDisponiveis.size(); i++) {
					    Tarefa tarefaDisponivel = tarefasDisponiveis.get(i);
					    System.out.println((i+1) + ". " + tarefaDisponivel.getNome());
					}
					System.out.print("Digite aqui : ");
					int opcao = scanner.nextInt();
					
					if(opcao > 0 && opcao <= tarefasDisponiveis.size()) {
					    Tarefa tarefaEscolhida = tarefasDisponiveis.get(opcao-1);
					    tarefas.add(tarefaEscolhida);
					    tarefaEscolhida.setUsuario(usuario);
					    tarefaRepository.save(tarefaEscolhida);
					    usuario.setTarefa(tarefas);
					    usuarioRepository.save(usuario);
					} else {
					    System.out.println("Opção inválida!");
					}
				}
	        }
	    } else {
	        System.out.println("Usuário não encontrado!");
	    }
	}

}
