package br.com.rafaelAbreu.tarefaDbz.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.entities.Usuario;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.TarefaStatus;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.TipoUsuario;
import br.com.rafaelAbreu.tarefaDbz.repositories.TarefaRepository;
import br.com.rafaelAbreu.tarefaDbz.repositories.UsuarioRepository;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

	@Autowired
	private TarefaRepository tarefaRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public void run(String... args) throws Exception {
		
		Usuario u1 = new Usuario(null,"Goku" , "goku@gmail.com", "goku123" ,TipoUsuario.SAYAJIN);
		Usuario u2 = new Usuario(null,"Kuririn", "kuririn@gmail.com", "kuririn123", TipoUsuario.HUMANO);
		Usuario u3 = new Usuario(null,"Piccolo", "piccolo@gmail.com", "piccolo123", TipoUsuario.NAMEKIUSEIJIN);
		
		Tarefa t1 = new Tarefa(null,"Encontrar as Esferas do Dragão",TarefaStatus.EM_ANDAMENTO , u1);
		Tarefa t2 = new Tarefa(null,"Treinar com Mestre Kame",TarefaStatus.EM_ANDAMENTO , u3);
		Tarefa t3 = new Tarefa(null,"Combate",TarefaStatus.PENDENTE , u2);
		Tarefa t4 = new Tarefa(null, "Treinar para o Torneio do Poder", TarefaStatus.EM_ANDAMENTO, u2);
		Tarefa t5 = new Tarefa(null, "Proteger a Terra das ameaças de Freeza", null, null);
		Tarefa t6 = new Tarefa(null, "Encontrar as Dragon Balls para ressuscitar os amigos mortos", null, null);
		
		usuarioRepository.saveAll(Arrays.asList(u1,u2,u3));
		tarefaRepository.saveAll(Arrays.asList(t1,t2,t3,t4,t5,t6));
		
		
	}

}
