package model.exceptions;

import java.util.HashMap;
import java.util.Map;

// excessão para erros de entrada de dados no formulario
public class ValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	// lista os textFilds e suas respectivas msg de erro
	private Map<String, String> errors = new HashMap<String, String>();

	public ValidationException(String msg) {
		super(msg);
	}
	
	// metodo para exibir as respectivas mensagens
	public Map<String, String> getErrors() {
		return errors;
	}
	
	// metodo para adicionas novas mensagens
	public void addErrors(String fieldName, String errorMessage) {
		errors.put(fieldName, errorMessage);
	}
}
