
package com.derivesystems.inventory.model;

import com.derivesystems.inventory.validation.ValidationErrorSummary;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;

import javax.validation.ConstraintViolationException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class representing the result of the password validation.  If the password is valid,
 * the result is set to success=true, and the errorSummary will be null.
 * Otherwise, success is false, and the errorSummary contains the list of validation messages.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonPropertyOrder({"success", "errorSummary"})
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "ValidationSummary", description = "Summary of all validations")
public class ValidationSummary
{
   public ValidationSummary(ConstraintViolationException e)
   {
      if (e != null)
      {
         success = false;
         errorSummary = new ValidationErrorSummary(e);
      }
   }

   public ValidationSummary()
   {
      success = true;
   }

   private boolean success;

   public ValidationErrorSummary getErrorSummary()
   {
      return errorSummary;
   }

   public void setErrorSummary(ValidationErrorSummary errorSummary)
   {
      this.errorSummary = errorSummary;
   }

   private ValidationErrorSummary errorSummary = null;


   public boolean isSuccess()
   {
      return success;
   }

   public void setSuccess(boolean success)
   {
      this.success = success;
   }

}
