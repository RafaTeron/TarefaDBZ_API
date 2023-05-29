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

	private boolean verificarTarefaEmAndamentoOuPendente(Tarefa tarefa, Usuario usuario) {
		boolean tarefaEmAndamento = usuario.getTarefa().stream().filter(t -> t.getNome().equals(tarefa.getNome()))
				.allMatch(t -> t.getStatus() == TarefaStatus.EM_ANDAMENTO || t.getStatus() == TarefaStatus.PENDENTE);
		return tarefaEmAndamento;
	}

	public void adicionarTarefa(Long id, int opcao) {
		Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

		if (usuarioOpt.isPresent()) {
			Usuario usuario = usuarioOpt.get();
			List<Tarefa> tarefas = usuario.getTarefa();

			if (tarefas.isEmpty()) {
				Tarefa tarefaAleatoria = tarefaRepository.TarefaAleatoriaFacil();
				if (tarefaAleatoria.getStatus() == TarefaStatus.CONCLUIDA) {

					if (verificarTarefaEmAndamentoOuPendente(tarefaAleatoria, usuario)) {
						Tarefa copiaTarefa = new Tarefa();
						copiaTarefa.setNivel(tarefaAleatoria.getNivel());
						copiaTarefa.setNome(tarefaAleatoria.getNome());
						copiaTarefa.setStatus(TarefaStatus.EM_ANDAMENTO);
						copiaTarefa.setUsuario(usuario);
						tarefaRepository.save(copiaTarefa);
						tarefas.add(copiaTarefa);
					}
				} else {
					tarefas.add(tarefaAleatoria);
					tarefaAleatoria.setStatus(TarefaStatus.EM_ANDAMENTO);
					tarefaAleatoria.setUsuario(usuario);
					tarefaRepository.save(tarefaAleatoria);
					usuario.setTarefa(tarefas);
					usuarioRepository.save(usuario);
				}
			} else {
				List<Tarefa> tarefasDisponiveis = tarefaRepository.encontrarTarefasDisponiveis();
				List<Tarefa> tarefasConcluidas = tarefaRepository.findByStatus(TarefaStatus.CONCLUIDA);
				tarefasDisponiveis.addAll(tarefasConcluidas);

				if (opcao > 0 && opcao <= tarefasDisponiveis.size()) {
					Tarefa tarefaEscolhida = tarefasDisponiveis.get(opcao - 1);

					Nivel nivelPermitido = verificarPermissaoTarefa(id);

					if (nivelPermitido != null && nivelPermitido.compareTo(tarefaEscolhida.getNivel()) >= 0) {

						if (tarefaEscolhida.getStatus() == TarefaStatus.CONCLUIDA) {

							if (verificarTarefaEmAndamentoOuPendente(tarefaEscolhida, usuario)) {
								Tarefa copiaTarefa = new Tarefa();
								copiaTarefa.setNivel(tarefaEscolhida.getNivel());
								copiaTarefa.setNome(tarefaEscolhida.getNome());
								copiaTarefa.setStatus(TarefaStatus.EM_ANDAMENTO);
								copiaTarefa.setUsuario(usuario);
								tarefaRepository.save(copiaTarefa);
								tarefas.add(copiaTarefa);
							} else {
								throw new IllegalArgumentException(
										"A tarefa selecionada está em andamento ou pendente para outro usuário.");
							}
						} else {
							tarefas.add(tarefaEscolhida);
							tarefaEscolhida.setStatus(TarefaStatus.EM_ANDAMENTO);
							tarefaEscolhida.setUsuario(usuario);
							tarefaRepository.save(tarefaEscolhida);
							usuario.setTarefa(tarefas);
							usuarioRepository.save(usuario);
						}
					} else {
						throw new IllegalArgumentException("Nivel do usuario insuficiente");
					}

					if (tarefas.size() > 1) {
						for (int i = tarefas.size() - 2; i >= 0; i--) {
						    Tarefa tarefaAnterior = tarefas.get(i);
						    if (tarefaAnterior.getStatus() != TarefaStatus.CONCLUIDA) {
						        tarefaAnterior.setStatus(TarefaStatus.PENDENTE);
						        tarefaRepository.save(tarefaAnterior);
						    }
						}
					}

				} else {
					throw new IllegalArgumentException(
							"Opção inválida! Digite um número inteiro correspondente a uma tarefa disponível.");
				}
			}

		} else {
			throw new IllegalArgumentException("Usuário não encontrado!");
		}
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

	public String verificarPermissaoTaref(Long id) {
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
