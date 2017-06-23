
package com.derivesystems.inventory.api.v1;

import com.derivesystems.inventory.ApplicationInfoService;
import com.derivesystems.inventory.PasswordSpringConfig;
import com.derivesystems.inventory.model.ValidationSummary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Validate a password using javax validation constraints.
 */
@Service
@Path("/validationservice/v1")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Api(value = "PasswordValidationServiceV1", description = "Password Validation")
@ApiResponses({
   @ApiResponse(code = 200, message = "Success"),
   @ApiResponse(code = 400, message = "Violation Errors\", response = ViolationSummary.class"),
   @ApiResponse(code = 500, message = "Internal Server Error"),

})
public class PasswordValidationService
{
   private static final Logger LOGGER = LoggerFactory.getLogger(com.derivesystems.inventory.api.v1.PasswordValidationService.class);
   final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PasswordSpringConfig.class);
   final ApplicationInfoService service = context.getBean(ApplicationInfoService.class);


   public PasswordValidationService()
   {
      LOGGER.info("constructing " + this.getClass().getName());
   }

   /**
    * Returns the version object which contains the current build information.
    *
    * @return Validationsummary containing ncsServiceIds and time based entitlements
    */
   @GET
   @Path("/version")
   @ApiOperation(
      value = "Returns the results of validation for a password",
      notes = "Checks whether password is correct size and fits the pattern.",
      response = ValidationSummary.class)
   public Response version(@Context final HttpServletRequest request)
   {
      LOGGER.info("Recieved version request");

      Response response = Response.status(Status.OK).entity(service.getApplicationInfo()).build();
      return response;
   }


   /**
    * Returns a ValidationSummary object which contains whether the password has passed the validation constraints.
    *
    * @param password      The password to validate
    * @return Validationsummary containing ncsServiceIds and time based entitlements
    */
   @GET
   @Path("/validate")
   @ApiOperation(
      value = "Returns the results of validation for a password",
      notes = "Checks whether password is correct size and fits the pattern.",
      response = ValidationSummary.class)
   public Response validate(@Context final HttpServletRequest request,
                               @Size(min = 5, max = 12)
                               @Pattern.List({
                                  @Pattern(regexp = "^[a-z0-9]+$"),
                                  @Pattern(regexp = "\\A(?!.*(.+)+\\1+).+\\z", message = "{validation.repeatedcharacters.message}"),
                                  @Pattern(regexp = "^(?=.*[a-z]).+$", message = "{validation.musthavelower.message}"),
                                  @Pattern(regexp = "^(?=.*[0-9]).+$", message = "{validation.musthavenumber.message}")
                               })
                               @QueryParam("password") @ApiParam(value = "password", required = true) final String password)
   {
      LOGGER.info("Received validation request for password password={}", password);

      Response response = Response.status(Status.OK).entity(new ValidationSummary()).build();
      return response;
   }
}
