package com.felfired.dvd.utility;

import java.util.ArrayList;

import com.felfired.dvd.domain.DVD;

public class HTMLHandler
{
	private static String createDVDRow(DVD dvd) 
	{
		String str = "<tr>";
		str += "<td>" + dvd.getUuid() + "</td>";
		str += "<td>" + dvd.getTitle() + "</td>";
		str += "<td>" + dvd.getGenre() + "</td>";
		str += "<td>" + dvd.getAmount() + "</td>";
		str += "</tr>\n";
		
		return str;
	}
	
	public static String createHtmlDVDUser(DVD dvd) 
	{
		String answer = "<html>\n";
		
		answer += "<head>\n";
		answer += "<title>" + dvd.getTitle() + "</title>\n";
		answer += "</head>\n";
		
		answer += "<body>\n";
		if (dvd != null)
		{
			answer += "<h1>" + dvd.getTitle() + "</h1>\n";
		}	
		answer += "<table border=\"1\" width=\"60%\" align=\"center\">\n";
		answer += "<caption>The requested DVD</caption>\n";
		answer += "<tr><th>Uuid</th><th>Title</th><th>Genre</th><th>Amount</th>";
		answer += "</tr>\n";
		if (dvd != null)
		{
			answer += createDVDRow(dvd);
		}	
		answer += "</table>\n";
		answer += "<br>";
		answer += "<a href='/api/home'> Back </a>";
		answer += "</body>\n";
		
		answer += "</html>";
		
		return answer;
	}
	
	public static String createHtmlDVDAuth(DVD dvd) 
	{
		String answer = "<html>\n";
		
		answer += "<head>\n";
		answer += "<title>" + dvd.getTitle() + "</title>\n";
		answer += "</head>\n";
		
		answer += "<body>\n";
		if (dvd != null)
		{
			answer += "<h1>" + dvd.getTitle() + "</h1>\n";
		}	
		answer += "<table border=\"1\" width=\"60%\" align=\"center\">\n";
		answer += "<caption>The requested DVD</caption>\n";
		answer += "<tr><th>Uuid</th><th>Title</th><th>Genre</th><th>Amount</th>";
		answer += "</tr>\n";
		if (dvd != null)
		{
			answer += createDVDRow(dvd);
		}	
		answer += "</table>\n";
		answer += "<br>";
		answer += "<a href='/api/dashboard'> Back </a>";
		answer += "</body>\n";
		
		answer += "</html>";
		
		return answer;
	}
	
	public static String createHtmlCatalogueUser(ArrayList<DVD> dvdList) 
	{
		String answer = "<html>\n";
		
		answer += "<head>\n";
		answer += "<title>Media Library</title>\n";
		answer += "</head>\n";
		
		answer += "<body>\n";
		answer += "<h1>Catalogue</h1>\n";
		answer += "<table border=\"1\" width=\"60%\" align=\"center\">\n";
		answer += "<caption>Our Catalogue</caption>\n";
		answer += "<tr><th>Uuid</th><th>Title</th><th>Genre</th><th>Amount</th>";
		answer += "</tr>\n";
		if (dvdList != null)
		{
			for (DVD dvd: dvdList)
			{
				answer += createDVDRow(dvd);
			}
		}
		answer += "</table>\n";
		answer += "<br>";
		answer += "<a href='/api/home'> Back </a>";
		
		answer += "</body>\n";	
		answer += "</html>";
		
		return answer;
	}
	
	public static String createHtmlCatalogueAuth(ArrayList<DVD> dvdList) 
	{
		String answer = "<html>\n";
		
		answer += "<head>\n";
		answer += "<title>Media Library</title>\n";
		answer += "</head>\n";
		
		answer += "<body>\n";
		answer += "<h1>Catalogue</h1>\n";
		answer += "<table border=\"1\" width=\"60%\" align=\"center\">\n";
		answer += "<caption>Our Catalogue</caption>\n";
		answer += "<tr><th>Uuid</th><th>Title</th><th>Genre</th><th>Amount</th>";
		answer += "</tr>\n";
		if (dvdList != null)
		{
			for (DVD dvd: dvdList)
			{
				answer += createDVDRow(dvd);
			}
		}
		answer += "</table>\n";
		answer += "<br>";
		answer += "<a href='/api/dashboard'> Back </a>";
		
		answer += "</body>\n";	
		answer += "</html>";
		
		return answer;
	}
}