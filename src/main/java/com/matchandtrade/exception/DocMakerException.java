package com.matchandtrade.exception;

public class DocMakerException extends RuntimeException {

	private static final long serialVersionUID = -5955298202431584165L;
	private String message;

	public DocMakerException(Throwable t) {
		super(t);
		this.message = t.getMessage();
		
	}

	public DocMakerException(Object objectThrowingException, Throwable t) {
		message = "Error on class: " + objectThrowingException.getClass().getSimpleName() + ". Exception message: " + t.getMessage();
	}
	
	public DocMakerException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
