package br.com.rafaelAbreu.tarefaDbz.exceptions;

public class ErroUsuarioException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public ErroUsuarioException(String mensagem) {
        super(mensagem);
    }

}
