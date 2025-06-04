package com.felfired.dvd.utility;

import com.felfired.dvd.domain.DVD;
import com.felfired.dvd.exceptions.InternalServerErrorException;
import org.springframework.stereotype.Repository;
import java.sql.Connection;
import java.sql.PreparedStatement; 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List; 
import javax.sql.DataSource; 

@Repository
public class DBHandler 
{
    private final DataSource dataSource;

    public DBHandler(DataSource dataSource) 
    {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException 
    {
        return dataSource.getConnection();
    }

    public boolean existsUser(String username, String password) throws InternalServerErrorException 
    {
        boolean hasUser = false;
        String query = "SELECT COUNT(*) FROM staff WHERE username=? AND password=?";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) 
	{
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) 
	    {
                if (rs.next()) 
		{
                    hasUser = rs.getInt(1) > 0;
                }
            }
        } 
	catch (SQLException e) 
	{
            throw new InternalServerErrorException("An internal error prevented from authenticating user: " + e.getMessage());
        }
        return hasUser;
    }

    public boolean createDVD(DVD dvd) throws InternalServerErrorException 
    {
        boolean created = false;
        try (Connection con = getConnection()) 
	{
            con.setAutoCommit(false); 
            try (Statement stmt = con.createStatement()) 
	    {
                String countQuery = "SELECT COUNT(*) FROM dvdlib";
                ResultSet rs = stmt.executeQuery(countQuery);
                int rowCount = 0;
                if (rs.next()) 
		{
                    rowCount = rs.getInt(1);
                }
                rs.close(); 
                int uuid;
                if (rowCount == 0) 
		{
                    uuid = 1;
                } 
		else 
		{
                    String maxUuidQuery = "SELECT MAX(uuid) FROM dvdlib";
                    rs = stmt.executeQuery(maxUuidQuery);
                    if (rs.next()) 
		    {
                        uuid = rs.getInt(1) + 1;
                    } else 
		    {
			// Fallback, though unlikely if count > 0
                        uuid = 1; 
                    }
                    rs.close();                
		}

                String insertQuery = "INSERT INTO dvdlib(uuid,title,genre,amount) VALUES(?,?,?,?)";
                try (PreparedStatement insertStmt = con.prepareStatement(insertQuery)) 
		{
                    insertStmt.setInt(1, uuid);
                    insertStmt.setString(2, dvd.getTitle());
                    insertStmt.setString(3, dvd.getGenre());
                    insertStmt.setInt(4, dvd.getAmount());
                    int updatedRows = insertStmt.executeUpdate();
                    if (updatedRows == 1) 
		    {
                        System.out.println("Database entry added.");
                        created = true;
                    }
                }
                con.commit(); 
            } 
	    catch (SQLException e) 
	    {
                con.rollback(); 
                throw new InternalServerErrorException("Internal database error. Unable to create DVD: " + e.getMessage());
            }
        } 
	catch (SQLException e) 
	{
            throw new InternalServerErrorException("Internal database error. Unable to get connection for DVD creation: " + e.getMessage());
        }
        return created;
    }

    public boolean existsDVD(String title) throws InternalServerErrorException 
    {
        boolean hasDVD = false;
        String query = "SELECT COUNT(*) FROM dvdlib WHERE title=?";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) 
	{
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) 
	    {
                if (rs.next()) 
		{
                    hasDVD = rs.getInt(1) > 0;
                }
            }
        } 
	catch (SQLException e) 
	{
            throw new InternalServerErrorException("Internal database error. Unable to access DVD data: " + e.getMessage());
        }
        return hasDVD;
    }

    public boolean existsDVDUUID(String uuid) throws InternalServerErrorException 
    {
        boolean hasDVD = false;
        String query = "SELECT COUNT(*) FROM dvdlib WHERE uuid=?";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) 
	{
            stmt.setInt(1, Integer.parseInt(uuid));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) 
		{
                    hasDVD = rs.getInt(1) > 0;
                }
            }
        } 
	catch (NumberFormatException e) 
	{
            throw new InternalServerErrorException("Invalid UUID format: " + uuid);
        } 
	catch (SQLException e) 
	{
            throw new InternalServerErrorException("Internal database error. Unable to access DVD data: " + e.getMessage());
        }
        return hasDVD;
    }

    public DVD getDVDFromTitle(String title) throws InternalServerErrorException 
    {
        DVD dvd = null;
        String query = "SELECT uuid, title, genre, amount FROM dvdlib WHERE title=?";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) 
	{
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) 
	    {
                if (rs.next()) 
		{
                    dvd = getDVDFromRS(rs);
                }
            }
        } 
	catch (SQLException e) 
	{
            throw new InternalServerErrorException("Internal database error. Unable to access DVD data: " + e.getMessage());
        }
        return dvd;
    }

    public DVD getDVDFromUUID(String uuid) throws InternalServerErrorException 
    {
        DVD dvd = null;
        String query = "SELECT uuid, title, genre, amount FROM dvdlib WHERE uuid=?";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) 
	{
            stmt.setInt(1, Integer.parseInt(uuid));
            try (ResultSet rs = stmt.executeQuery()) 
	    {
                if (rs.next()) 
		{
                    dvd = getDVDFromRS(rs);
                }
            }
        } 
	catch (NumberFormatException e) 
	{
            throw new InternalServerErrorException("Invalid UUID format: " + uuid);
        } 
	catch (SQLException e) 
	{
            throw new InternalServerErrorException("Internal database error. Unable to access DVD data: " + e.getMessage());
        }
        return dvd;
    }

    public List<DVD> getAllDVD() throws InternalServerErrorException 
	{
        List<DVD> dvdList = new ArrayList<>();
        String query = "SELECT uuid, title, genre, amount FROM dvdlib";
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) 
	{
            while (rs.next()) 
	    {
                dvdList.add(getDVDFromRS(rs));
            }
        } 
	catch (SQLException e) 
	{
            throw new InternalServerErrorException("Internal database error. Unable to access DVD data: " + e.getMessage());
        }
        return dvdList;
    }

    public boolean deleteDVD(String uuid) throws InternalServerErrorException 
    {
        boolean deleted = false;
        String query = "DELETE FROM dvdlib WHERE uuid=?";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) 
	{
            stmt.setInt(1, Integer.parseInt(uuid));
            int updatedRows = stmt.executeUpdate();
            if (updatedRows == 1) 
	    {
                System.out.println("Entry deleted successfully.");
                deleted = true;
            }
        } 
	catch (NumberFormatException e) 
	{
            throw new InternalServerErrorException("Invalid UUID format: " + uuid);
        } 
	catch (SQLException e) 
	{
            throw new InternalServerErrorException("Internal database error. Unable to access DVD data: " + e.getMessage());
        }
        return deleted;
    }

    public boolean editDVD(DVD dvdToEdit, DVD dvd) throws InternalServerErrorException 
    {
        boolean edited = false;
        String query = "UPDATE dvdlib SET genre=?, amount=? WHERE uuid=?";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) 
	{
            stmt.setString(1, dvd.getGenre());
            stmt.setInt(2, dvd.getAmount());
            stmt.setInt(3, dvdToEdit.getUuid());
            int updatedRows = stmt.executeUpdate();
            if (updatedRows == 1) 
	    {
                System.out.println("Database entry has been updated.");
                edited = true;
            }
        } 
	catch (SQLException e) 
	{
            throw new InternalServerErrorException("Internal database error. Unable to access DVD data: " + e.getMessage());
        }
        return edited;
    }

    private DVD getDVDFromRS(ResultSet rs) throws SQLException 
    {
        DVD dvd = new DVD();
        dvd.setUuid(rs.getInt("uuid"));
        dvd.setTitle(rs.getString("title"));
        dvd.setGenre(rs.getString("genre"));
        dvd.setAmount(rs.getInt("amount"));
        return dvd;
    }
}
