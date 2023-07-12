package br.com.rafaelAbreu.tarefaDbz.builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.entities.Usuario;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.Nivel;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.TipoUsuario;


public class UsuarioBuilder {
	private Usuario elemento;
	private UsuarioBuilder(){}

	public static UsuarioBuilder umUsuario() {
		UsuarioBuilder builder = new UsuarioBuilder();		
		inicializarDadosPadroes(builder);
		return builder;
	}

	
	public static void inicializarDadosPadroes(UsuarioBuilder builder) {
		builder.elemento = new Usuario();
		Usuario elemento = builder.elemento;
		
		
		elemento.setId(1L);
		elemento.setNome("Gohan");
		elemento.setEmail("gohan@gmail.com");
		elemento.setSenha("gohan123");
		elemento.setRaca(TipoUsuario.SAYAJIN);
		elemento.setTarefasConcluidas(Collections.singletonMap(Nivel.FACIL, 3));
		elemento.setTarefa(List.of(TarefaBuilder.umTarefa().agora()));
	}
	
	public UsuarioBuilder comId(Long param) {
		elemento.setId(param);
		return this;
	}

	public UsuarioBuilder comNome(String param) {
		elemento.setNome(param);
		return this;
	}

	public UsuarioBuilder comEmail(String param) {
		elemento.setEmail(param);
		return this;
	}

	public UsuarioBuilder comSenha(String param) {
		elemento.setSenha(param);
		return this;
	}

	public UsuarioBuilder comRaca(TipoUsuario param) {
		elemento.setRaca(param);
		return this;
	}

	public UsuarioBuilder comTarefasConcluidas(Map<Nivel, Integer> param) {
		elemento.setTarefasConcluidas(param);
		return this;
	}

	public UsuarioBuilder comListaTarefa(Tarefa... params) {
		elemento.setTarefa(Arrays.asList(params));
		return this;
	}

	public Usuario agora() {
		return elemento;
	}
}