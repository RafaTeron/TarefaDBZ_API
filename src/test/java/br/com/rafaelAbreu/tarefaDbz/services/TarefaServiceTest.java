package br.com.rafaelAbreu.tarefaDbz.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.com.rafaelAbreu.tarefaDbz.builder.TarefaBuilder;
import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.entities.Usuario;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.Nivel;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.TarefaStatus;
import br.com.rafaelAbreu.tarefaDbz.repositories.TarefaRepository;
import br.com.rafaelAbreu.tarefaDbz.repositories.UsuarioRepository;

public class TarefaServiceTest {

	@InjectMocks
	@Spy
	private TarefaService tarefaService;

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private TarefaRepository tarefaRepository;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void encontrarTodosAsTarefas() throws Exception {
		// cenario
		List<Tarefa> tarefas = Arrays.asList(
	            TarefaBuilder.umTarefa().agora(),
	            TarefaBuilder.umTarefa().agora(),
	            TarefaBuilder.umTarefa().agora()
	    );
	    Mockito.when(tarefaRepository.findAll()).thenReturn(tarefas);

		// açao
		List<Tarefa> resultado = tarefaService.findAll();

		// verificaçao
		Assertions.assertEquals(tarefas, resultado);
	    Mockito.verify(tarefaRepository, Mockito.times(1)).findAll();
	}
 
	
	@Test
	public void encontrarTarefaPorId() throws Exception {
		// cenario
		Long id = 1L;
		Tarefa tarefa = TarefaBuilder.umTarefa().agora();
		Mockito.when(tarefaRepository.findById(id)).thenReturn(Optional.of(tarefa));

		// açao
		Tarefa resultado = tarefaService.findById(id);

		// verificaçao
		Assertions.assertEquals(tarefa, resultado);
		Mockito.verify(tarefaRepository, Mockito.times(1)).findById(id);
	}

    
	@Test
	public void insertUsuario() throws Exception {
		// cenario
		Tarefa tarefa = TarefaBuilder.umTarefa().agora();
		Tarefa tarefa2 = TarefaBuilder.umTarefa().agora();
		Mockito.when(tarefaRepository.save(tarefa)).thenReturn(tarefa2);

		// açao
		Tarefa resultado = tarefaService.insert(tarefa);
		// verificaçao
		Assertions.assertSame(tarefa2, resultado);
		Mockito.verify(tarefaRepository, Mockito.times(1)).save(tarefa);
	}

	@Test
	public void deletarUsuariosPorId() throws Exception {
		// cenario
	    Long id = 1L;

	    // açao
	    tarefaService.deleteById(id);

	    // Verificação
	    Mockito.verify(tarefaRepository, Mockito.times(1)).deleteById(id);
	}


	@Test
	public void update() throws Exception {
		//cenario
		Long id = 1L;
		Tarefa tarefa = TarefaBuilder.umTarefa().comNome("Tarefa").agora();
		Tarefa tarefaAntiga = TarefaBuilder.umTarefa().comNome("TarefaAntiga").agora();
        
        Mockito.when(tarefaRepository.getReferenceById(id)).thenReturn(tarefaAntiga);
        
        //açao
        tarefaService.update(id, tarefa);

        //Verificação 
        Mockito.verify(tarefaRepository).getReferenceById(id);
        Mockito.verify(tarefaService).updateData(tarefaAntiga, tarefa);
        Mockito.verify(tarefaRepository).save(tarefaAntiga);
        Assertions.assertEquals(tarefaAntiga, tarefa);
        Assertions.assertEquals("Tarefa", tarefa.getNome());
	}
	
	@Test
    public void updateData() {
		//cenario
		Tarefa tarefa = TarefaBuilder.umTarefa().comNome("Tarefa").agora();
		Tarefa tarefaAntiga = Mockito.mock(Tarefa.class);
        //açao
        tarefaService.updateData(tarefaAntiga, tarefa);        

        //verificaçao
        Mockito.verify(tarefaAntiga).setNome(tarefa.getNome());
        Assertions.assertEquals("Tarefa", tarefa.getNome());
	}
	
	@Test
	public void updateStatus() {
		Long id = 1L;
		Tarefa tarefa = TarefaBuilder.umTarefa()
				.comNome("Tarefa")
				.comStatus(TarefaStatus.CONCLUIDA)
				.agora();
		Tarefa tarefaAntiga = new Tarefa();
	    tarefaAntiga.setId(1L);
	    tarefaAntiga.setStatus(TarefaStatus.PENDENTE);
		
		Usuario usuarioMock = Mockito.mock(Usuario.class);
		usuarioMock.setId(1L);
	    tarefaAntiga.setUsuario(usuarioMock);
	    usuarioMock.setTarefasConcluidas(new HashMap<>());

	    Mockito.when(tarefaRepository.getReferenceById(id)).thenReturn(tarefaAntiga);
        
        tarefaService.updateStatus(id, tarefaAntiga, tarefa);
      
        Nivel nivel = tarefaAntiga.getNivel();
        Mockito.verify(tarefaRepository).save(tarefaAntiga);
        Mockito.verify(tarefaAntiga.getUsuario()).incrementarTarefaConcluida(nivel);
           
	}
	
	@Test
	public void updateStatus_StatusNotConcluded_NoIncrement() {
	    // Cenário
	    Long id = 1L;
	    Tarefa tarefa = TarefaBuilder.umTarefa()
	            .comNome("Tarefa")
	            .comStatus(TarefaStatus.CONCLUIDA)
	            .agora();
	    Tarefa tarefaAntiga = Mockito.mock(Tarefa.class);
	    tarefaAntiga.setId(1L);

	    Mockito.when(tarefaRepository.getReferenceById(id)).thenReturn(tarefaAntiga);

        tarefaService.updateStatus(id, tarefaAntiga, tarefa);

	    Mockito.verify(tarefaRepository).save(tarefaAntiga);
	}
	
	@Test
    public void encontrarTarefasPorStatus_ComTarefasFiltradas() {
        Long userId = 1L;
        TarefaStatus statusFiltrado = TarefaStatus.CONCLUIDA;

        Tarefa tarefa1 = new Tarefa();
        tarefa1.setId(1L);
        tarefa1.setStatus(statusFiltrado);
        Usuario usuario1 = new Usuario();
        usuario1.setId(userId);
        tarefa1.setUsuario(usuario1);

        Tarefa tarefa2 = new Tarefa();
        tarefa2.setId(2L);
        tarefa2.setStatus(statusFiltrado);
        
        Usuario usuario2 = new Usuario();
        usuario2.setId(userId);
        tarefa2.setUsuario(usuario2);

        List<Tarefa> tarefas = new ArrayList<>();
        tarefas.add(tarefa1);
        tarefas.add(tarefa2);

        Mockito.when(tarefaRepository.findByStatus(statusFiltrado)).thenReturn(tarefas);

        List<Tarefa> tarefasFiltradas = tarefaService.encontrarTarefasPorStatus(userId, statusFiltrado);

        Assertions.assertEquals(2, tarefasFiltradas.size());
        Assertions.assertTrue(tarefasFiltradas.contains(tarefa1));
        Assertions.assertTrue(tarefasFiltradas.contains(tarefa2));
    }

}
