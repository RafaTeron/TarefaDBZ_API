package br.com.rafaelAbreu.tarefaDbz.entities.enums;

public enum TipoUsuario {
	
	HUMANO("HUMANO"),
	NAMEKIUSEIJIN("NAMEKIUSEIJIN"),
	SAYAJIN("SAYAJIN");
	
	private final String descricao;

	TipoUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
    
    public static TipoUsuario valueOfDescricao(String descricao) {
        for (TipoUsuario raca : TipoUsuario.values()) {
            if (raca.getDescricao().equalsIgnoreCase(descricao)) {
                return raca;
            }
        }
        throw new IllegalArgumentException("Invalid TipoUsuario description");
    }
}
