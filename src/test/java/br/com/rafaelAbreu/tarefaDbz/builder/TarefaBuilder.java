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
		inicializarDadosPadroes(builder);
		return builder;
	}

	public static void inicializarDadosPadroes(TarefaBuilder builder) {
		builder.elemento = new Tarefa();
		Tarefa elemento = builder.elemento;

		elemento.setId(null);
		elemento.setNome(null);
		elemento.setStatus(null);
		elemento.setNivel(null);
		elemento.setUsuario(null);
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
	    Tarefa novaTarefa = new Tarefa();
	    novaTarefa.setId(elemento.getId());
	    novaTarefa.setNome(elemento.getNome());
	    novaTarefa.setStatus(elemento.getStatus());
	    novaTarefa.setNivel(elemento.getNivel());
	    novaTarefa.setUsuario(elemento.getUsuario());
	    return novaTarefa;
	}
}
