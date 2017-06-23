package com.derivesystems.inventory.validation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the errors from validation
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "ValidationSummary", description = "Summary of all validations")
public class ValidationErrorSummary
{
   private List<String> validationErrors = new ArrayList<>();

   public ValidationErrorSummary(ConstraintViolationException e)
   {
      if (e != null)
      {
         for (ConstraintViolation<?> cv : e.getConstraintViolations())
         {
            validationErrors.add(cv.getMessage());
         }
      }
   }

   public List<String> getValidationErrors()
   {
      return validationErrors;
   }

   public void setValidationErrors(List<String> validationErrors)
   {
      this.validationErrors = validationErrors;
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(validationErrors + "\n");
      return sb.toString();
   }
}
