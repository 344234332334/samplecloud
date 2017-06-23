package com.derivesystems.inventory.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;

public class BeanValidationProvider extends org.apache.cxf.validation.BeanValidationProvider
{
   private static final Logger LOGGER = LoggerFactory.getLogger(BeanValidationProvider.class);

   public BeanValidationProvider()
   {
      super();
      LOGGER.info("created custom bean validation provider; provider={}", this);
   }

   @Override
   public <T> void validateParameters(final T instance, final Method method, final Object[] arguments)
   {
      try
      {
         if (LOGGER.isTraceEnabled())
         {
            LOGGER.trace("validate parameters; instance={}, method={}, arguments={}", instance, method.getName(), arguments == null ? null : Arrays.asList(arguments));
         }

         super.validateParameters(instance, method, arguments);
      }
      catch (final RuntimeException e)
      {
         LOGGER.warn("parameter validation failed; instance={}, method={}, arguments={}", instance, method.getName(), arguments == null ? null : Arrays.asList(arguments));
         throw e;
      }
   }

   @Override
   public <T> void validateBean(final T bean)
   {
      try
      {
         if (LOGGER.isTraceEnabled())
         {
            LOGGER.trace("validate bean; bean={}", bean);
         }

         super.validateBean(bean);
      }
      catch (final RuntimeException e)
      {
         LOGGER.warn("bean validation failed; bean={}", bean);
         throw e;
      }
   }
}
