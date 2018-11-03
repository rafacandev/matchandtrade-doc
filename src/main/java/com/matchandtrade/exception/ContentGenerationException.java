package com.matchandtrade.exception;

public class ContentGenerationException extends RuntimeException {

	private static final long serialVersionUID = -5955298202431584165L;
	private String message;

	public ContentGenerationException(Throwable t) {
		super(t);
		this.message = t.getMessage();
		
	}

	public ContentGenerationException(Object objectThrowingException, Throwable t) {
		message = "Error on class: " + objectThrowingException.getClass().getSimpleName() + ". Exception message: " + t.getMessage();
	}
	
	public ContentGenerationException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
