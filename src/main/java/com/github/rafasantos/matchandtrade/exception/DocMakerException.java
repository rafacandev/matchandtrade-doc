package com.github.rafasantos.matchandtrade.exception;

public class DocMakerException extends RuntimeException {

	private static final long serialVersionUID = -5955298202431584165L;
	private String message;

	public DocMakerException(Exception e) {
		super(e);
		this.message = e.getMessage();
		
	}

	public DocMakerException(Object objectThrowingException, Exception e) {
		message = "Error on class: " + objectThrowingException.getClass().getSimpleName() + ". Exception message: " + e.getMessage();
	}
	
	public DocMakerException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
