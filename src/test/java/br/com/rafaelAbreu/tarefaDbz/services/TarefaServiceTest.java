package br.com.rafaelAbreu.tarefaDbz.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.com.rafaelAbreu.tarefaDbz.builder.TarefaBuilder;
import br.com.rafaelAbreu.tarefaDbz.builder.UsuarioBuilder;
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
		//cenario
		Long id = 1L;
		Tarefa tarefa = TarefaBuilder.umTarefa()
				.comNome("Tarefa")
				.comStatus(TarefaStatus.CONCLUIDA)
				.agora();
		Tarefa tarefaAntiga = TarefaBuilder.umTarefa()
				.comId(1L)
				.comStatus(TarefaStatus.PENDENTE)
				.agora();
		
		Usuario usuarioMock = Mockito.mock(Usuario.class);
		usuarioMock.setId(1L);
	    tarefaAntiga.setUsuario(usuarioMock);
	    usuarioMock.setTarefasConcluidas(new HashMap<>());

	    Mockito.when(tarefaRepository.getReferenceById(id)).thenReturn(tarefaAntiga);
	    
	    //açao
        tarefaService.updateStatus(id, tarefaAntiga, tarefa);
      
        //verificaçao
        Nivel nivel = tarefaAntiga.getNivel();
        Mockito.verify(tarefaRepository).save(tarefaAntiga);
        Mockito.verify(tarefaAntiga.getUsuario()).incrementarTarefaConcluida(nivel);           
	}
	
	@Test
	public void updateStatus_StatusDiferenteDeCONCLUIDA() {
	    // Cenário
	    Long id = 1L;
	    Tarefa tarefa = TarefaBuilder.umTarefa()
	            .comNome("Tarefa")
	            .comStatus(TarefaStatus.PENDENTE)
	            .agora();
	    Usuario usuarioMock = Mockito.mock(Usuario.class);
		usuarioMock.setId(1L);
		
	    Tarefa tarefaAntiga = Mockito.mock(Tarefa.class);
	    tarefaAntiga.setId(1L);
	    
	    Mockito.when(tarefaRepository.getReferenceById(id)).thenReturn(tarefaAntiga);

	    //açao
        tarefaService.updateStatus(id, tarefaAntiga, tarefa);

        //verificaçao
	    Mockito.verify(tarefaRepository).save(tarefaAntiga);
	}
	
	
	@Test
	public void updateStatus_UsuarioNull() {
	    // Cenário
	    Long id = 1L;
	    Tarefa tarefa = TarefaBuilder.umTarefa()
	            .comNome("Tarefa")
	            .comStatus(TarefaStatus.CONCLUIDA)
	            .agora();
	    Tarefa tarefaAntiga = Mockito.mock(Tarefa.class);
	    tarefaAntiga.setId(1L);
	    tarefaAntiga.setUsuario(null);

	    Mockito.when(tarefaRepository.getReferenceById(id)).thenReturn(tarefaAntiga);

	    //açao
        tarefaService.updateStatus(id, tarefaAntiga, tarefa);

        //verificaçao
	    Mockito.verify(tarefaRepository).save(tarefaAntiga);
	}
	
	@Test
    public void encontrarTarefasPorStatus_ComTarefasFiltradas() {
		//cenario
		Usuario usuario1 = UsuarioBuilder.umUsuario().comId(1L).agora();
	
        Tarefa tarefa1 = TarefaBuilder.umTarefa()
				.comId(1L)
				.comStatus(TarefaStatus.CONCLUIDA)
				.agora();
        tarefa1.setUsuario(usuario1);

        Tarefa tarefa2 = TarefaBuilder.umTarefa()
				.comId(2L)
				.comStatus(TarefaStatus.CONCLUIDA)
				.agora();   
        tarefa2.setUsuario(usuario1);

        List<Tarefa> tarefas = new ArrayList<>();
        tarefas.add(tarefa1);
        tarefas.add(tarefa2);

        Mockito.when(tarefaRepository.findByStatus(TarefaStatus.CONCLUIDA)).thenReturn(tarefas);
        //açao
        List<Tarefa> tarefasFiltradas = tarefaService.encontrarTarefasPorStatus(usuario1.getId(), TarefaStatus.CONCLUIDA);

        //verificaçao
        Assertions.assertEquals(2, tarefasFiltradas.size());
        Assertions.assertTrue(tarefasFiltradas.contains(tarefa1));
        Assertions.assertTrue(tarefasFiltradas.contains(tarefa2));
    }
	
	@Test
	public void ordenarPorNivel() {
		//cenario
		List<String> nomesTarefasDisponiveis = Arrays.asList(
				"Tarefa (FACIL)",
				"Outra Tarefa (DIFICIL)",
				"Mais uma Tarefa (NORMAL)",
				"Tarefa Final (FACIL)");
		
		List<String> nomesTarefasEsperados = Arrays.asList(
				"Tarefa (FACIL)",
				"Tarefa Final (FACIL)",
				"Mais uma Tarefa (NORMAL)",
				"Outra Tarefa (DIFICIL)");
		
		//açao
		List<String> nomesTarefasOrdenados = tarefaService.ordenarPorNivel(nomesTarefasDisponiveis);

		//verificaçao
		Assertions.assertEquals(nomesTarefasEsperados, nomesTarefasOrdenados);
	}
	
	@Test
    public void testEncontrarTarefaPorNomeEStatus_TarefaEncontrada() {
		//cenario
		Tarefa tarefa1 = TarefaBuilder.umTarefa()
				.comNome("Tarefa 1")
				.comStatus(TarefaStatus.PENDENTE)
				.agora();

        List<Tarefa> tarefas = new ArrayList<>();
        tarefas.add(tarefa1);

        Mockito.when(tarefaRepository.findAll()).thenReturn(tarefas);

        //açao
        boolean tarefaComMesmoNomeEncontrada = tarefaService.encontrarTarefaPorNomeEStatus(tarefa1.getNome(), true);

        //verificaçao
        Assertions.assertTrue(tarefaComMesmoNomeEncontrada);
    }

    @Test
    public void testEncontrarTarefaPorNomeEStatus_TarefaNaoEncontrada() {
    	//cenario
    	String nomeTarefaDisponivel = "Tarefa 3";

        Tarefa tarefa1 = TarefaBuilder.umTarefa()
				.comNome("Tarefa 1")
				.comStatus(TarefaStatus.PENDENTE)
				.agora();

        List<Tarefa> tarefas = new ArrayList<>();
        tarefas.add(tarefa1);

        Mockito.when(tarefaRepository.findAll()).thenReturn(tarefas);

        //açao
        boolean tarefaComMesmoNomeEncontrada = tarefaService.encontrarTarefaPorNomeEStatus(nomeTarefaDisponivel, false);

        //verificaçao
        Assertions.assertFalse(tarefaComMesmoNomeEncontrada);
    }
    
    @Test
    public void encontrarTarefaPorNomeEStatus_TarefaNaoEncontradaStatusNull() {
    	//cenario
    	String nomeTarefaDisponivel = "Tarefa 3";

        Tarefa tarefa1 = TarefaBuilder.umTarefa()
				.comNome("Tarefa 1")
				.comStatus(null)
				.agora();

        List<Tarefa> tarefas = new ArrayList<>();
        tarefas.add(tarefa1);

        Mockito.when(tarefaRepository.findAll()).thenReturn(tarefas);

        //açao 
        boolean tarefaComMesmoNomeEncontrada = tarefaService.encontrarTarefaPorNomeEStatus(nomeTarefaDisponivel, false);

        //verificaçao
        Assertions.assertFalse(tarefaComMesmoNomeEncontrada);
    }
    
    @Test
    public void encontrarTarefaPorNomeEStatus_TarefaNaoEncontradaStatusCONCLUIDA() {
    	//cenario
    	String nomeTarefaDisponivel = "Tarefa 3";

        Tarefa tarefa1 = TarefaBuilder.umTarefa()
				.comNome("Tarefa 1")
				.comStatus(TarefaStatus.CONCLUIDA)
				.agora();

        List<Tarefa> tarefas = new ArrayList<>();
        tarefas.add(tarefa1);

        Mockito.when(tarefaRepository.findAll()).thenReturn(tarefas);
        
        //açao
        boolean tarefaComMesmoNomeEncontrada = tarefaService.encontrarTarefaPorNomeEStatus(nomeTarefaDisponivel, false);
       
        //verificaçao
        Assertions.assertFalse(tarefaComMesmoNomeEncontrada);
    }
    
    @Test
    public void testAdicionarTarefaDisponivel_ListaVazia() {
    	//cenario
    	List<Tarefa> tarefasDisponiveis = new ArrayList<>();
        List<String> nomesTarefasDisponiveis = new ArrayList<>();
        Set<String> nomesTarefasSet = new HashSet<>();

        //açao
        tarefaService.adicionarTarefaDisponivel(tarefasDisponiveis, nomesTarefasDisponiveis, nomesTarefasSet);
        
        //verificaçao
        Assertions.assertTrue(nomesTarefasDisponiveis.isEmpty());
    }

    @Test
    public void testAdicionarTarefaDisponivel_TarefasNaoAdicionadas() {
    	//cenario
    	Tarefa tarefa = TarefaBuilder.umTarefa()
    			.comId(1L).comNome("Tarefa 1")
    			.comNivel(Nivel.FACIL)
    			.agora();
    	
        List<Tarefa> tarefasDisponiveis = new ArrayList<>();
        tarefasDisponiveis.add(tarefa);

        List<String> nomesTarefasDisponiveis = new ArrayList<>();
        nomesTarefasDisponiveis.add(tarefa.getNome());
                
        Set<String> nomesTarefasSet = new HashSet<>();
        nomesTarefasSet.add(tarefa.getNome());

        Mockito.when(tarefaService.encontrarTarefaPorNomeEStatus(tarefa.getNome(),false)).thenReturn(true);

        //açao
        tarefaService.adicionarTarefaDisponivel(tarefasDisponiveis, nomesTarefasDisponiveis, nomesTarefasSet);

        //verificaçao
        Assertions.assertEquals(1, nomesTarefasDisponiveis.size());
        Assertions.assertFalse(!nomesTarefasSet.contains(tarefa.getNome()));
    }

    @Test
    public void testAdicionarTarefaDisponivel_TarefasJaAdicionadas() {
    	//cenario
    	List<Tarefa> tarefasDisponiveis = new ArrayList<>();
        tarefasDisponiveis.add(TarefaBuilder.umTarefa()
        		.comNome("Tarefa 1")
        		.comNivel(Nivel.FACIL)
        		.agora());

        List<String> nomesTarefasDisponiveis = new ArrayList<>();
        nomesTarefasDisponiveis.add("1. Tarefa 1 (FACIL)");

        Set<String> nomesTarefasSet = new HashSet<>();
        nomesTarefasSet.add("Tarefa 1");
        
        //açao
        tarefaService.adicionarTarefaDisponivel(tarefasDisponiveis, nomesTarefasDisponiveis, nomesTarefasSet);
      
        //verificaçao
        Assertions.assertEquals(1, nomesTarefasDisponiveis.size());
        Assertions.assertTrue(nomesTarefasDisponiveis.contains("1. Tarefa 1 (FACIL)"));
    }
    
    @Test
    public void testAdicionarTarefaDisponivel_TarefasNaoAdicionadasDevidoAoNome() {
    	//cenario
    	Tarefa tarefa1 = TarefaBuilder.umTarefa()
    			.comId(1L)
    			.comNome("Tarefa 1")
    			.comStatus(TarefaStatus.CONCLUIDA)
    			.comNivel(Nivel.FACIL)
    			.agora();
    	
    	List<Tarefa> tarefasDisponiveis = new ArrayList<>();
        tarefasDisponiveis.add(tarefa1);

        List<String> nomesTarefasDisponiveis = new ArrayList<>();
        nomesTarefasDisponiveis.add(tarefa1.getNome());

        Set<String> nomesTarefasSet = new HashSet<>();
        nomesTarefasSet.add(tarefa1.getNome()); 
        
        boolean tarefaComMesmoNomeEncontrada = false;
        Mockito.when(tarefaService.encontrarTarefaPorNomeEStatus(tarefa1.getNome(),tarefaComMesmoNomeEncontrada)).thenReturn(true);

        //açao
        tarefaService.adicionarTarefaDisponivel(tarefasDisponiveis, nomesTarefasDisponiveis, nomesTarefasSet);
      
        //verificaçao
        Assertions.assertEquals(1, nomesTarefasDisponiveis.size());
        Mockito.verify(tarefaService).encontrarTarefaPorNomeEStatus("Tarefa 1", tarefaComMesmoNomeEncontrada);
    }
    
    @Test
    public void testTarefasDisponiveis() {
        // cenario
    	Tarefa tarefa1 = TarefaBuilder.umTarefa()
    			.comId(1L).comNome("Tarefa 1")
    			.comStatus(TarefaStatus.CONCLUIDA)
    			.comNivel(Nivel.FACIL)
    			.agora();
    	Tarefa tarefa2 = TarefaBuilder.umTarefa()
    			.comId(1L).comNome("Tarefa 2")
    			.comNivel(Nivel.FACIL)
    			.agora();

        List<Tarefa> tarefasDisponiveis = new ArrayList<>();
        tarefasDisponiveis.add(tarefa1);
        tarefasDisponiveis.add(tarefa2);

        Mockito.when(tarefaRepository.encontrarTarefasDisponiveis()).thenReturn(tarefasDisponiveis);
        Mockito.when(tarefaRepository.findByStatus(TarefaStatus.CONCLUIDA)).thenReturn(new ArrayList<>());

        //açao
        List<String> nomesTarefasDisponiveis = tarefaService.tarefasDisponiveis();

        //Verificação
        Assertions.assertEquals(2, nomesTarefasDisponiveis.size());
        Assertions.assertTrue(nomesTarefasDisponiveis.contains("1. Tarefa 1  (FACIL)")); 
        Assertions.assertTrue(nomesTarefasDisponiveis.contains("2. Tarefa 2  (FACIL)")); 
    }
}

