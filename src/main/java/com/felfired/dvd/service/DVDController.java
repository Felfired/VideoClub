package com.felfired.dvd.controller;

import com.felfired.dvd.domain.DVD;
import com.felfired.dvd.exceptions.InternalServerErrorException;
import com.felfired.dvd.utility.DBHandler;
import com.felfired.dvd.utility.HTMLHandler;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/api")
public class DvdController {

    private final DBHandler dbHandler;
    private final HTMLHandler htmlHandler; 
    
    public DvdController(DBHandler dbHandler) 
    {
        this.dbHandler = dbHandler;
        this.htmlHandler = new HTMLHandler();
    }

    @GetMapping("/home")
    public String getHomePage() 
    {
        return "index.html"; 
    }

    
    @GetMapping("/login")
    public String getLoginPage() 
    {
        return "login.html"; 
    }

    @GetMapping("/error")
    public String getErrorPage() 
    {
        return "error.html";
    }

    @GetMapping("/dashboard")
    public Object getDashboardPage(HttpSession session) 
    {
        Boolean loggedInUser = (Boolean) session.getAttribute("loggedInUser");
        if (loggedInUser != null && loggedInUser) 
        {
            return "dashboard.html"; 
        } else 
        {
            return new RedirectView("/api/login");
        }
    }

    @PostMapping("/login")
    public RedirectView authenticateUser(@RequestParam("username") String username,
                                         @RequestParam("password") String password,
                                         HttpSession session) 
    {
        try 
        {
            if (dbHandler.existsUser(username, password)) 
            {
                session.setAttribute("loggedInUser", true);
                return new RedirectView("/api/dashboard");
            } 
            else 
            {
                return new RedirectView("/api/error");
            }
        } 
        catch (InternalServerErrorException e) 
        {
            System.err.println("Database error during authentication: " + e.getMessage());
            // Redirect to error page on DB error.
            return new RedirectView("/api/error"); 
        }
    }

    @GetMapping("/logout")
    public RedirectView logoutUser(HttpSession session) 
    {
        session.invalidate(); 
        return new RedirectView("/api/login");
    }

    @GetMapping(value = "/dvdlib", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getDvdCatalogue(HttpSession session) 
    {
        Boolean loggedInUser = (Boolean) session.getAttribute("loggedInUser");
        try 
        {
            List<DVD> dvdList = dbHandler.getAllDVD();
            if (loggedInUser != null && loggedInUser) 
            {
                return htmlHandler.createHtmlCatalogueAuth(dvdList);
            } 
            else 
            {
                return htmlHandler.createHtmlCatalogueUser(dvdList);
            }
        } 
        catch (InternalServerErrorException e) 
        {
            return htmlHandler.createHtmlError("Error fetching DVD catalogue: " + e.getMessage());
        }
    }

    @GetMapping(value = "/dvdlib/{title}", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getDvdHtmlByTitle(@PathVariable("title") String encodedTitle, HttpSession session) 
    {
        try 
        {
            String title = URLDecoder.decode(encodedTitle, StandardCharsets.UTF_8.toString());
            DVD dvd = dbHandler.getDVDFromTitle(title);

            if (dvd == null) 
            {
                return htmlHandler.createHtmlError("404 Not Found - The requested DVD was not found.");
            }

            Boolean loggedInUser = (Boolean) session.getAttribute("loggedInUser");
            if (loggedInUser != null && loggedInUser) 
            {
                return htmlHandler.createHtmlDVDAuth(dvd);
            } 
            else 
            {
                return htmlHandler.createHtmlDVDUser(dvd);
            }
        } 
        catch (InternalServerErrorException e) 
        {
            return htmlHandler.createHtmlError("Error retrieving DVD: " + e.getMessage());
        } 
        catch (Exception e) 
        {
            return htmlHandler.createHtmlError("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping(value = "/search", produces = MediaType.TEXT_HTML_VALUE)
    public RedirectView searchDvd(@RequestParam("request") String request) 
    {
        if (request == null || request.trim().isEmpty()) 
        {
            return new RedirectView("/api/error");
        }

        DVD dvd = null;
        try 
        {
            if (request.matches("[0-9]+")) 
            {
                dvd = dbHandler.getDVDFromUUID(request);
            } 
            else 
            {
                dvd = dbHandler.getDVDFromTitle(request);
            }
        } 
        catch (InternalServerErrorException e) 
        {
            System.err.println("Database error during search: " + e.getMessage());
            return new RedirectView("/api/error");
        }

        if (dvd != null) 
        {
            return new RedirectView("/api/dvdlib/" + dvd.getTitle());
        } 
        else 
        {
            return new RedirectView("/api/error");
        }
    }

    @PostMapping("/delete")
    public RedirectView deleteDvd(@RequestParam("uuid") String uuid) 
    {
        try 
        {
            if (dbHandler.existsDVDUUID(uuid)) 
            {
                dbHandler.deleteDVD(uuid);
                return new RedirectView("/api/dashboard");
            } 
            else 
            {
                return new RedirectView("/api/error");
            }
        } 
        catch (InternalServerErrorException e) 
        {
            System.err.println("Database error during delete: " + e.getMessage());
            return new RedirectView("/api/error");
        }
    }

    @PostMapping(value = "/createdvd", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> createDvd(@RequestBody DVD dvd) 
    {
        if (dvd == null || dvd.getTitle() == null || dvd.getGenre() == null || dvd.getAmount() <= 0) 
        {
            return new ResponseEntity<>("400 Bad Format - Invalid DVD data.", HttpStatus.BAD_REQUEST);
        }
        try
        {
            if (dbHandler.existsDVD(dvd.getTitle())) 
            {
                return new ResponseEntity<>("300 Identical Entry - The DVD already exists.", HttpStatus.CONFLICT);
            else 
            {
                dbHandler.createDVD(dvd);
                return new ResponseEntity<>("200 Success - Operation was successful.", HttpStatus.OK);
            }
        } 
        catch (InternalServerErrorException e) 
        {
            System.err.println("Database error during DVD creation: " + e.getMessage());
            return new ResponseEntity<>("500 Internal Server Error - " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } 
        catch (Exception e)
        {
            System.err.println("Unknown error during DVD creation: " + e.getMessage());
            return new ResponseEntity<>("400 Bad Format - Unknown JSON data fault.", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/editdvd", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> editDvd(@RequestBody DVD dvd) 
    {
        if (dvd == null || dvd.getUuid() <= 0 || dvd.getGenre() == null || dvd.getAmount() <= 0) 
        {
            return new ResponseEntity<>("400 Bad Format - Invalid DVD data.", HttpStatus.BAD_REQUEST);
        }
        try 
        {
            String uuid_string = Integer.toString(dvd.getUuid());
            if (dbHandler.existsDVDUUID(uuid_string)) 
            {
                DVD dvdToEdit = dbHandler.getDVDFromUUID(uuid_string);
                dbHandler.editDVD(dvdToEdit, dvd);
                return new ResponseEntity<>("200 Success - Operation was successful.", HttpStatus.OK);
            } 
            else 
            {
                return new ResponseEntity<>("404 Not Found - The requested resource was not found.", HttpStatus.NOT_FOUND);
            }
        } 
        catch (InternalServerErrorException e) 
        {
            System.err.println("Database error during DVD edit: " + e.getMessage());
            return new ResponseEntity<>("500 Internal Server Error - " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } 
        catch (Exception e) 
        {
            System.err.println("Unknown error during DVD edit: " + e.getMessage());
            return new ResponseEntity<>("400 Bad Format - Unknown JSON data fault.", HttpStatus.BAD_REQUEST);
        }
    }
}
