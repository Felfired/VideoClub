package com.felfired.dvd.domain;

public class DVD 
{

	private int uuid;
    private String title = null;
    private String genre = null;
    private int amount;
    
    public DVD() {}

    public DVD(int uuid, String title, String genre, int amount)
    {
        this.uuid = uuid;
        this.title = title;
        this.genre = genre;
        this.amount = amount;
    }
    
    public String toString()
    {
    	return "DVD: (" + uuid + ", " + title + ", " + genre + ", " + amount + ")";
    }

	public int getUuid() 
	{
		return uuid;
	}

	public void setUuid(int uuid) 
	{
		this.uuid = uuid;
	}

	public String getTitle() 
	{
		return title;
	}

	public void setTitle(String title) 
	{
		this.title = title;
	}

	public String getGenre() 
	{
		return genre;
	}

	public void setGenre(String genre) 
	{
		this.genre = genre;
	}

	public int getAmount() 
	{
		return amount;
	}

	public void setAmount(int amount) 
	{
		this.amount = amount;
	}

}