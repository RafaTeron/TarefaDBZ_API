package br.com.rafaelAbreu.tarefaDbz.entities;

import java.io.Serializable;
import java.util.Objects;

import br.com.rafaelAbreu.tarefaDbz.entities.enums.TipoUsuario;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_usuario")
public class Usuario implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nome;
	private String email;
	private String senha;
	
	private TipoUsuario raça;

	public Usuario() {
	
	}

	public Usuario(String nome, String email, String senha, TipoUsuario raça) {
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.raça = raça;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public TipoUsuario getRaça() {
		return raça;
	}

	public void setRaça(TipoUsuario raça) {
		this.raça = raça;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(id, other.id);
	}	

}
