package com.felfired.dvd.service;

import static spark.Spark.*;
import spark.Session;
import spark.Spark;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;


import com.felfired.dvd.domain.DVD;
import com.felfired.dvd.utility.DBHandler;
import com.felfired.dvd.utility.HTMLHandler;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


public class DVDService 
{
	
	// Serve the index.html file.
    public static void getHomePage()
    {
    	get("/api/home", (req, res) -> 
    	{
    		InputStream inputStream = DVDService.class.getClassLoader().getResourceAsStream("public/index.html");
    		if(inputStream != null)
    		{
    			return inputStream;
    		}
    		else 
    		{
    			res.status(404);
    			return "404 Not Found - The requested resource was not found."; 
    		}
    	});
    }
    
    //Serve the login.html file.
    public static void getLoginPage()
    {
    	get("/api/login", (req, res) ->
    	{
    		InputStream inputStream = DVDService.class.getClassLoader().getResourceAsStream("public/login.html");
    		if(inputStream != null)
    		{
    			return inputStream;
    		}
    		else 
    		{
    			res.status(404);
    			return "404 Not Found - The requested resource was not found."; 
    		}
    	});
    }
    
    //Serve the error.html file.
    public static void getErrorPage()
    {
    	get("/api/error", (req, res) ->
    	{
    		InputStream inputStream = DVDService.class.getClassLoader().getResourceAsStream("public/error.html");
    		if(inputStream != null)
    		{
    			return inputStream;
    		}
    		else 
    		{
    			res.status(404);
    			return "404 Not Found - The requested resource was not found."; 
    		}
    	});
    }
    
    //Serve the dashboard.html file.
    public static void getDashboardPage()
    {
    	get("/api/dashboard", (req, res) -> 
    	{
    	    Session session = req.session();
    	    Boolean loggedInUser = session.attribute("loggedInUser");

    	    if (loggedInUser != null && loggedInUser) 
    	    {
    	        //User is logged in, grant access.
    	    	InputStream inputStream = DVDService.class.getClassLoader().getResourceAsStream("public/dashboard.html");
        		if(inputStream != null)
        		{
        			return inputStream;
        		}
        		else 
        		{
        			res.status(404);
        			return "404 Not Found - The requested resource was not found."; 
        		}
    	    } 
    	    else 
    	    {
    	        //User is not logged in, redirect to the login page.
    	        res.redirect("/api/login");
    	        return null;
    	    }
    	});
    }
    
    public static void authenticateUser()
    {
    	post("/api/login", (req, res) ->
    	{
    		String username = req.queryParams("username");
            String password = req.queryParams("password");
            
            if(DBHandler.existsUser(username, password))
            {
            	req.session().attribute("loggedInUser", true);
            	res.redirect("/api/dashboard");
            }
            else
            {
            	res.redirect("/api/error");
            }
            
            return null;
    	});
    }
    
    public static void logoutUser()
    {
    	get("/api/logout", (req, res) ->
    	{
    		//Clear the user's session to log them out.
    	    req.session().invalidate();
    	    
    	    //Redirect the user to the login page after logout.
    	    res.redirect("/api/login");
    	    
    	    //Return null to end the response
    	    return null; 
    	});
    }
    
    private static boolean jsonContentType(String type) 
    {
		if (type != null && !type.trim().equals("") && type.equals("application/json"))
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
    
    private static void getDVDCatalogue()
    {
    	get("/api/dvdlib", "text/html", (req, res) ->
    	{
    		Boolean loggedInUser = req.session().attribute("loggedInUser");
    		if (loggedInUser != null && loggedInUser) 
    	    {
    			ArrayList<DVD> dvdList = DBHandler.getAllDVD();
    	    	String html = HTMLHandler.createHtmlCatalogueAuth(dvdList);
        		res.type("text/html");
                return html;
    	    }
    	    else
    	    {
    	    	ArrayList<DVD> dvdList = DBHandler.getAllDVD();
    	    	String html = HTMLHandler.createHtmlCatalogueUser(dvdList);
        		res.type("text/html");
                return html;
    	    }
    	});
    }
    
    private static void getDVDHTML()
    {
    	get("/api/dvdlib/:title", "text/html", (req, res) ->
    	{
    		DVD dvd = null;
    		String encodedTitle = req.params(":title");
            String title = URLDecoder.decode(encodedTitle, "UTF-8"); // Decode the title
    		dvd = DBHandler.getDVDFromTitle(title);
    		Boolean loggedInUser = req.session().attribute("loggedInUser");

    	    if (loggedInUser != null && loggedInUser) 
    	    {
    	    	String html = HTMLHandler.createHtmlDVDAuth(dvd);
        		res.type("text/html");
                return html;
    	    }
    	    else
    	    {
    	    	String html = HTMLHandler.createHtmlDVDUser(dvd);
        		res.type("text/html");
                return html;
    	    }
    		
    	});
    }
    
    private static void searchDVD() 
    {
		get("/api/search", "text/html", (req, res) -> 
		{
			String request = req.queryParams("request");
			if (request == null || request.trim().isEmpty()) 
			{
	            res.redirect("/api/error");
	            return null;
	        }
			DVD dvd = null;
			try 
			{
				if(request.matches("[0-9]+"))
				{
					dvd = DBHandler.getDVDFromUUID(request);
				}
				else
				{
					dvd = DBHandler.getDVDFromTitle(request);
				}
				
			}
			catch(Exception e) 
			{
				res.status(500);
				return "500 Database Error - Internal database error.";
			}
			
			if (dvd != null) 
			{
				res.redirect("/api/dvdlib/" + dvd.getTitle());
				return null;
			}
			else 
			{
				res.status(404);
				return "404 Not Found - The requested resource was not found.";
			}
		});
	}
    
    private static void deleteDVD()
    {
    	post("/api/delete", (req, res) ->
    	{
    		String uuid = req.queryParams("uuid");
    		boolean hasDVD = DBHandler.existsDVDUUID(uuid);
    	    if (hasDVD == true) 
    	    {
    	        DBHandler.deleteDVD(uuid);
    	        res.status(200);
    	        res.redirect("/api/dashboard");
    	        return null;
    	    } 
    	    else 
    	    {
    	        res.status(404);
    			return "404 Not Found - The requested resource was not found.";
    	    }
    	});
    }
    
    private static void createDVD()
    {
    	post("/api/createdvd", "application/json", (req, res) ->
    	{
    		if (!jsonContentType(req.contentType())) 
    		{
    			System.out.println("Invalid JSON data.");
    			res.status(400);
    			return "400 Bad Format - Invalid JSON data.";
			}
    		try
    		{
    			boolean hasDVD = false;
    			DVD dvd = new Gson().fromJson(req.body(), DVD.class);
    			
    			if(dvd != null && dvd.getTitle() != null && dvd.getGenre() != null && dvd.getAmount() > 0)
    			{
    				hasDVD = DBHandler.existsDVD(dvd.getTitle());
    				if(hasDVD == true)
    				{
    					System.out.println("The DVD already exists.");
    					res.status(300);
    					return "300 Identical Entry - The DVD already exists.";
    				}
    				else
    				{			
    					DBHandler.createDVD(dvd);
    					System.out.println("Operation was successful.");
    					res.status(200);
    					return "200 Success - Operation was successful.";
    				}
    			}
    			else
    			{
    				System.out.println("Invalid DVD data.");
    				res.status(400);
    				return "400 Bad Format - Invalid DVD data.";
    			}
    		}
    		catch (JsonSyntaxException e)
    		{
    			System.out.println("Unknown JSON data fault.");
    			res.status(400);
    			return "400 Bad Format - Unknown JSON data fault.";
    		}
    	});
    }
    
    private static void editDVD()
    {
    	put("/api/editdvd", "application/json", (req, res) ->
    	{
    		if (!jsonContentType(req.contentType())) 
    		{
    			System.out.println("Invalid JSON data.");
    			res.status(400);
    			return "400 Bad Format - Invalid JSON data.";
			}
    		try 
    		{
    			boolean hasDVD = false;
    			DVD dvd = new Gson().fromJson(req.body(), DVD.class);

    			if(dvd != null && dvd.getUuid() > 0 && dvd.getGenre() != null && dvd.getAmount() > 0)
    			{
    				String uuid_string = Integer.toString(dvd.getUuid());
    				hasDVD = DBHandler.existsDVDUUID(uuid_string);
    				if(hasDVD == true)
    				{
    					DVD dvdtoEdit = DBHandler.getDVDFromUUID(uuid_string);
    					DBHandler.editDVD(dvdtoEdit, dvd);
    					System.out.println("Operation was successful.");
    					res.status(200);
    					return "200 Success - Operation was successful.";
    				}
    				else
    				{			
    					res.status(404);
    	    			return "404 Not Found - The requested resource was not found.";
    				}
    			}
    			else
    			{
    				System.out.println("Invalid DVD data.");
    				res.status(400);
    				return "400 Bad Format - Invalid DVD data.";
    			}
    		}
    		catch (JsonSyntaxException e)
    		{
    			System.out.println("Unknown JSON data fault.");
    			res.status(400);
    			return "400 Bad Format - Unknown JSON data fault.";
    		}
    	});
    }
    
	private static void run() 
	{
		port(8080);
		staticFileLocation("public");
		Spark.init();
		getHomePage();
		getLoginPage();
		getErrorPage();
		getDashboardPage();
		getDVDHTML();
		getDVDCatalogue();
		createDVD();
		searchDVD();
		deleteDVD();
		editDVD();
		authenticateUser();
		logoutUser();	
	}
	
	public static void main(String[] args) 
	{
		run();
	}
}