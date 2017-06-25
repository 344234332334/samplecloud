package com.derivesystems;

import com.derivesystems.model.ObjectNotExist;
import com.derivesystems.model.ObjectifyAdapter;
import com.derivesystems.model.Shoe;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

public class ShoeInventoryServlet extends HttpServlet
{

   ObjectMapper mapper = new ObjectMapper();
   private final static Logger logger = Logger.getLogger(ShoeInventoryServlet.class.getName());


   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException
   {
      String shoeParam = request.getParameter("shoeId");
      Long shoeId = shoeParam == null ? null : Long.parseLong(shoeParam);
      ObjectifyAdapter<Shoe> adapter = new ObjectifyAdapter<>(Shoe.class);
      Shoe shoe = null;

      String jsonInString = "";
      if (shoeId != null)
      {
         shoe = adapter.get(shoeId);
         jsonInString = mapper.writeValueAsString(shoe);
      }
      else
      {
         List<Shoe> shoes = adapter.getAll();
         jsonInString = mapper.writeValueAsString(shoes);
      }
      writeResponse(response, jsonInString, HttpServletResponse.SC_OK);
   }

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException
   {
      int status = HttpServletResponse.SC_CREATED;
      Shoe shoe = mapper.readerFor(Shoe.class).readValue(request.getReader());
      ObjectifyAdapter<Shoe> adapter = new ObjectifyAdapter<>(Shoe.class);
      try
      {
         adapter.insert(shoe);
      }
      catch (ObjectNotExist e)
      {
         logger.log(java.util.logging.Level.SEVERE, "Could not insert object", e);
         status = HttpServletResponse.SC_NOT_FOUND;
      }
      String jsonInString = mapper.writeValueAsString(shoe);
      writeResponse(response, jsonInString, status);
   }

   public static String getInfo()
   {
      return "Version: " + System.getProperty("java.version")
         + " OS: " + System.getProperty("os.name")
         + " User: " + System.getProperty("user.name");
   }

   public void writeResponse(HttpServletResponse response, String jsonInString, int status) throws IOException
   {
      response.setStatus(status);
      response.setContentType("application/json");
      PrintWriter out = response.getWriter();

      out.print(jsonInString);
      out.flush();
   }
}
