package br.com.rafaelAbreu.tarefaDbz.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rafaelAbreu.tarefaDbz.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

}
