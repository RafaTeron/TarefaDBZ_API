package br.com.rafaelAbreu.tarefaDbz.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

	private boolean verificarTarefaEmAndamentoOuPendente(Tarefa tarefa, Usuario usuario) {
		boolean tarefaEmAndamento = usuario.getTarefa().stream()
		        .filter(t -> t.getNome().equals(tarefa.getNome()))
		        .allMatch(t -> t.getStatus() == TarefaStatus.EM_ANDAMENTO || t.getStatus() == TarefaStatus.PENDENTE);
	    return tarefaEmAndamento;
	}

	public void adicionarTarefa(Long id, int opcao) {
		Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

		if (usuarioOpt.isPresent()) {
			Usuario usuario = usuarioOpt.get();
			List<Tarefa> tarefas = usuario.getTarefa();

			if (tarefas.isEmpty()) {
				Tarefa tarefaAleatoria = tarefaRepository.encontrarTarefaAleatoria();
				tarefas.add(tarefaAleatoria);
				tarefaAleatoria.setStatus(TarefaStatus.EM_ANDAMENTO);
				tarefaAleatoria.setUsuario(usuario);
				tarefaRepository.save(tarefaAleatoria);
				usuario.setTarefa(tarefas);
				usuarioRepository.save(usuario);
			} else {
				List<Tarefa> tarefasDisponiveis = tarefaRepository.encontrarTarefasDisponiveis();
				List<Tarefa> tarefasConcluidas = tarefaRepository.findAll().stream()
						.filter(t -> t.getStatus() == TarefaStatus.CONCLUIDA).collect(Collectors.toList());
				tarefasDisponiveis.addAll(tarefasConcluidas);

				if (opcao > 0 && opcao <= tarefasDisponiveis.size()) {
					Tarefa tarefaEscolhida = tarefasDisponiveis.get(opcao - 1);
					if (tarefaEscolhida.getStatus() == TarefaStatus.CONCLUIDA) {
						if (verificarTarefaEmAndamentoOuPendente(tarefaEscolhida,usuario)) {
							Tarefa copiaTarefa = new Tarefa();
							copiaTarefa.setNome(tarefaEscolhida.getNome());
							copiaTarefa.setStatus(TarefaStatus.EM_ANDAMENTO);
							copiaTarefa.setUsuario(usuario);
							tarefaRepository.save(copiaTarefa);
							tarefas.add(copiaTarefa);
						} else {
							throw new IllegalArgumentException("A tarefa selecionada está em andamento ou pendente para outro usuário.");
						}
					} else {
						tarefas.add(tarefaEscolhida);
						tarefaEscolhida.setStatus(TarefaStatus.EM_ANDAMENTO);
						tarefaEscolhida.setUsuario(usuario);
						tarefaRepository.save(tarefaEscolhida);
						usuario.setTarefa(tarefas);
						usuarioRepository.save(usuario);
					}

					if (tarefas.size() > 1) {
						Tarefa tarefaAnterior = tarefas.get(tarefas.size() - 2);
						tarefaAnterior.setStatus(TarefaStatus.PENDENTE);
						tarefaRepository.save(tarefaAnterior);
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

}
