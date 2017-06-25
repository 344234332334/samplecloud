package com.derivesystems;

import com.derivesystems.model.Shoe;
import com.googlecode.objectify.ObjectifyService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;
import java.util.logging.Logger;

public class WarmUpListener implements ServletContextListener
{
   private static final Logger LOGGER = Logger.getLogger(WarmUpListener.class.getName());

   @Override
   public void contextInitialized(ServletContextEvent sce)
   {
      // startup code here
      processLogging(null);
      ObjectifyService.register(Shoe.class);
   }

   @Override
   public void contextDestroyed(ServletContextEvent sce)
   {
      // shutdown code here
   }

   private static void processLogging(final List<String> args)
   {
      System.setProperty("org.apache.commons.logging.Log",
                         "org.apache.commons.logging.impl.NoOpLog");
   }
}

