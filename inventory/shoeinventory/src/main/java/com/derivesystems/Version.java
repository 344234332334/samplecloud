package com.derivesystems;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Version extends HttpServlet
{
   private static final Logger LOGGER = Logger.getLogger(Version.class.getName());

   String appVersion;

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException
   {
      LOGGER.log(Level.INFO, "Received application version request, appVersion={}", appVersion);
      response.setContentType("application/json");
      PrintWriter out = response.getWriter();
      ObjectMapper mapper = new ObjectMapper();

      String jsonInString = mapper.writeValueAsString(getApplicationInfo());

      out.print(jsonInString);
      out.flush();
   }

   public ApplicationInfo getApplicationInfo() throws IOException
   {
      ApplicationInfo applicationInfo = new ApplicationInfo(getApplicationVersion());
      return applicationInfo;
   }

   public String getApplicationVersion() throws IOException
   {
      if (appVersion != null)
      {
         return appVersion;
      }
      try
      {
         File schedulerFile = new File(getServletContext().getResource("/WEB-INF/inventoryservice.version.properties").getPath());
         Configuration config = new PropertiesConfiguration(schedulerFile);
         appVersion = config.getString("inventoryService.version");

      }
      catch (ConfigurationException e)
      {
         throw new IOException(e);
      }
      return appVersion;
   }
}
