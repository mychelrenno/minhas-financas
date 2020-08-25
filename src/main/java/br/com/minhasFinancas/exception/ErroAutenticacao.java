package br.com.minhasFinancas.exception;

public class ErroAutenticacao extends RuntimeException {

	public ErroAutenticacao(String msg) {
		super(msg);
	}
}
