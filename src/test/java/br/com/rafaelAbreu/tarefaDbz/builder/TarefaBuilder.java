package br.com.rafaelAbreu.tarefaDbz.builder;

import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.entities.Usuario;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.Nivel;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.TarefaStatus;


public class TarefaBuilder {
	private Tarefa elemento;
	private TarefaBuilder(){}

	public static TarefaBuilder umTarefa() {
		TarefaBuilder builder = new TarefaBuilder();
		inicializarDadosPadroes(builder, null);
		return builder;
	}

	public static void inicializarDadosPadroes(TarefaBuilder builder, Usuario usuario) {
		builder.elemento = new Tarefa();
		Tarefa elemento = builder.elemento;

		elemento.setId(1L);
		elemento.setNome("Controlar o Ki");
		elemento.setStatus(TarefaStatus.EM_ANDAMENTO);
		elemento.setNivel(Nivel.NORMAL);
		elemento.setUsuario(usuario);
	}

	public TarefaBuilder comId(Long param) {
		elemento.setId(param);
		return this;
	}

	public TarefaBuilder comNome(String param) {
		elemento.setNome(param);
		return this;
	}

	public TarefaBuilder comStatus(TarefaStatus param) {
		elemento.setStatus(param);
		return this;
	}

	public TarefaBuilder comNivel(Nivel param) {
		elemento.setNivel(param);
		return this;
	}

	public TarefaBuilder comUsuario(Usuario param) {
		elemento.setUsuario(param);
		return this;
	}

	public Tarefa agora() {
		return elemento;
	}
}
