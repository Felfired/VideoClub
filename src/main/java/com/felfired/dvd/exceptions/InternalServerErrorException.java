package com.felfired.dvd.exceptions;

public class InternalServerErrorException extends Exception
{

	private static final long serialVersionUID = 1L;

	public InternalServerErrorException() 
	{
		super();
	}
	
	public InternalServerErrorException(String message) 
	{
		super(message);
	}
}