package br.com.rafaelAbreu.tarefaDbz.services;

import java.util.Arrays;
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

import br.com.rafaelAbreu.tarefaDbz.builder.UsuarioBuilder;
import br.com.rafaelAbreu.tarefaDbz.entities.Usuario;
import br.com.rafaelAbreu.tarefaDbz.repositories.UsuarioRepository;

public class UsuarioServiceTest {

	@InjectMocks
	@Spy
	private UsuarioService usuarioService;

	@Mock
	private UsuarioRepository usuarioRepository;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void encontrarTodosOsUsuarios() throws Exception{
		//cenario
		Usuario usuario1 = UsuarioBuilder.umUsuario().agora();
	    Usuario usuario2 = UsuarioBuilder.umUsuario().agora();
        List<Usuario> usuarios = Arrays.asList(usuario1, usuario2);
        Mockito.when(usuarioRepository.findAll()).thenReturn(usuarios);
		
        //açao
		List<Usuario> resultado = usuarioService.findAll();
		
		//verificaçao
		Assertions.assertEquals(2, resultado.size());
		Assertions.assertEquals(usuario1, resultado.get(0));
		Assertions.assertEquals(usuario2, resultado.get(1));
		
		Mockito.verify(usuarioRepository, Mockito.times(1)).findAll();
		Mockito.verifyNoMoreInteractions(usuarioRepository);
	}
	
	@Test
	public void encontrarUsuariosPorId() throws Exception{
		//cenario
		 Long idUsuario = 1L;
		 Usuario usuario = UsuarioBuilder.umUsuario().comId(idUsuario).agora();
		 Mockito.when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
		
		//açao
		Optional <Usuario> resultado = Optional.of(usuarioService.findById(idUsuario));
		
		//verificaçao
		Assertions.assertTrue(resultado.isPresent());
		Assertions.assertEquals(usuario, resultado.get());
	    Mockito.verify(usuarioRepository, Mockito.times(1)).findById(idUsuario);
	}
	
}
