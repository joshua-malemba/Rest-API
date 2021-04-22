package edu.leicester.co2103.domain;

public class ErrorInfo {
	
	
	private String message;
	
	public ErrorInfo(String string) {
		message = string;
	
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
	
}
