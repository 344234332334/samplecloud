package com.derivesystems.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:inventoryservice.version.properties")

public class InventorySpringConfig
{
   @Autowired
public Environment env;
      @Bean
      public ApplicationInfoService applicationInfoService() {
         return new ApplicationInfoService(env);
      }

}
