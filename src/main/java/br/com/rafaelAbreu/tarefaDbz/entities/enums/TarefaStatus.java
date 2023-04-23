package br.com.rafaelAbreu.tarefaDbz.entities.enums;

public enum TarefaStatus {

	PENDENTE("Pendente"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDA("Concluída");

	private final String descricao;

	TarefaStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
    
    public static TarefaStatus valueOfDescricao(String descricao) {
        for (TarefaStatus status : TarefaStatus.values()) {
            if (status.getDescricao().equalsIgnoreCase(descricao)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid TarefaStatus description");
    }

}
