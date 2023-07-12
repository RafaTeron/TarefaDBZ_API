package br.com.rafaelAbreu.tarefaDbz.exceptions;

public class ErroTarefaException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public ErroTarefaException(String mensagem) {
        super(mensagem);
    }
}
