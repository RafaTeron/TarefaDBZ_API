package br.com.rafaelAbreu.tarefaDbz.resources;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.rafaelAbreu.tarefaDbz.builder.UsuarioBuilder;
import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.entities.Usuario;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.Nivel;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.TipoUsuario;
import br.com.rafaelAbreu.tarefaDbz.exceptions.ErroTarefaException;
import br.com.rafaelAbreu.tarefaDbz.repositories.TarefaRepository;
import br.com.rafaelAbreu.tarefaDbz.repositories.UsuarioRepository;
import br.com.rafaelAbreu.tarefaDbz.services.UsuarioService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class UsuarioResourceTest {

	@Mock
	UsuarioService usuarioService;
	
	@Autowired
	ObjectMapper mapper;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	TarefaRepository tarefaRepository;
	
	
	private Usuario createdUsuario;

	@BeforeEach
	void up() {
	    Usuario usuario = new Usuario();
	    usuario.setId(1L);
	    usuario.setNome("Usuario 1");
	    usuario.setRaca(TipoUsuario.HUMANO);
	    usuario.setSenha("usuario1");
	    usuario.setEmail("usuario1@gmail.com");
	    usuario.setTarefa(null);
	    Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 2);
        usuario.setTarefasConcluidas(tarefasConcluidas);
	    createdUsuario = usuarioRepository.save(usuario);
	}
	
	@AfterEach
	void down() {
	    if (createdUsuario != null) {
	        usuarioRepository.delete(createdUsuario);
	    }
	}
	
	@Test
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
    void findAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/usuarios"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
	
	@Test
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
    void findById() throws Exception {		
        mockMvc.perform(MockMvcRequestBuilders.get("/usuarios/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
	
	@Test
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
	void insert() throws Exception {
		Usuario usuario = UsuarioBuilder.umUsuario()
				.comId(2L)
				.comNome("Usuario 2")
				.comRaca(TipoUsuario.NAMEKIUSEIJIN)
				.comSenha("usuario2")
				.comEmail("usuario2@gmail.com")
				.agora();
		String usuarioRequest = mapper.writeValueAsString(usuario);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/usuarios")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content(usuarioRequest)
				)
		        .andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
	void update() throws Exception {
		createdUsuario.setNome("Usuario Atualizada");
		
		String usuarioRequest = mapper.writeValueAsString(createdUsuario);
		
		mockMvc.perform(MockMvcRequestBuilders.put("/usuarios/1")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content(usuarioRequest)
				)
		        .andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print());		
	}
	
	@Test
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
	void delete() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/usuarios/1"))
				.andExpect(MockMvcResultMatchers.status().isNoContent())
				.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
	void adicionarTarefa() throws Exception {
		Usuario usuario = new Usuario();
		usuario.setId(2L);
	    usuario.setNome("Nome do Usuário");
	    usuarioRepository.save(usuario);

	    Tarefa tarefaFacil = new Tarefa();
	    tarefaFacil.setNome("Primeira Tarefa");
	    tarefaFacil.setNivel(Nivel.FACIL);
	    tarefaRepository.save(tarefaFacil);

	    int opcao = 1;

	    mockMvc.perform(MockMvcRequestBuilders.post("/usuarios/2/adicionarTarefa", usuario.getId())
	            .contentType(MediaType.APPLICATION_JSON)
	            .characterEncoding("UTF-8")
	            .content(Integer.toString(opcao)))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.content().string("Tarefa adicionada com sucesso!"))
	            .andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
	void adicionarTarefa_UsuarioInexistente() throws Exception {
	    int opcao = 1; // Defina a opção desejada

	    mockMvc.perform(MockMvcRequestBuilders.post("/usuarios/99/adicionarTarefa")
	            .contentType(MediaType.APPLICATION_JSON)
	            .characterEncoding("UTF-8")
	            .content(Integer.toString(opcao)))
	            .andExpect(MockMvcResultMatchers.status().isBadRequest())
	            .andExpect(MockMvcResultMatchers.content().string("Usuário não encontrado!"))
	            .andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
	void adicionarTarefa_ErroTarefa() throws Exception {
	    int opcao = -1; // Defina a opção desejada
	    
	    Usuario usuario = new Usuario();
		usuario.setId(2L);
	    usuario.setNome("Nome do Usuário");
	    usuarioRepository.save(usuario);

	    Tarefa tarefaFacil = new Tarefa();
	    tarefaFacil.setNome("Primeira Tarefa");
	    tarefaFacil.setNivel(Nivel.FACIL);
	    tarefaFacil.setUsuario(usuario);
	    
	    Tarefa tarefaFacil2 = new Tarefa();
	    tarefaFacil.setNome("Primeira Tarefa");
	    tarefaFacil.setNivel(Nivel.FACIL);
	    tarefaRepository.save(tarefaFacil);
	    tarefaRepository.save(tarefaFacil2);

	    // Simule um cenário onde ocorre um erro relacionado à tarefa
	    Mockito.doThrow(new ErroTarefaException("Opção inválida! Digite um número inteiro correspondente a uma tarefa disponível."))
	           .when(usuarioService).adicionarTarefaAoUsuario(Mockito.anyLong(), Mockito.anyInt());

	    mockMvc.perform(MockMvcRequestBuilders.post("/usuarios/2/adicionarTarefa")
	            .contentType(MediaType.APPLICATION_JSON)
	            .characterEncoding("UTF-8")
	            .content(Integer.toString(opcao)))
	            .andExpect(MockMvcResultMatchers.status().isBadRequest())
	            .andExpect(MockMvcResultMatchers.content().string("Opção inválida! Digite um número inteiro correspondente a uma tarefa disponível."))
	            .andDo(MockMvcResultHandlers.print());
	}
	
	
}
