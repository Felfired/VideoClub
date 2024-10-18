package com.felfired.dvd.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.felfired.dvd.configuration.DBProperties;
import com.felfired.dvd.domain.DVD;
import com.felfired.dvd.exceptions.InternalServerErrorException;

public final class DBHandler 
{	
	private DBHandler() {}
	
	private static Connection getConnection() throws Exception
	{
		try 
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(  
			"jdbc:mysql://" + DBProperties.getDbHost() + ":" + DBProperties.getDbPort() + "/dvd", DBProperties.getLogin(), DBProperties.getPwd());
			return con;
		}
		catch(Exception e) 
		{
			throw e;
		}
	}
	
	public static boolean existsUser(String username, String password) throws Exception 
	{
		boolean hasUser = false;
		Connection con = getConnection();
		try 
		{
			Statement stmt = con.createStatement();
			String query = "select * from staff where username='" + username + "' and password='" + password + "'";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) 
			{
				hasUser = true;
			}
			con.close();
		}
		catch(Exception e) 
		{
			throw new InternalServerErrorException("An internal error prevented from authenticating user");
		}
		return hasUser;
	}
	
	public static boolean createDVD(DVD dvd) throws Exception
	{
		Connection con = getConnection();
		int rowCount = 0;
		boolean created = false;
		try 
		{
			Statement stmt = con.createStatement();
			String query = "SELECT COUNT(*) FROM dvdlib";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) 
			{
			    rowCount = rs.getInt(1);
			}
			
			if(rowCount == 0)
			{
				query = "insert into dvdlib(uuid,title,genre,amount) "
						+ "values(" + "1" + ",'" + dvd.getTitle() + "','" +
						dvd.getGenre() + "'," + dvd.getAmount() + ")";
				stmt.execute(query);
				if (stmt.getUpdateCount() == 1) 
				{
					System.out.println("Database entry added.");
					created = true;
				}
			}
			else if(rowCount > 0)
			{
				query ="SELECT uuid FROM dvdlib WHERE uuid = (SELECT MAX(uuid) FROM dvdlib)";
				rs = stmt.executeQuery(query);
				if(rs.next())
				{
					int uuid = rs.getInt(1) + 1;
					query = "insert into dvdlib(uuid,title,genre,amount) "
							+ "values(" + uuid + ",'" + dvd.getTitle() + "','" +
							dvd.getGenre() + "'," + dvd.getAmount() + ")";
					stmt.execute(query);
					if (stmt.getUpdateCount() == 1) 
					{
						System.out.println("Database entry added.");
						created = true;
					}
				}
			}
			con.close();
		}
		catch(Exception e) 
		{
			throw new InternalServerErrorException("Internal database error. Unable to access DVD data.");
		}
		
		return created;
	}
	
	public static boolean existsDVD(String title) throws Exception 
	{
		boolean hasDVD = false;
		Connection con = getConnection();
		try 
		{
			Statement stmt = con.createStatement();
			String query = "select * from dvdlib where title='" + title + "'";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) 
			{
				hasDVD = true;
			}
			
			con.close();
		}
		catch(Exception e) 
		{
			throw new InternalServerErrorException("Internal database error. Unable to access DVD data.");
		}
		return hasDVD;
	}
	
	public static boolean existsDVDUUID(String uuid) throws Exception 
	{
		boolean hasDVD = false;
		Connection con = getConnection();
		try 
		{
			Statement stmt = con.createStatement();
			String query = "select * from dvdlib where uuid=" + uuid + "";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) 
			{
				hasDVD = true;
			}
			
			con.close();
		}
		catch(Exception e) 
		{
			throw new InternalServerErrorException("Internal database error. Unable to access DVD data.");
		}
		return hasDVD;
	}
	
	public static DVD getDVDFromTitle(String title) throws Exception
	{
		DVD dvd = null;
		Connection con = getConnection();
		try 
		{
			Statement stmt = con.createStatement();
			String query = "select * from dvdlib where title='" + title + "'";
			ResultSet rs = stmt.executeQuery(query);
			System.out.println("Query was successful.");
			if (rs.next()) 
			{
				dvd = getDVDFromRS(rs);
			}
			System.out.println("Processed query results.");
			
			con.close();
		}
		catch(Exception e) 
		{
			throw new InternalServerErrorException("Internal database error. Unable to access DVD data.");
		}
		return dvd;
	}
	
	public static DVD getDVDFromUUID(String uuid) throws Exception
	{
		DVD dvd = null;
		int uuid_int = Integer.parseInt(uuid);
		Connection con = getConnection();
		try 
		{
			Statement stmt = con.createStatement();
			String query = "select * from dvdlib where uuid=" + uuid_int + "";
			ResultSet rs = stmt.executeQuery(query);
			System.out.println("Query was successful.");
			if (rs.next()) 
			{
				dvd = getDVDFromRS(rs);
			}
			System.out.println("Processed query results.");
			
			con.close();
		}
		catch(Exception e) 
		{
			throw new InternalServerErrorException("Internal database error. Unable to access DVD data.");
		}
		return dvd;
	}
	
	public static ArrayList<DVD> getAllDVD() throws Exception
	{
		ArrayList<DVD> dvdList = null;
		Connection con = getConnection();
		try
		{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from dvdlib");
			System.out.println("Query was successful.");
			if(rs.next())
			{
				dvdList = new ArrayList<DVD>();
				dvdList.add(getDVDFromRS(rs));
				while (rs.next()) 
				{
					dvdList.add(getDVDFromRS(rs));
				}
			}
		}
		catch(Exception e)
		{
			throw new InternalServerErrorException("Internal database error. Unable to access DVD data.");
		}
		return dvdList;
	}
	
	public static boolean deleteDVD(String uuid) throws Exception
	{
		boolean deleted = false;
		int uuid_int = Integer.parseInt(uuid);
		Connection con = getConnection();
		try 
		{
			Statement stmt = con.createStatement();
			String query = "delete from dvdlib where uuid=" + uuid_int + "";
			stmt.execute(query);
			if (stmt.getUpdateCount() == 1) 
			{
				System.out.println("Entry deleted successfully.");
				deleted = true;
			}
			con.close();
		}
		catch(Exception e) 
		{
			throw new InternalServerErrorException("Internal database error. Unable to access DVD data.");
		}
		return deleted;
	}
	
	public static boolean editDVD(DVD dvdtoEdit, DVD dvd) throws Exception
	{
		boolean edited = false;
		Connection con = getConnection();
		try
		{
			String editGenre = dvd.getGenre();
			int editAmount = dvd.getAmount();
			int uuid = dvdtoEdit.getUuid();
			
			Statement stmt = con.createStatement();
			String query = "UPDATE dvdlib SET genre ='" + editGenre + "', amount =" + editAmount + " WHERE uuid = " + uuid + "";
			stmt.execute(query);
			if (stmt.getUpdateCount() == 1) 
			{
				System.out.println("Database entry has been updated.");
				edited = true;
			}
			con.close();
		}
		catch(Exception e) 
		{
			throw new InternalServerErrorException("Internal database error. Unable to access DVD data.");
		}
		return edited;
	}
	
	private static DVD getDVDFromRS(ResultSet rs) throws SQLException
	{
		DVD dvd = new DVD();
		dvd.setUuid(rs.getInt("uuid"));
		dvd.setTitle(rs.getString("title"));
		dvd.setGenre(rs.getString("genre"));
		dvd.setAmount(rs.getInt("amount"));
		
		return dvd;
	}
}