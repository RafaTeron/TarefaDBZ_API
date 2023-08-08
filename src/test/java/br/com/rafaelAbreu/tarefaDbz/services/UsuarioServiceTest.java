package br.com.rafaelAbreu.tarefaDbz.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import br.com.rafaelAbreu.tarefaDbz.builder.UsuarioBuilder;
import br.com.rafaelAbreu.tarefaDbz.entities.Tarefa;
import br.com.rafaelAbreu.tarefaDbz.entities.Usuario;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.Nivel;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.TarefaStatus;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.TipoUsuario;
import br.com.rafaelAbreu.tarefaDbz.exceptions.ErroTarefaException;
import br.com.rafaelAbreu.tarefaDbz.exceptions.ErroUsuarioException;
import br.com.rafaelAbreu.tarefaDbz.exceptions.SemUsuarioException;
import br.com.rafaelAbreu.tarefaDbz.repositories.TarefaRepository;
import br.com.rafaelAbreu.tarefaDbz.repositories.UsuarioRepository;

public class UsuarioServiceTest {

	@InjectMocks
	@Spy
	private UsuarioService usuarioService;

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private TarefaRepository tarefaRepository;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void encontrarTodosOsUsuarios() throws Exception {
		// cenario
		List<Usuario> usuarios = Arrays.asList(
	            UsuarioBuilder.umUsuario().agora(),
	            UsuarioBuilder.umUsuario().agora(),
	            UsuarioBuilder.umUsuario().agora()
	    );
	    Mockito.when(usuarioRepository.findAll()).thenReturn(usuarios);

		// açao
		List<Usuario> resultado = usuarioService.findAll();

		// verificaçao
		Assertions.assertEquals(usuarios, resultado);
	    Mockito.verify(usuarioRepository, Mockito.times(1)).findAll();
	}

	@Test
	public void encontrarUsuariosPorId() throws Exception {
		// cenario
		Long id = 1L;
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Mockito.when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

		// açao
		Usuario resultado = usuarioService.findById(id);

		// verificaçao
		Assertions.assertEquals(usuario, resultado);
		Mockito.verify(usuarioRepository, Mockito.times(1)).findById(id);
	}

	@Test
	public void insertUsuario() throws Exception {
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().agora();
		Mockito.when(usuarioRepository.save(usuario)).thenReturn(usuario2);

		// açao
		Usuario resultado = usuarioService.insert(usuario);
		// verificaçao
		Assertions.assertSame(usuario2, resultado);
		Mockito.verify(usuarioRepository, Mockito.times(1)).save(usuario);
	}

	@Test
	public void deletarUsuariosPorId() throws Exception {
		// cenario
	    Long id = 1L;

	    // açao
	    usuarioService.deleteById(id);

	    // Verificação
	    Mockito.verify(usuarioRepository, Mockito.times(1)).deleteById(id);
	}

	@Test
	public void update() throws Exception {
		//cenario
		Long id = 1L;
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
        Usuario entity = Mockito.mock(Usuario.class);
        
        Mockito.when(usuarioRepository.getReferenceById(id)).thenReturn(entity);
        Mockito.when(usuarioRepository.save(entity)).thenReturn(entity);
        
        //açao
        Usuario resultado = usuarioService.update(id, usuario);

        //Verificação 
        Mockito.verify(usuarioRepository).getReferenceById(id);
        Mockito.verify(usuarioService).updateData(entity,usuario);
        Mockito.verify(usuarioRepository).save(entity);
        Assertions.assertEquals(entity, resultado);
	}
	
	@Test
    public void updateData() {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
        Usuario entity = Mockito.mock(Usuario.class);
        
        //açao
        usuarioService.updateData(entity, usuario);        

        //verificaçao
        Mockito.verify(entity).setNome(usuario.getNome());
        Mockito.verify(entity).setEmail(usuario.getEmail());
        Mockito.verify(entity).setSenha(usuario.getSenha());
	}
	
	@Test
	public void verificarTarefaEmAndamentoOuPendente() {
		// cenario
		Tarefa tarefaEscolhida = TarefaBuilder.umTarefa()
				.comNome("TarefaEscolhida")
				.comStatus(TarefaStatus.CONCLUIDA)
				.agora();
		Tarefa tarefa1 = TarefaBuilder.umTarefa()
				.comNome("TarefaEscolhida")
				.comStatus(TarefaStatus.EM_ANDAMENTO)
				.agora();
		Tarefa tarefa2 = TarefaBuilder.umTarefa()
				.comNome("TarefaEscolhida")
				.comStatus(TarefaStatus.PENDENTE)
				.agora();
		Usuario usuario = Mockito.mock(Usuario.class);

		List<Tarefa> tarefas = Arrays.asList(tarefa1 , tarefa2, tarefaEscolhida);
		
		Mockito.when(usuario.getTarefa()).thenReturn(tarefas);

		//açao
		boolean resultado = usuarioService.verificarTarefaEmAndamentoOuPendente(tarefaEscolhida, usuario);

		//verificação 
		Assertions.assertFalse(resultado);
	}
	
	
	@Test
    public void adicionarPrimeiraTarefaSeForConcluida() throws ErroTarefaException {
		//cenario
		Long usuarioId = 1L;
	    Usuario usuario = new Usuario();
	    usuario.setId(usuarioId);

	    Tarefa tarefaAleatoria = TarefaBuilder.umTarefa()
	    		.comNome("Tarefa Aleatória")
	    		.comNivel(Nivel.FACIL)
	    		.comStatus(TarefaStatus.CONCLUIDA)
	    		.agora();

	    List<Tarefa> tarefas = new ArrayList<>();

	    Mockito.when(tarefaRepository.TarefaAleatoriaFacil()).thenReturn(tarefaAleatoria);
	    Mockito.when(usuarioService.verificarTarefaEmAndamentoOuPendente(tarefaAleatoria, usuario)).thenReturn(true);

	    //ação
	    usuarioService.primeiraTarefa(usuario, tarefas);

	    //verificação
	    Assertions.assertEquals(1, tarefas.size());

	    Tarefa tarefaAdicionada = tarefas.get(0);
	    Assertions.assertEquals(Nivel.FACIL, tarefaAdicionada.getNivel());
	    Assertions.assertEquals("Tarefa Aleatória", tarefaAdicionada.getNome());
	    Assertions.assertEquals(TarefaStatus.EM_ANDAMENTO, tarefaAdicionada.getStatus());
	    Assertions.assertSame(usuario, tarefaAdicionada.getUsuario());
	    
	    Mockito.verify(tarefaRepository).TarefaAleatoriaFacil();
        Mockito.verify(usuarioService, Mockito.times(1)).verificarTarefaEmAndamentoOuPendente(tarefaAleatoria, usuario);
	    Mockito.verify(usuarioService).criarCopiaTarefa(usuario, tarefas, tarefaAdicionada);

    }
	
	@Test
    public void adicionarPrimeiraTarefa1() throws ErroTarefaException {
		//cenario
	    Tarefa tarefaAleatoria1 = TarefaBuilder.umTarefa()
	    		.comNome("Tarefa Aleatória")
	    		.comStatus(TarefaStatus.CONCLUIDA)
	    		.comNivel(Nivel.FACIL)
	    		.agora();
		Usuario usuario = Mockito.mock(Usuario.class);

		List<Tarefa> tarefas = new ArrayList<>();

		Mockito.when(tarefaRepository.TarefaAleatoriaFacil()).thenReturn(tarefaAleatoria1);
		Mockito.when(tarefaRepository.save(Mockito.any(Tarefa.class))).thenReturn(tarefaAleatoria1);
		Mockito.when(usuarioService.verificarTarefaEmAndamentoOuPendente(tarefaAleatoria1, usuario)).thenReturn(false);

		// açao
		try {
			usuarioService.primeiraTarefa(usuario, tarefas);
			//verificaçao
			Assertions.fail();
		} catch (ErroTarefaException e) {
			Assertions.assertEquals("A tarefa selecionada está em andamento ou pendente para outro usuário.",
					e.getMessage());
		}

	}
	
	@Test
    public void adicionarPrimeiraTarefa() throws ErroTarefaException {
		//cenario
	    Tarefa tarefaAleatoria1 = TarefaBuilder.umTarefa()
	    		.comNome("Tarefa Aleatória")
	    		.comNivel(Nivel.FACIL)
	    		.agora();
	    Usuario usuario = Mockito.mock(Usuario.class);

	    List<Tarefa> tarefas = new ArrayList<>();
	    
        
        Mockito.when(tarefaRepository.TarefaAleatoriaFacil()).thenReturn(tarefaAleatoria1);
        Mockito.when(tarefaRepository.save(Mockito.any(Tarefa.class))).thenReturn(tarefaAleatoria1);

        //açao
        usuarioService.primeiraTarefa(usuario, tarefas);

        //Verificação
        Tarefa tarefaAdicionada = tarefas.get(0);
	    Assertions.assertEquals(Nivel.FACIL, tarefaAdicionada.getNivel());
	    Assertions.assertEquals("Tarefa Aleatória", tarefaAdicionada.getNome());
	    Assertions.assertEquals(TarefaStatus.EM_ANDAMENTO, tarefaAdicionada.getStatus());
	    Assertions.assertSame(usuario, tarefaAdicionada.getUsuario());
        
        Mockito.verify(usuarioService).salvarTarefa(usuario, tarefas, tarefaAleatoria1);
    }
		
	@Test
    void criarCopiaTarefa() {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
        Tarefa tarefaOriginal = TarefaBuilder.umTarefa()
        		.comNome("Tarefa Original")
	    		.comNivel(Nivel.FACIL)
	    		.agora();

        Tarefa tarefaCopia = new Tarefa();
        tarefaCopia.setNivel(tarefaOriginal.getNivel());
        tarefaCopia.setNome(tarefaOriginal.getNome());
        tarefaCopia.setStatus(TarefaStatus.EM_ANDAMENTO);
        tarefaCopia.setUsuario(usuario);

        Mockito.when(tarefaRepository.save(Mockito.any(Tarefa.class))).thenReturn(tarefaCopia);

        List<Tarefa> tarefas = new ArrayList<>();

        //açao
        usuarioService.criarCopiaTarefa(usuario, tarefas, tarefaOriginal);

        //verificaçao
        Assertions.assertEquals(1, tarefas.size());
        Assertions.assertEquals(tarefaCopia, tarefas.get(0));
    }

    @Test
    void adicionarTarefa() {
        //cenario
    	Usuario usuario = UsuarioBuilder.umUsuario().agora();
        Tarefa tarefa = TarefaBuilder.umTarefa()
        		.comNome("Tarefa Aleatória")
	    		.comStatus(TarefaStatus.CONCLUIDA)
	    		.comNivel(Nivel.FACIL)
	    		.agora();

        Mockito.when(tarefaRepository.save(Mockito.any(Tarefa.class))).thenReturn(tarefa);

        List<Tarefa> tarefaLista = new ArrayList<>();

        //açao
        usuarioService.salvarTarefa(usuario, tarefaLista, tarefa);

        //verificaçao
        Assertions.assertEquals(1, tarefaLista.size());
        Assertions.assertEquals(tarefa, tarefaLista.get(0));
        Assertions.assertEquals(TarefaStatus.EM_ANDAMENTO, tarefa.getStatus());
        Assertions.assertEquals(usuario, tarefa.getUsuario());
        Mockito.verify(usuarioRepository, Mockito.times(1)).save(usuario);
    }
    
    @Test
    void verificarPermissaoTarefaDEUS() throws SemUsuarioException {
        //cenario
        Usuario usuario = UsuarioBuilder
        		.umUsuario()
        		.comId(1L)
        		.comNome("Goku")
        		.comEmail("goku@gmail.com")        		
        		.comRaca(TipoUsuario.SAYAJIN)
        		.agora();

        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 2);
        tarefasConcluidas.put(Nivel.NORMAL, 3);
        tarefasConcluidas.put(Nivel.DIFICIL, 3);
        tarefasConcluidas.put(Nivel.MUITO_DIFICIL, 4);
        usuario.setTarefasConcluidas(tarefasConcluidas);

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        //açao
        Nivel resultado = usuarioService.verificarPermissaoTarefa(1L);

        //verificaçao
        Assertions.assertEquals(Nivel.DEUS, resultado);
    }
    
    @Test
    void verificarPermissaoTarefaMUITO_DIFICIL() throws SemUsuarioException {
    	//cenario
        Usuario usuario = UsuarioBuilder
        		.umUsuario()
        		.comId(1L)
        		.comNome("Goku")
        		.comEmail("goku@gmail.com")        		
        		.comRaca(TipoUsuario.SAYAJIN)
        		.agora();

        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 2);
        tarefasConcluidas.put(Nivel.NORMAL, 3);
        tarefasConcluidas.put(Nivel.DIFICIL, 3);
        tarefasConcluidas.put(Nivel.MUITO_DIFICIL, 2);
        usuario.setTarefasConcluidas(tarefasConcluidas);

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        //açao
        Nivel resultado = usuarioService.verificarPermissaoTarefa(1L);

        //verificaçao
        Assertions.assertEquals(Nivel.MUITO_DIFICIL, resultado);
    }
    
    @Test
    void verificarPermissaoTarefaDIFICIL() throws SemUsuarioException {
    	//cenario
        Usuario usuario = UsuarioBuilder
        		.umUsuario()
        		.comId(1L)
        		.comNome("Goku")
        		.comEmail("goku@gmail.com")        		
        		.comRaca(TipoUsuario.SAYAJIN)
        		.agora();

        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 3);
        tarefasConcluidas.put(Nivel.NORMAL, 3);
        tarefasConcluidas.put(Nivel.DIFICIL, 2);

        usuario.setTarefasConcluidas(tarefasConcluidas);

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        //açao
        Nivel resultado = usuarioService.verificarPermissaoTarefa(1L);

        //verificaçao
        Assertions.assertEquals(Nivel.DIFICIL, resultado);
    }
    
    @Test
    void verificarPermissaoTarefaNORMAL() throws SemUsuarioException {
    	//cenario
        Usuario usuario = UsuarioBuilder
        		.umUsuario()
        		.comId(1L)
        		.comNome("Goku")
        		.comEmail("goku@gmail.com")        		
        		.comRaca(TipoUsuario.SAYAJIN)
        		.agora();

        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 3);
        tarefasConcluidas.put(Nivel.NORMAL, 2);
        
        usuario.setTarefasConcluidas(tarefasConcluidas);

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        //açao
        Nivel resultado = usuarioService.verificarPermissaoTarefa(1L);

        //verificaçao
        Assertions.assertEquals(Nivel.NORMAL, resultado);
    }
    
    @Test
    void verificarPermissaoTarefaFACIL() throws SemUsuarioException {
    	//cenario
        Usuario usuario = UsuarioBuilder
        		.umUsuario()
        		.comId(1L)
        		.comNome("Goku")
        		.comEmail("goku@gmail.com")        		
        		.comRaca(TipoUsuario.SAYAJIN)
        		.agora();

        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 2);
        usuario.setTarefasConcluidas(tarefasConcluidas);

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        //açao
        Nivel resultado = usuarioService.verificarPermissaoTarefa(1L);

        //verificaçao
        Assertions.assertEquals(Nivel.FACIL, resultado);
    }
    
    @Test
    void verificarPermissaoTarefaUsuarioVAZIO() throws SemUsuarioException {
    	//cenario

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        //açao
        try {
        	usuarioService.verificarPermissaoTarefa(null);
        	//verificaçao
        	Assertions.fail();
		} catch (SemUsuarioException e) {
			Assertions.assertEquals("Usuário não encontrado", e.getMessage());
		}
    }
    
    @Test
    void nivelUsuarioDEUS() {
    	//cenario
        Usuario usuario = UsuarioBuilder
        		.umUsuario()
        		.comId(1L)
        		.comNome("Goku")
        		.comEmail("goku@gmail.com")        		
        		.comRaca(TipoUsuario.SAYAJIN)
        		.agora();

        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 3);
        tarefasConcluidas.put(Nivel.NORMAL, 3);
        tarefasConcluidas.put(Nivel.DIFICIL, 3);
        tarefasConcluidas.put(Nivel.MUITO_DIFICIL, 4);
        usuario.setTarefasConcluidas(tarefasConcluidas);

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        //açao
        String resultado = usuarioService.nivelUsuario(1L);
      
        //verificaçao
        Assertions.assertEquals("DEUS", resultado);
    }
    
    @Test
    void nivelUsuarioMUITO_DIFICIL() {
    	//cenario
        Usuario usuario = UsuarioBuilder
        		.umUsuario()
        		.comId(1L)
        		.comNome("Goku")
        		.comEmail("goku@gmail.com")        		
        		.comRaca(TipoUsuario.SAYAJIN)
        		.agora();

        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 3);
        tarefasConcluidas.put(Nivel.NORMAL, 3);
        tarefasConcluidas.put(Nivel.DIFICIL, 3);
        tarefasConcluidas.put(Nivel.MUITO_DIFICIL, 2);
        usuario.setTarefasConcluidas(tarefasConcluidas);

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        //açao
        String resultado = usuarioService.nivelUsuario(1L);
        
        //verificaçao
        Assertions.assertEquals("MUITO_DIFICIL", resultado);
    }
    
    @Test
    void nivelUsuarioDIFICIL() {
    	//cenario
        Usuario usuario = UsuarioBuilder
        		.umUsuario()
        		.comId(1L)
        		.comNome("Goku")
        		.comEmail("goku@gmail.com")        		
        		.comRaca(TipoUsuario.SAYAJIN)
        		.agora();

        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 3);
        tarefasConcluidas.put(Nivel.NORMAL, 3);
        tarefasConcluidas.put(Nivel.DIFICIL, 2);
        usuario.setTarefasConcluidas(tarefasConcluidas);

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        //açao
        String resultado = usuarioService.nivelUsuario(1L);

        //verificaçao
        Assertions.assertEquals("DIFICIL", resultado);
    }
    
    @Test
    void nivelUsuarioNORMAL() {
    	//cenario
        Usuario usuario = UsuarioBuilder
        		.umUsuario()
        		.comId(1L)
        		.comNome("Goku")
        		.comEmail("goku@gmail.com")        		
        		.comRaca(TipoUsuario.SAYAJIN)
        		.agora();

        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 3);
        tarefasConcluidas.put(Nivel.NORMAL, 2);
        
        usuario.setTarefasConcluidas(tarefasConcluidas);

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        //açao
        String resultado = usuarioService.nivelUsuario(1L);

        //verificaçao
        Assertions.assertEquals("NORMAL", resultado);
    }
    
    @Test
    void nivelUsuarioFACIL() {
    	//cenario
        Usuario usuario = UsuarioBuilder
        		.umUsuario()
        		.comId(1L)
        		.comNome("Goku")
        		.comEmail("goku@gmail.com")        		
        		.comRaca(TipoUsuario.SAYAJIN)
        		.agora();

        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 2);

        usuario.setTarefasConcluidas(tarefasConcluidas);

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        //açao
        String resultado = usuarioService.nivelUsuario(1L);
      
        //verificaçao
        Assertions.assertEquals("FACIL", resultado);
    }
    
    @Test
    void marcarTarefasAnterioresComoPendentes() {   	
    	//cenario
    	Tarefa tarefa1 = TarefaBuilder.umTarefa().comStatus(TarefaStatus.EM_ANDAMENTO).agora();
    	Tarefa tarefa2 = TarefaBuilder.umTarefa().comStatus(TarefaStatus.CONCLUIDA).agora();
    	Tarefa tarefaNova = TarefaBuilder.umTarefa().comStatus(TarefaStatus.EM_ANDAMENTO).agora();
    	
    	List<Tarefa> tarefaLista = Arrays.asList(tarefa1,tarefa2, tarefaNova);
    	
    	Mockito.when(tarefaRepository.save(Mockito.any(Tarefa.class))).thenReturn(null);
    	
    	//açao
    	usuarioService.marcarTarefasAnterioresComoPendentes(tarefaLista);
    	
    	//verificaçao
    	Mockito.verify(tarefaRepository, Mockito.times(1)).save(tarefa1);
    	Mockito.verify(tarefaRepository, Mockito.times(1)).save(tarefa2); 
    	Mockito.verify(tarefaRepository, Mockito.times(1)).save(tarefaNova);    	
    }
    
    @Test
    void marcarTarefasAnterioresComoPendentes_ListaVazia() {
        //cenario
    	Tarefa tarefa1 = TarefaBuilder.umTarefa().comStatus(TarefaStatus.EM_ANDAMENTO).agora();

        List<Tarefa> tarefaLista = Arrays.asList(tarefa1);

        Mockito.when(tarefaRepository.save(Mockito.any(Tarefa.class))).thenReturn(null);

        //açao
        usuarioService.marcarTarefasAnterioresComoPendentes(tarefaLista);

        //verificaçao
        Mockito.verify(tarefaRepository, Mockito.times(0)).save(Mockito.any(Tarefa.class));
    }
    
    @Test
    public void novaTarefa_OpcaoValida() throws ErroTarefaException, ErroUsuarioException, SemUsuarioException {
        //cenario
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        
        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 3);
        usuario.setTarefasConcluidas(tarefasConcluidas);

        Tarefa tarefa1 = TarefaBuilder.umTarefa()
        		.comId(1L)
				.comNome("Tarefa 1")
				.comStatus(TarefaStatus.CONCLUIDA)
				.comNivel(Nivel.FACIL)
				.agora();

        Tarefa tarefa2 = TarefaBuilder.umTarefa()
        		.comId(2L)
				.comNome("Tarefa 2")
				.comNivel(Nivel.NORMAL)
				.agora();

        List<Tarefa> tarefasDisponiveis = Arrays.asList(tarefa1, tarefa2);

        Mockito.when(usuarioRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(usuario));
        Mockito.when(tarefaRepository.encontrarTarefasDisponiveis()).thenReturn(tarefasDisponiveis);

        Mockito.when(usuarioService.verificarPermissaoTarefa(1L)).thenReturn(Nivel.NORMAL);

        int opcao = 2;

        //açao
        usuarioService.novaTarefa(usuario, new ArrayList<>(), opcao);

        //verificaçao
        Mockito.verify(usuarioRepository, Mockito.times(1)).save(usuario);
        Mockito.verify(tarefaRepository, Mockito.times(1)).save(tarefa2);
        Assertions.assertEquals(TarefaStatus.EM_ANDAMENTO, tarefa2.getStatus());
        Assertions.assertSame(usuario, tarefa2.getUsuario());
    }
    
    @Test
    public void novaTarefa_OpcaoInvalidaMaiorQueALista() throws ErroTarefaException, ErroUsuarioException, SemUsuarioException {
        //cenario
    	Usuario usuario = new Usuario();
        usuario.setId(1L);        
        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 3);
        usuario.setTarefasConcluidas(tarefasConcluidas);

        Tarefa tarefa1 = TarefaBuilder.umTarefa()
        		.comId(1L)
				.comNome("Tarefa 1")
				.comStatus(TarefaStatus.CONCLUIDA)
				.comNivel(Nivel.FACIL)
				.agora();

        Tarefa tarefa2 = TarefaBuilder.umTarefa()
        		.comId(2L)
				.comNome("Tarefa 2")
				.comStatus(TarefaStatus.PENDENTE)
				.comNivel(Nivel.MUITO_DIFICIL)
				.agora();

        List<Tarefa> tarefasDisponiveis = Arrays.asList(tarefa1, tarefa2);

        Mockito.when(usuarioRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(usuario));
        Mockito.when(tarefaRepository.encontrarTarefasDisponiveis()).thenReturn(tarefasDisponiveis);

        Mockito.when(usuarioService.verificarPermissaoTarefa(1L)).thenReturn(Nivel.NORMAL);

        int opcao = 4;
        //açao
        try {
        	usuarioService.novaTarefa(usuario, new ArrayList<>(), opcao);
			//verificaçao
        	Assertions.fail();
		} catch (ErroTarefaException e) {
			Assertions.assertEquals("Opção inválida! Digite um número inteiro correspondente a uma tarefa disponível.", e.getMessage());
		}
    }
    
    @Test
    public void novaTarefa_OpcaoInvalidaMenorQueZero() throws ErroTarefaException, ErroUsuarioException, SemUsuarioException {
        //cenario
    	Usuario usuario = new Usuario();
        usuario.setId(1L);        
        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 3);
        usuario.setTarefasConcluidas(tarefasConcluidas);

        Tarefa tarefa1 = TarefaBuilder.umTarefa()
        		.comId(1L)
				.comNome("Tarefa 1")
				.comStatus(TarefaStatus.CONCLUIDA)
				.comNivel(Nivel.FACIL)
				.agora();

        Tarefa tarefa2 = TarefaBuilder.umTarefa()
        		.comId(2L)
				.comNome("Tarefa 2")
				.comStatus(TarefaStatus.PENDENTE)
				.comNivel(Nivel.MUITO_DIFICIL)
				.agora();

        List<Tarefa> tarefasDisponiveis = Arrays.asList(tarefa1, tarefa2);

        Mockito.when(usuarioRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(usuario));
        Mockito.when(tarefaRepository.encontrarTarefasDisponiveis()).thenReturn(tarefasDisponiveis);

        Mockito.when(usuarioService.verificarPermissaoTarefa(1L)).thenReturn(Nivel.NORMAL);

        int opcao = 0;
        //açao
        try {
        	usuarioService.novaTarefa(usuario, new ArrayList<>(), opcao);
			//verificaçao
        	Assertions.fail();
		} catch (ErroTarefaException e) {
			Assertions.assertEquals("Opção inválida! Digite um número inteiro correspondente a uma tarefa disponível.", e.getMessage());
		}
    }
    
    @Test
    public void novaTarefa_NivelUsuarioInsuficiente() throws ErroTarefaException, ErroUsuarioException, SemUsuarioException {
        //cenario
    	Usuario usuario = new Usuario();
        usuario.setId(1L);
        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 3);
        usuario.setTarefasConcluidas(tarefasConcluidas);

        Tarefa tarefa1 = TarefaBuilder.umTarefa()
        		.comId(1L)
				.comNome("Tarefa 1")
				.comStatus(TarefaStatus.CONCLUIDA)
				.comNivel(Nivel.DIFICIL)
				.agora();

        List<Tarefa> tarefasDisponiveis = Arrays.asList(tarefa1);

        Mockito.when(usuarioRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(usuario));
        Mockito.when(tarefaRepository.encontrarTarefasDisponiveis()).thenReturn(tarefasDisponiveis);

        Mockito.when(usuarioService.verificarPermissaoTarefa(1L)).thenReturn(Nivel.FACIL);

        int opcao = 1;

        //açao
        try {
        	usuarioService.novaTarefa(usuario, new ArrayList<>(), opcao);
			//verificaçao
        	Assertions.fail();
		} catch (ErroUsuarioException e) {
			Assertions.assertEquals("Nível do usuário insuficiente.", e.getMessage());
		}
    }
    
    @Test
    public void novaTarefa_NivelUsuarioNull() throws ErroTarefaException, ErroUsuarioException, SemUsuarioException {
        //cenario
    	Usuario usuario = new Usuario();
        usuario.setId(1L);
        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(null, null);
        usuario.setTarefasConcluidas(tarefasConcluidas);

        Tarefa tarefa1 = TarefaBuilder.umTarefa()
        		.comId(1L)
				.comNome("Tarefa 1")
				.comStatus(TarefaStatus.CONCLUIDA)
				.comNivel(Nivel.DIFICIL)
				.agora();

        List<Tarefa> tarefasDisponiveis = Arrays.asList(tarefa1);

        Mockito.when(usuarioRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(usuario));
        Mockito.when(tarefaRepository.encontrarTarefasDisponiveis()).thenReturn(tarefasDisponiveis);

        int opcao = 1;

        //açao
        try {
        	usuarioService.novaTarefa(usuario, new ArrayList<>(), opcao);
			//verificaçao
        	Assertions.fail();
		} catch (ErroUsuarioException e) {
			Assertions.assertEquals("Nível do usuário insuficiente.", e.getMessage());
		}
    }
    
    @Test
    void novaTarefa_tarefaEscolhidaStatusConcluida() throws ErroTarefaException, ErroUsuarioException, SemUsuarioException {
        //cenario
        int opcao = 1;
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 3);
        usuario.setTarefasConcluidas(tarefasConcluidas);
        
        List<Tarefa> tarefas = new ArrayList<>();

        Tarefa tarefaEscolhida = TarefaBuilder.umTarefa()
        		.comId(1L)
				.comNome("Tarefa 1")
				.comStatus(TarefaStatus.CONCLUIDA)
				.comNivel(Nivel.FACIL)
				.agora();

        List<Tarefa> tarefasDisponiveis = new ArrayList<>(Arrays.asList(tarefaEscolhida));

        Mockito.when(usuarioRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(usuario));
        Mockito.when(tarefaRepository.encontrarTarefasDisponiveis()).thenReturn(tarefasDisponiveis);
        Mockito.when(usuarioService.verificarTarefaEmAndamentoOuPendente(tarefaEscolhida,usuario)).thenReturn(true);

        //açao
        usuarioService.novaTarefa(usuario, tarefas, opcao);

        //verificaçao
        Mockito.verify(usuarioService, Mockito.times(1)).verificarTarefaEmAndamentoOuPendente(tarefaEscolhida, usuario);
        Mockito.verify(usuarioService, Mockito.times(1)).criarCopiaTarefa(usuario, tarefas, tarefaEscolhida);
        Mockito.verify(usuarioService).verificarPermissaoTarefa(1L);
        
        Assertions.assertEquals(1, tarefas.size());
    }
    
    @Test
    void novaTarefa_verificarTarefaEmAndamentoOuPendenteFalse() throws ErroTarefaException, ErroUsuarioException, SemUsuarioException {
        //cenario
        int opcao = 1;
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 2);
        usuario.setTarefasConcluidas(tarefasConcluidas);
        
        List<Tarefa> tarefas = new ArrayList<>();

        Tarefa tarefaEscolhida = new Tarefa();
        tarefaEscolhida.setId(1L);
        tarefaEscolhida.setNome("Tarefa 1");
        tarefaEscolhida.setStatus(TarefaStatus.CONCLUIDA);
        tarefaEscolhida.setNivel(Nivel.FACIL);

        List<Tarefa> tarefasDisponiveis = new ArrayList<>(Arrays.asList(tarefaEscolhida));

        Mockito.when(usuarioRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(usuario));
        Mockito.when(tarefaRepository.encontrarTarefasDisponiveis()).thenReturn(tarefasDisponiveis);
        Mockito.when(usuarioService.verificarTarefaEmAndamentoOuPendente(tarefaEscolhida,usuario)).thenReturn(false);

        //açao
        try {
        	usuarioService.novaTarefa(usuario, tarefas, opcao);
			//verificaçao
        	Assertions.fail();
		} catch (ErroTarefaException e) {
			Assertions.assertEquals("A tarefa selecionada está em andamento ou pendente para outro usuário.", e.getMessage());
		}
    }
    
    @Test
    public void adicionarTarefaAoUsuario_ComUsuarioExistenteSemTarefas() throws SemUsuarioException, ErroTarefaException, ErroUsuarioException {
    	//cenario
    	Long usuarioId = 1L;
	    Usuario usuario = new Usuario();
	    usuario.setId(usuarioId); 

        Tarefa tarefaAleatoria = TarefaBuilder.umTarefa().comNome("Tarefa Aleatoria").agora();

        Mockito.when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        Mockito.when(tarefaRepository.TarefaAleatoriaFacil()).thenReturn(tarefaAleatoria);

        //açao
        usuarioService.adicionarTarefaAoUsuario(usuarioId, 1);

        //verificaçao
        Tarefa tarefaAdicionada = usuario.getTarefa().get(0);
        Assertions.assertEquals(1, usuario.getTarefa().size());
        Assertions.assertEquals(tarefaAleatoria,tarefaAdicionada);
    }
    
    @Test
    public void adicionarTarefaAoUsuario_NovaTarefa() throws SemUsuarioException, ErroTarefaException, ErroUsuarioException {
    	//cenario
    	Tarefa tarefaEscolhida = TarefaBuilder.umTarefa()
				.comNome("TarefaEscolhida")
				.comNivel(Nivel.FACIL)
				.agora();
		Tarefa tarefa1 = TarefaBuilder.umTarefa()
				.comNome("TarefaEscolhida")
				.comStatus(TarefaStatus.CONCLUIDA)
				.agora();
		Tarefa tarefa2 = TarefaBuilder.umTarefa()
				.comNome("TarefaEscolhida")
				.comStatus(TarefaStatus.CONCLUIDA)
				.agora();
		
		List<Tarefa> tarefas = new ArrayList<>();
	    tarefas.add(tarefa1);
	    tarefas.add(tarefa2);
    	
	    Usuario usuario = UsuarioBuilder.umUsuario().comId(1L).agora();
	    Map<Nivel, Integer> tarefasConcluidas = new HashMap<>();
        tarefasConcluidas.put(Nivel.FACIL, 2);
        usuario.setTarefasConcluidas(tarefasConcluidas);
	    usuario.setTarefa(tarefas);

	    List<Tarefa> tarefasDisponiveis = new ArrayList<>();
        tarefasDisponiveis.add(tarefaEscolhida);
        
        Mockito.when(tarefaRepository.encontrarTarefasDisponiveis()).thenReturn(tarefasDisponiveis);
        Mockito.when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        //açao
        usuarioService.adicionarTarefaAoUsuario(usuario.getId(), 1);

        //verificaçao
        Tarefa tarefaAdicionada = usuario.getTarefa().get(2);
        Assertions.assertEquals(3, usuario.getTarefa().size());
        Assertions.assertEquals(tarefaEscolhida,tarefaAdicionada);
    }
    
    @Test
    public void adicionarTarefaAoUsuario_SemUsuario() throws SemUsuarioException, ErroTarefaException, ErroUsuarioException {
    	//cenario
        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        //açao
        try {
			usuarioService.adicionarTarefaAoUsuario(1L, 1);
			//verificaçao
			Assertions.fail();
		} catch (SemUsuarioException e) {
			Assertions.assertEquals("Usuário não encontrado!",e.getMessage());
		}
    }
}

