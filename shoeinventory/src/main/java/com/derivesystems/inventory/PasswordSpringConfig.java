package com.derivesystems.inventory;

import com.derivesystems.inventory.model.Shoe;
import com.googlecode.objectify.ObjectifyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:passwordservice.version.properties")

public class PasswordSpringConfig
{
   @Autowired
public Environment env;


      @Bean
      public ApplicationInfoService applicationInfoService() {
         return new ApplicationInfoService(env);
      }

}
