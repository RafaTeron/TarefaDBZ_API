package br.com.rafaelAbreu.tarefaDbz.resources;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.Nivel;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.TarefaStatus;
import br.com.rafaelAbreu.tarefaDbz.repositories.TarefaRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TarefaResourceTest {

	@Autowired
	ObjectMapper mapper;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	TarefaRepository tarefaRepository;
	
	@BeforeEach
    void up() {
		Tarefa tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setNome("Tarefa 1");       
        tarefa.setNivel(Nivel.FACIL);
        tarefa.setStatus(TarefaStatus.EM_ANDAMENTO);
        tarefa.setUsuario(null);
        tarefaRepository.save(tarefa);
    }
	
	@AfterEach
    void down() {
		tarefaRepository.deleteAll();
    }
	
	@Test
    void findAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tarefas"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
