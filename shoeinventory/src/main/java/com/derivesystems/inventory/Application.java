package com.derivesystems.inventory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.List;

@org.springframework.context.annotation.Configuration
public class Application implements ApplicationContextAware
{
   private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
   private ApplicationContext applicationContext = null;

   public static void main(final String[] args) throws Exception
   {
      // Setup the logger
      final String key = "application";
      final String application = "passwordservice";

      LOGGER.info("using applicationName={}", application);
      List<String> argList = Arrays.asList(args);
      processLogging(argList);
      LOGGER.info("constructing and loading spring application context");
      ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:context-passwords.xml");
      LOGGER.info("loaded spring application context");
      Application thisApp = applicationContext.getBean("passwordApp", Application.class);

      thisApp.start();
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
   {
      this.applicationContext = applicationContext;
   }

   public void start()
   {
      try
      {
         LOGGER.info("starting up password validation server");

         final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PasswordSpringConfig.class);
         final ApplicationInfoService service = context.getBean(ApplicationInfoService.class);
         final String version = service.getApplicationVersion();
         LOGGER.info("running passwordService Version={}", version);

         LOGGER.info("password server successfully started");
      }
      catch (final Exception e)
      {
         LOGGER.error("caught exception while starting up server", e);
         throw new RuntimeException(e);
      }
   }

   private static void processLogging(final List<String> args)
   {
      LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
      Configuration config = ctx.getConfiguration();
      LoggerConfig loggerConfig = config.getLoggerConfig("root");
      if (args.contains("INFO"))
      {
         loggerConfig.setLevel(Level.INFO);
      }
      else if (args.contains("DEBUG"))
      {
         loggerConfig.setLevel(Level.DEBUG);
      }
      else if (args.contains("TRACE"))
      {
         loggerConfig.setLevel(Level.TRACE);
      }
   }
}
