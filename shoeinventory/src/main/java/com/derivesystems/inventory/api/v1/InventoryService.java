
package com.derivesystems.inventory.api.v1;

import com.derivesystems.inventory.ApplicationInfoService;
import com.derivesystems.inventory.InventorySpringConfig;
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

import com.googlecode.objectify.ObjectifyFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Store or get a shoe from inventory.
 */
@Service
@Path("/inventory/v1")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Api(value = "InventoryServiceV1", description = "Shoe Inventory")
@ApiResponses({
   @ApiResponse(code = 200, message = "Success"),
   @ApiResponse(code = 400, message = "Violation Errors\", response = ViolationSummary.class"),
   @ApiResponse(code = 500, message = "Internal Server Error"),

})
public class InventoryService
{
   private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);
   final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(InventorySpringConfig.class);
   final ApplicationInfoService service = context.getBean(ApplicationInfoService.class);


   public InventoryService()
   {
      LOGGER.info("constructing " + this.getClass().getName());
   }

   /**
    * Returns the version object which contains the current build information.
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
    * Return a shoe with id as the query param.
    *
    * @return Shoe
    */
   @GET
   @Path("/shoe")
   @ApiOperation(
      value = "Returns  shoe from the inventory",
      notes = "Returns  shoe from the inventory.",
      response = Shoe.class)
   public Response shoe(@Context final HttpServletRequest request,

                        @QueryParam("shoeid") final Long shoeId)
   {
      LOGGER.info("Received validation request for shoe shoeId={}", shoeId);

      Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
      Entity entity = null;
      List<Shoe> shoes = null;
      if (shoeId != null)
      {
         KeyFactory keyFactory = datastore.newKeyFactory().setKind("shoe");
         Key key = keyFactory.setKind("shoe").newKey(shoeId);
         entity = datastore.get(key);
      }
      else
      {
         // Retrieve the last 10 visits from the datastore, ordered by timestamp.
         Query<Entity> query = Query.newEntityQueryBuilder().setKind("shoe")
                                    .setOrderBy(StructuredQuery.OrderBy.desc("dateCreated")).setLimit(10).build();
         QueryResults<Entity> results = datastore.run(query);

         shoes = new ArrayList<Shoe>();
         while (results.hasNext())
         {
            Entity temp = results.next();
            shoes.add(new Shoe(temp));
         }
      }
      Response response = null;
      if (entity != null)
      {
         response = Response.status(Status.OK).entity(new Shoe(entity)).build();
      }
      else if (shoes != null)
      {
         response = Response.status(Status.OK).entity(shoes).build();
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
      Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
      KeyFactory keyFactory = datastore.newKeyFactory().setKind("shoe");
      IncompleteKey key = keyFactory.setKind("shoe").newKey();

      // Record a visit to the datastore, storing the IP and timestamp.
      FullEntity<IncompleteKey> shoeEntity = FullEntity.newBuilder(key)
                                                       .set("name", shoe.name).set("description", shoe.description).set("size", shoe.size).set("dateCreated", DateTime.now()).build();

      Entity returnedEntity = datastore.add(shoeEntity);

      if (shoe == null)
      {
         response = Response.status(Status.BAD_REQUEST).build();
      }
      else
      {

         response = Response.status(Status.OK).entity(new Shoe(returnedEntity)).build();
      }

      return response;
   }

}
