package br.com.rafaelAbreu.tarefaDbz.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.entities.Usuario;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.Nivel;
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

	public void deleteById(Long id) {
		usuarioRepository.deleteById(id);
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

	    if (usuarioOpt.isPresent()) {
	        Usuario usuario = usuarioOpt.get();
	        List<Tarefa> tarefas = usuario.getTarefa();

	        if (tarefas.isEmpty()) {
	            adicionarPrimeiraTarefa(usuario, tarefas);
	        } else {
	            adicionarTarefaExistente(usuario, tarefas, opcao);
	        }
	    } else {
	        throw new IllegalArgumentException("Usuário não encontrado!");
	    }
	}

	private void adicionarPrimeiraTarefa(Usuario usuario, List<Tarefa> tarefas) {
	    Tarefa tarefaAleatoria = tarefaRepository.TarefaAleatoriaFacil();

	    if (tarefaAleatoria.getStatus() == TarefaStatus.CONCLUIDA) {
	        if (verificarTarefaEmAndamentoOuPendente(tarefaAleatoria, usuario)) {
	            criarCopiaTarefa(usuario, tarefas, tarefaAleatoria);
	        }
	    } else {
	        adicionarTarefa(usuario, tarefas, tarefaAleatoria);
	    }
	}

	private void adicionarTarefaExistente(Usuario usuario, List<Tarefa> tarefas, int opcao) {
	    List<Tarefa> tarefasDisponiveis = tarefaRepository.encontrarTarefasDisponiveis();
	    List<Tarefa> tarefasConcluidas = tarefaRepository.findByStatus(TarefaStatus.CONCLUIDA);
	    tarefasDisponiveis.addAll(tarefasConcluidas);

	    if (opcao > 0 && opcao <= tarefasDisponiveis.size()) {
	        Tarefa tarefaEscolhida = tarefasDisponiveis.get(opcao - 1);
	        Nivel nivelPermitido = verificarPermissaoTarefa(usuario.getId());

	        if (nivelPermitido != null && nivelPermitido.compareTo(tarefaEscolhida.getNivel()) >= 0) {
	            if (tarefaEscolhida.getStatus() == TarefaStatus.CONCLUIDA) {
	                if (verificarTarefaEmAndamentoOuPendente(tarefaEscolhida, usuario)) {
	                    criarCopiaTarefa(usuario, tarefas, tarefaEscolhida);
	                } else {
	                    throw new IllegalArgumentException("A tarefa selecionada está em andamento ou pendente para outro usuário.");
	                }
	            } else {
	                adicionarTarefa(usuario, tarefas, tarefaEscolhida);
	            }
	        } else {
	            throw new IllegalArgumentException("Nível do usuário insuficiente.");
	        }

	        marcarTarefasAnterioresComoPendentes(tarefas);
	    } else {
	        throw new IllegalArgumentException("Opção inválida! Digite um número inteiro correspondente a uma tarefa disponível.");
	    }
	}
	
	
	private void criarCopiaTarefa(Usuario usuario, List<Tarefa> tarefas, Tarefa tarefaOriginal) {
	    Tarefa copiaTarefa = new Tarefa();
	    copiaTarefa.setNivel(tarefaOriginal.getNivel());
	    copiaTarefa.setNome(tarefaOriginal.getNome());
	    copiaTarefa.setStatus(TarefaStatus.EM_ANDAMENTO);
	    copiaTarefa.setUsuario(usuario);
	    tarefaRepository.save(copiaTarefa);
	    tarefas.add(copiaTarefa);
	}

	private void adicionarTarefa(Usuario usuario, List<Tarefa> tarefas, Tarefa tarefa) {
	    tarefas.add(tarefa);
	    tarefa.setStatus(TarefaStatus.EM_ANDAMENTO);
	    tarefa.setUsuario(usuario);
	    tarefaRepository.save(tarefa);
	    usuario.setTarefa(tarefas);
	    usuarioRepository.save(usuario);
	}

	private void marcarTarefasAnterioresComoPendentes(List<Tarefa> tarefas) {
	    if (tarefas.size() > 1) {
	        for (int i = tarefas.size() - 2; i >= 0; i--) {
	            Tarefa tarefaAnterior = tarefas.get(i);
	            if (tarefaAnterior.getStatus() != TarefaStatus.CONCLUIDA) {
	                tarefaAnterior.setStatus(TarefaStatus.PENDENTE);
	                tarefaRepository.save(tarefaAnterior);
	            }
	        }
	    }
	}
	
	private boolean verificarTarefaEmAndamentoOuPendente(Tarefa tarefa, Usuario usuario) {
		boolean tarefaEmAndamento = usuario.getTarefa().stream().filter(t -> t.getNome().equals(tarefa.getNome()))
				.allMatch(t -> t.getStatus() == TarefaStatus.EM_ANDAMENTO || t.getStatus() == TarefaStatus.PENDENTE);
		return tarefaEmAndamento;
	}

	public Nivel verificarPermissaoTarefa(Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		Map<Nivel, Integer> verificarNivel = usuario.get().getTarefasConcluidas();

		if (verificarNivel.getOrDefault(Nivel.MUITO_DIFICIL, 0) >= 3) {
			return Nivel.DEUS;
		} else if (verificarNivel.getOrDefault(Nivel.DIFICIL, 0) >= 3) {
			return Nivel.MUITO_DIFICIL;
		} else if (verificarNivel.getOrDefault(Nivel.NORMAL, 0) >= 3) {
			return Nivel.DIFICIL;
		} else if (verificarNivel.getOrDefault(Nivel.FACIL, 0) >= 3) {
			return Nivel.NORMAL;
		} else if (verificarNivel.getOrDefault(Nivel.FACIL, 0) < 3) {
			return Nivel.FACIL;
		} else {
			return null;
		}
	}

	public String nivelUsuario(Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		Map<Nivel, Integer> verificarNivel = usuario.get().getTarefasConcluidas();

		String nivelString = null;

		if (verificarNivel.getOrDefault(Nivel.NORMAL, 0) >= 3) {
			nivelString = "DIFICIL";
		} else if (verificarNivel.getOrDefault(Nivel.FACIL, 0) >= 3) {
			nivelString = "NORMAL";
		} else if (verificarNivel.getOrDefault(Nivel.DIFICIL, 0) >= 3) {
			nivelString = "MUITO_DIFICIL";
		} else if (verificarNivel.getOrDefault(Nivel.MUITO_DIFICIL, 0) >= 3) {
			nivelString = "DEUS";
		} else if (verificarNivel.getOrDefault(Nivel.FACIL, 0) < 3) {
			nivelString = "FACIL";
		} else {
			nivelString = "SEM_PERMISSAO";
		}
		return nivelString;
	}
}
