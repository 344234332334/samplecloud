
package com.derivesystems.inventory.validation;


import com.derivesystems.inventory.model.ValidationSummary;
import org.apache.cxf.jaxrs.validation.ValidationExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@SuppressWarnings("WeakerAccess")
public class CustomValidationExceptionMapper extends ValidationExceptionMapper
{
   private static final Logger LOGGER = LoggerFactory.getLogger(CustomValidationExceptionMapper.class);

   @Override
   public Response toResponse(ValidationException exception)
   {
      if (exception == null)
      {
         LOGGER.trace("creating response; validationException=null");
      }
      else
      {
         LOGGER.trace("creating response; validationException={}", exception.getMessage(), exception);
      }

      Response response = Response.status(Status.INTERNAL_SERVER_ERROR).build();

      if (exception instanceof ConstraintViolationException)
      {
         final ConstraintViolationException constraint = (ConstraintViolationException) exception;
         ValidationSummary summary = new ValidationSummary(constraint);

         LOGGER.trace("creating response; violationSummary={}", summary);

         if (summary != null)
         {
            LOGGER.warn("Request generated ConstraintViolation(s), violationSummary={}", summary);
            response = Response.status(Status.BAD_REQUEST).entity(summary).build();
         }
      }
      else
      {
         LOGGER.error("Caught exception trying to execute the annotations on model objects, exception={}", exception.getMessage(), exception);
      }

      LOGGER.trace("created response; response={}", response);
      return response;
   }
}
