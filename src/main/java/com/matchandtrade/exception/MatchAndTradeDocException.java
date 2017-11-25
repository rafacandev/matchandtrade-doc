package com.matchandtrade.exception;

public class MatchAndTradeDocException extends RuntimeException {

	private static final long serialVersionUID = -5955298202431584165L;
	private String message;

	public MatchAndTradeDocException(Throwable t) {
		super(t);
		this.message = t.getMessage();
		
	}

	public MatchAndTradeDocException(Object objectThrowingException, Throwable t) {
		message = "Error on class: " + objectThrowingException.getClass().getSimpleName() + ". Exception message: " + t.getMessage();
	}
	
	public MatchAndTradeDocException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
