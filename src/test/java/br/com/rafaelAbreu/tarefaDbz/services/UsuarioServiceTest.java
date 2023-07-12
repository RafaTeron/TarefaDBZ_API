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

}
