package br.com.rafaelAbreu.tarefaDbz.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.entities.Usuario;
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
		// TODO Auto-generated method stub
		
		Usuario u1 = new Usuario("Goku" , "goku@gmail.com", "goku123" ,TipoUsuario.SAIYAJIN);
		Usuario u2 = new Usuario("Kuririn", "kuririn@gmail.com", "kuririn123", TipoUsuario.HUMANO);
		Usuario u3 = new Usuario("Piccolo", "piccolo@gmail.com", "piccolo123", TipoUsuario.NAMEKIUSEIJIN);
		
		Tarefa t1 = new Tarefa("Encontrar as Esferas do Dragão");
		Tarefa t2 = new Tarefa("Treinar com Mestre Kame");
		Tarefa t3 = new Tarefa("Combate");
		
		usuarioRepository.saveAll(Arrays.asList(u1,u2,u3));
		tarefaRepository.saveAll(Arrays.asList(t1,t2,t3));
		
		
	}

}
