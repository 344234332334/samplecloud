
package com.derivesystems.inventory.api.v1;

import com.derivesystems.inventory.ApplicationInfoService;
import com.derivesystems.inventory.PasswordSpringConfig;
import com.derivesystems.inventory.model.Shoe;
import com.derivesystems.inventory.model.ValidationSummary;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.DateTime;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;
import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.cloud.datastore.StructuredQuery.Filter;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.util.Closeable;
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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Validate a password using javax validation constraints.
 */
@Service
@Path("/inventory/v1")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Api(value = "PasswordValidationServiceV1", description = "Password Validation")
@ApiResponses({
   @ApiResponse(code = 200, message = "Success"),
   @ApiResponse(code = 400, message = "Violation Errors\", response = ViolationSummary.class"),
   @ApiResponse(code = 500, message = "Internal Server Error"),

})
public class InventoryService
{
   private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);
   final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PasswordSpringConfig.class);
   final ApplicationInfoService service = context.getBean(ApplicationInfoService.class);


   public InventoryService()
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

      // store only the first two octets of a users ip address
      String userIp = request.getRemoteAddr();
      InetAddress address = null;
      Response response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
      try
      {
         address = InetAddress.getByName(userIp);

      if (address instanceof Inet6Address) {
         // nest indexOf calls to find the second occurrence of a character in a string
         // an alternative is to use Apache Commons Lang: StringUtils.ordinalIndexOf()
         userIp = userIp.substring(0, userIp.indexOf(":", userIp.indexOf(":") + 1)) + ":*:*:*:*:*:*";
      } else if (address instanceof Inet4Address) {
         userIp = userIp.substring(0, userIp.indexOf(".", userIp.indexOf(".") + 1)) + ".*.*";
      }

      Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
      KeyFactory keyFactory = datastore.newKeyFactory().setKind("visit");
      IncompleteKey key = keyFactory.setKind("visit").newKey();

      // Record a visit to the datastore, storing the IP and timestamp.
      FullEntity<IncompleteKey> curVisit = FullEntity.newBuilder(key)
                                                     .set("user_ip", userIp).set("timestamp", DateTime.now()).build();
      datastore.add(curVisit);

      // Retrieve the last 10 visits from the datastore, ordered by timestamp.
      Query<Entity> query = Query.newEntityQueryBuilder().setKind("visit")
                                 .setOrderBy(StructuredQuery.OrderBy.desc("timestamp")).setLimit(10).build();
      QueryResults<Entity> results = datastore.run(query);

     // resp.setContentType("text/plain");
    //  PrintWriter out = resp.getWriter();
    //  out.print("Last 10 visits:\n");
         List<String> visits = new ArrayList<String>();
      while (results.hasNext()) {
         Entity entity = results.next();
        // out.format("Time: %s Addr: %s\n", entity.getDateTime("timestamp"),
      //              entity.getString("user_ip"));
         visits.add(String.format("Time: %s Addr: %s\n", entity.getDateTime("timestamp"),
                                  entity.getString("user_ip")));

      }


      response = Response.status(Status.OK).entity(visits).build();

      }
      catch (UnknownHostException e)
      {
         e.printStackTrace();
      }
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

   /**
    * Returns a ValidationSummary object which contains whether the password has passed the validation constraints.
    *
    * @return Validationsummary containing ncsServiceIds and time based entitlements
    */
   @GET
   @Path("/shoe")
   @ApiOperation(
      value = "Returns the results of validation for a password",
      notes = "Checks whether password is correct size and fits the pattern.",
      response = Shoe.class)
   public Response shoe(@Context final HttpServletRequest request,

                            @QueryParam("shoeid") final Long shoeId)
   {
      LOGGER.info("Received validation request for shoe shoeId={}", shoeId);
      //Shoe shoe =      ofy().load().type(Shoe.class).id(shoeId).now();
    //  Result<Shoe> th = ofy().load().type(Shoe.class).id(123L);

      // Retrieve the last 10 visits from the datastore, ordered by timestamp.
   //   Query<Entity> query = Query.newEntityQueryBuilder().setKind("shoe").
    //  QueryResults<Entity> results = datastore.run(query);

      Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
      KeyFactory keyFactory = datastore.newKeyFactory().setKind("shoe");
      Key key = keyFactory.setKind("shoe").newKey(shoeId);

      // Record a visit to the datastore, storing the IP and timestamp.
    //  FullEntity<IncompleteKey> curVisit = FullEntity.newBuilder(key)
   //                                                  .set("user_ip", userIp).set("timestamp", DateTime.now()).build();
      Entity entity = datastore.get(key);

      Response response = null;
if(entity!=null)
{

   response = Response.status(Status.OK).entity(entity.toString()).build();
}else
{
   response = Response.status(Status.NOT_FOUND).build();
}
      return response;
   }

   @POST
   @Path("/shoe")
   @ApiOperation(
      value = "Creates a new shoe.",
      notes = "Creates a new shoe.",
      response = Shoe.class)
   public Response createShoe(@Context final HttpServletRequest request, Shoe shoe)
   {
      LOGGER.info("Received post request for shoe shoe={}", shoe);
      Response response = null;
      Closeable session = ObjectifyService.begin();
      if(shoe==null){
         response = Response.status(Status.BAD_REQUEST).build();
      }else
      {

         ofy().save().entity(shoe).now();   // synchronous
         response = Response.status(Status.OK).entity(shoe).build();
      }
      session.close();


      return response;
   }




}
