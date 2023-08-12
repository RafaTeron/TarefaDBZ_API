package br.com.rafaelAbreu.tarefaDbz.resources;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.entities.Usuario;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.Nivel;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.TarefaStatus;
import br.com.rafaelAbreu.tarefaDbz.exceptions.ErroTarefaException;
import br.com.rafaelAbreu.tarefaDbz.repositories.TarefaRepository;
import br.com.rafaelAbreu.tarefaDbz.repositories.UsuarioRepository;
import br.com.rafaelAbreu.tarefaDbz.services.TarefaService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class TarefaResourceTest {

	@Mock
	TarefaService tarefaService;
	
	@Autowired
	ObjectMapper mapper;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	TarefaRepository tarefaRepository;
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	
	private Tarefa createdTarefa;

	@BeforeEach
	void up() {
	    Tarefa tarefa = new Tarefa();
	    tarefa.setId(1L);
	    tarefa.setNome("Tarefa 1");
	    tarefa.setNivel(Nivel.FACIL);
	    tarefa.setStatus(TarefaStatus.EM_ANDAMENTO);
	    tarefa.setUsuario(null);
	    createdTarefa = tarefaRepository.save(tarefa);
	}
	
	@AfterEach
	void down() {
	    if (createdTarefa != null) {
	        tarefaRepository.delete(createdTarefa);
	    }
	}
	
	@Test
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
    void findAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tarefas"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
	
	@Test
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
    void findById() throws Exception {		
        mockMvc.perform(MockMvcRequestBuilders.get("/tarefas/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> Assertions.assertFalse(result.getResolvedException() instanceof ErroTarefaException))
                .andDo(MockMvcResultHandlers.print());
    }
	
	@Test
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
	void insert() throws Exception {
		Tarefa tarefa = TarefaBuilder.umTarefa()
				.comId(2L)
				.comNome("Tarefa 2")
				.comNivel(Nivel.FACIL)
				.comStatus(null)
				.comUsuario(null).agora();
		String tarefaRequest = mapper.writeValueAsString(tarefa);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/tarefas")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content(tarefaRequest)
				)
		        .andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
	void update() throws Exception {
		createdTarefa.setNome("Tarefa Atualizada");
		
		String tarefaRequest = mapper.writeValueAsString(createdTarefa);
		
		mockMvc.perform(MockMvcRequestBuilders.put("/tarefas/1")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content(tarefaRequest)
				)
		        .andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print());
		
	}
	
	@Test
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
	void delete() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/tarefas/1"))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
	void updateStatus() throws Exception {

		createdTarefa.setStatus(TarefaStatus.CONCLUIDA);

		String tarefaRequest = mapper.writeValueAsString(createdTarefa);

		mockMvc.perform(MockMvcRequestBuilders.put("/tarefas/1/status")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content(tarefaRequest)
				)
		        .andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CONCLUIDA"))
				.andDo(MockMvcResultHandlers.print());

	}
	
	@Test
    @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
	void listarTarefasDisponiveis() throws Exception {		
		mockMvc.perform(MockMvcRequestBuilders.get("/tarefas/listaTarefasDisponiveis"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$").isArray()).andDo(MockMvcResultHandlers.print());
	}
	
	@Test
    @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
	void encontrarTarefasPorUsuario() throws Exception {
		
		Long idUsuario = 1L;
		Usuario usuario = new Usuario();
	    usuario.setId(idUsuario);
	    usuarioRepository.save(usuario);

		Tarefa tarefa1 = TarefaBuilder.umTarefa()
				.comId(1L)
				.comNome("Tarefa 1")
				.comUsuario(usuario)
				.agora();
		Tarefa tarefa2 = TarefaBuilder.umTarefa()
				.comId(2L)
				.comNome("Tarefa 2")
				.comUsuario(usuario)
				.agora();
		tarefaRepository.save(tarefa1);
	    tarefaRepository.save(tarefa2);

        mockMvc.perform(MockMvcRequestBuilders.get("/tarefas/" + idUsuario + "/encontrarTarefasPorUsuario"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2))) // Verifica se existem duas tarefas na resposta JSON
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].nome").value(tarefa1.getNome())) // Verifica o nome da primeira tarefa
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].nome").value(tarefa2.getNome())); // Verifica o nome da segunda tarefa
	}
}
