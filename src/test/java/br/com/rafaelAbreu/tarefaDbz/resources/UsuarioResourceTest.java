package br.com.rafaelAbreu.tarefaDbz.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
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

import br.com.rafaelAbreu.tarefaDbz.builder.TarefaBuilder;
import br.com.rafaelAbreu.tarefaDbz.builder.UsuarioBuilder;
import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.entities.Usuario;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.Nivel;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.TarefaStatus;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.TipoUsuario;
import br.com.rafaelAbreu.tarefaDbz.exceptions.ErroTarefaException;
import br.com.rafaelAbreu.tarefaDbz.repositories.TarefaRepository;
import br.com.rafaelAbreu.tarefaDbz.repositories.UsuarioRepository;
import br.com.rafaelAbreu.tarefaDbz.services.TarefaService;
import br.com.rafaelAbreu.tarefaDbz.services.UsuarioService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class UsuarioResourceTest {

	@Mock
	UsuarioService usuarioService;
	
	@Mock
	TarefaService tarefaService;
	
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
	    Tarefa tarefaFacil = TarefaBuilder
	    		.umTarefa()
	    		.comNome("Primeira Tarefa")
	    		.comNivel(Nivel.FACIL)
	    		.agora();
	    tarefaRepository.save(tarefaFacil);

	    int opcao = 1;

	    mockMvc.perform(MockMvcRequestBuilders.post("/usuarios/1/adicionarTarefa", createdUsuario.getId())
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
	    int opcao = 1;

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
	    int opcao = -1;
	    Tarefa tarefaFacil = TarefaBuilder
	    		.umTarefa()
	    		.comNome("Primeira Tarefa")
	    		.comNivel(Nivel.FACIL)
	    		.agora();
	    tarefaFacil.setUsuario(createdUsuario);
	    
	    Tarefa tarefaFacil2 = TarefaBuilder
	    		.umTarefa()
	    		.comNome("Segunda Tarefa")
	    		.comNivel(Nivel.FACIL)
	    		.agora();
	    tarefaRepository.save(tarefaFacil);
	    tarefaRepository.save(tarefaFacil2);

	    Mockito.doThrow(new ErroTarefaException("Opção inválida! Digite um número inteiro correspondente a uma tarefa disponível."))
	           .when(usuarioService).adicionarTarefaAoUsuario(Mockito.anyLong(), Mockito.anyInt());

	    mockMvc.perform(MockMvcRequestBuilders.post("/usuarios/1/adicionarTarefa")
	            .contentType(MediaType.APPLICATION_JSON)
	            .characterEncoding("UTF-8")
	            .content(Integer.toString(opcao)))
	            .andExpect(MockMvcResultMatchers.status().isBadRequest())
	            .andExpect(MockMvcResultMatchers.content().string("Opção inválida! Digite um número inteiro correspondente a uma tarefa disponível."))
	            .andDo(MockMvcResultHandlers.print());
	}

	@Test
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
    void filtrarTarefasPorStatus_DeveRetornarListaDeTarefas() throws Exception {
		List<Tarefa> tarefas = new ArrayList<>();
        Tarefa tarefaFacil = TarefaBuilder
	    		.umTarefa()
	    		.comId(1L)
	    		.comNome("Primeira Tarefa")
	    		.comStatus(TarefaStatus.PENDENTE)
	    		.agora();
	    tarefaFacil.setUsuario(createdUsuario);
	    tarefaRepository.save(tarefaFacil);
	    
	    Tarefa tarefaFacil2 = TarefaBuilder
	    		.umTarefa()
	    		.comId(2L)
	    		.comNome("Segunda Tarefa")
	    		.comStatus(TarefaStatus.CONCLUIDA)
	    		.agora();
	    tarefaFacil2.setUsuario(createdUsuario);
	    tarefaRepository.save(tarefaFacil2);
	    
	    tarefas.add(tarefaFacil);
	    tarefas.add(tarefaFacil2);
	    createdUsuario.setTarefa(tarefas);
	
        mockMvc.perform(MockMvcRequestBuilders.get("/usuarios/1/tarefas/status/PENDENTE"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
            .andDo(MockMvcResultHandlers.print());
    }	
	
	@Test
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
    void nivelUsuario() throws Exception {		
        mockMvc.perform(MockMvcRequestBuilders.get("/usuarios/1/nivel-permissao"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("FACIL"))
            .andDo(MockMvcResultHandlers.print());
    }	
}
