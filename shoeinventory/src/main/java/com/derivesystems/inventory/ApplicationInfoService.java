
package com.derivesystems.inventory;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Component
@PropertySource("classpath:passwordservice.version.properties")
public final class ApplicationInfoService
{
   private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationInfoService.class);

   private Environment environment = null;

   @Autowired
   public ApplicationInfoService(Environment env){
      this.environment=env;
   }

   public ApplicationInfo getApplicationInfo()
   {
      ApplicationInfo applicationInfo = new ApplicationInfo(getApplicationVersion());
      return applicationInfo;
   }

   public String getApplicationVersion()
   {
      String applicationVersion = this.environment.getProperty("passwordValidation.version");
      LOGGER.debug("retrieved application version={}", applicationVersion);

      return applicationVersion;
   }
}
