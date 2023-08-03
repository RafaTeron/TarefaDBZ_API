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
import br.com.rafaelAbreu.tarefaDbz.exceptions.ErroTarefaException;
import br.com.rafaelAbreu.tarefaDbz.exceptions.ErroUsuarioException;
import br.com.rafaelAbreu.tarefaDbz.exceptions.SemUsuarioException;
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

	protected void updateData(Usuario entity, Usuario obj) {
		entity.setNome(obj.getNome());
		entity.setEmail(obj.getEmail());
		entity.setSenha(obj.getSenha());
	}

	public void adicionarTarefaAoUsuario(Long id, int opcao)
			throws SemUsuarioException, ErroTarefaException, ErroUsuarioException {
		Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

		if (usuarioOpt.isPresent()) {
			Usuario usuario = usuarioOpt.get();
			List<Tarefa> tarefas = usuario.getTarefa();

			if (tarefas.isEmpty()) {
				primeiraTarefa(usuario, tarefas);
			} else {
				novaTarefa(usuario, tarefas, opcao);
			}
		} else {
			throw new SemUsuarioException("Usuário não encontrado!");
		}
	}

	protected void primeiraTarefa(Usuario usuario, List<Tarefa> tarefas) throws ErroTarefaException {
		Tarefa tarefaAleatoria = tarefaRepository.TarefaAleatoriaFacil();

		if (tarefaAleatoria.getStatus() == TarefaStatus.CONCLUIDA) {
			if (verificarTarefaEmAndamentoOuPendente(tarefaAleatoria, usuario)) {
				criarCopiaTarefa(usuario, tarefas, tarefaAleatoria);
			}else {
				throw new ErroTarefaException(
						"A tarefa selecionada está em andamento ou pendente para outro usuário.");
			}
		} else {
			salvarTarefa(usuario, tarefas, tarefaAleatoria);
		}
	}

	protected void novaTarefa(Usuario usuario, List<Tarefa> tarefas, int opcao)
			throws ErroTarefaException, ErroUsuarioException, SemUsuarioException {
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
						throw new ErroTarefaException("A tarefa selecionada está em andamento ou pendente para outro usuário.");
					}
				} else {
					salvarTarefa(usuario, tarefas, tarefaEscolhida);
				}
			} else {
				throw new ErroUsuarioException("Nível do usuário insuficiente.");
			}

			marcarTarefasAnterioresComoPendentes(tarefas);
		} else {
			throw new ErroTarefaException("Opção inválida! Digite um número inteiro correspondente a uma tarefa disponível.");
		}
	}

	protected void criarCopiaTarefa(Usuario usuario, List<Tarefa> tarefas, Tarefa tarefaOriginal) {
		Tarefa copiaTarefa = new Tarefa();
		copiaTarefa.setNivel(tarefaOriginal.getNivel());
		copiaTarefa.setNome(tarefaOriginal.getNome());
		copiaTarefa.setStatus(TarefaStatus.EM_ANDAMENTO);
		copiaTarefa.setUsuario(usuario);
		tarefaRepository.save(copiaTarefa);
		tarefas.add(copiaTarefa);
	}

	protected void salvarTarefa(Usuario usuario, List<Tarefa> tarefaLista, Tarefa tarefa) {
		tarefaLista.add(tarefa);
		tarefa.setStatus(TarefaStatus.EM_ANDAMENTO);
		tarefa.setUsuario(usuario);
		tarefaRepository.save(tarefa);
		usuario.setTarefa(tarefaLista);
		usuarioRepository.save(usuario);
	}

	protected void marcarTarefasAnterioresComoPendentes(List<Tarefa> tarefas) {
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

	protected boolean verificarTarefaEmAndamentoOuPendente(Tarefa tarefa, Usuario usuario) {
		boolean tarefaEmAndamento = usuario.getTarefa().stream().filter(t -> t.getNome().equals(tarefa.getNome()))
				.allMatch(t -> t.getStatus() == TarefaStatus.EM_ANDAMENTO || t.getStatus() == TarefaStatus.PENDENTE);
		return tarefaEmAndamento;
	}

	public Nivel verificarPermissaoTarefa(Long id) throws SemUsuarioException {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		if (usuario.isPresent()) {
	        Map<Nivel, Integer> verificarNivel = usuario.get().getTarefasConcluidas();

	        if (verificarNivel.getOrDefault(Nivel.MUITO_DIFICIL, 0) >= 3) {
	            return Nivel.DEUS;
	        } else if (verificarNivel.getOrDefault(Nivel.DIFICIL, 0) >= 3) {
	            return Nivel.MUITO_DIFICIL;
	        } else if (verificarNivel.getOrDefault(Nivel.NORMAL, 0) >= 3) {
	            return Nivel.DIFICIL;
	        } else if (verificarNivel.getOrDefault(Nivel.FACIL, 0) >= 3) {
	            return Nivel.NORMAL;
	        } else {
	            return Nivel.FACIL;
	        } 
	    } else {
	        throw new SemUsuarioException("Usuário não encontrado");
	    } 
	}

	public String nivelUsuario(Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		Map<Nivel, Integer> verificarNivel = usuario.get().getTarefasConcluidas();

		String nivelString = null;

		if (verificarNivel.getOrDefault(Nivel.FACIL, 0) < 3) {
			nivelString = "FACIL";
		} else if (verificarNivel.getOrDefault(Nivel.NORMAL, 0) < 3) {
			nivelString = "NORMAL";
		} else if (verificarNivel.getOrDefault(Nivel.DIFICIL, 0) < 3) {
			nivelString = "DIFICIL";
		} else if (verificarNivel.getOrDefault(Nivel.MUITO_DIFICIL, 0) < 3) {
			nivelString = "MUITO_DIFICIL";
		} else {
			nivelString = "DEUS";
		}
		return nivelString;
	}
}
