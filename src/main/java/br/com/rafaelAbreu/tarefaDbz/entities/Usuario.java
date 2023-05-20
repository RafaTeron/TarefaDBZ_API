package br.com.rafaelAbreu.tarefaDbz.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.rafaelAbreu.tarefaDbz.entities.enums.Nivel;
import br.com.rafaelAbreu.tarefaDbz.entities.enums.TipoUsuario;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.OneToMany;
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
	
	private String raca;
	
	@ElementCollection
    @CollectionTable(name = "usuario_tarefas_concluidas", joinColumns = @jakarta.persistence.JoinColumn(name = "usuario_id"))
    @MapKeyEnumerated(EnumType.STRING)
	private Map<Nivel, Integer> tarefasConcluidas;
	
	@OneToMany(mappedBy = "usuario")	
	public List<Tarefa> tarefa = new ArrayList<>();

	public void incrementarTarefaConcluida(Nivel nivel) {
	    Map<Nivel, Integer> tarefasConcluidas = getTarefasConcluidas();
	    tarefasConcluidas.put(nivel, tarefasConcluidas.getOrDefault(nivel, 0) + 1);
	}
	
	public Usuario() {
	
	}

	public Usuario(Long id, String nome, String email, String senha, TipoUsuario raca) {
		super();
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		setRaca(raca);;
		this.tarefasConcluidas = new HashMap<>();
        for (Nivel nivel : Nivel.values()) {
            this.tarefasConcluidas.put(nivel, 0);
        }
	}

	public Long getId() {
		return id;
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
		return TipoUsuario.valueOfDescricao(raca);
	}

	public void setRaca(TipoUsuario raca) {
		this.raca = raca.getDescricao();
	}

	public List<Tarefa> getTarefa() {
		return tarefa;
	}

	public void setTarefa(List<Tarefa> tarefa) {
		this.tarefa = tarefa;
	}

	public Map<Nivel, Integer> getTarefasConcluidas() {
		return tarefasConcluidas;
	}

	public void setTarefasConcluidas(Map<Nivel, Integer> tarefasConcluidas) {
		this.tarefasConcluidas = tarefasConcluidas;
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
