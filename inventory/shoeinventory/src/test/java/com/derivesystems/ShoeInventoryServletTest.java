package com.derivesystems;

import com.derivesystems.model.ObjectifyAdapter;
import com.derivesystems.model.Shoe;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ShoeInventoryServlet}.
 */

@RunWith(JUnit4.class)
public class ShoeInventoryServletTest
{
   private static final String FAKE_URL = "fake.fk/inventory/shoes";
   private Long shoeId = 4785074604081152L;
   // Set up a helper so that the ApiProxy returns a valid environment for local testing.
   private final LocalServiceTestHelper helper = new LocalServiceTestHelper();

   @Mock
   private HttpServletRequest mockRequest;
   @Mock
   private HttpServletRequest mockRequestSpecificGet;

   @Mock
   private HttpServletResponse mockResponse;
   private StringWriter responseWriter;
   private ShoeInventoryServlet servletUnderTest;

   @Before
   public void setUp() throws Exception
   {
      MockitoAnnotations.initMocks(this);
      helper.setUp();
      //  Set up some fake HTTP requests
      when(mockRequest.getRequestURI()).thenReturn(FAKE_URL);
      when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader("{\"name\":\"xyz\",\"size\":\"2\",\"description\":\"just some text\"}")));

      // Set up a fake HTTP response.
      responseWriter = new StringWriter();
      when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

      servletUnderTest = new ShoeInventoryServlet();


      //Create a shoe for testing
      ObjectifyService.run(new VoidWork()
      {
         public void vrun()
         {
            try
            {
               ObjectifyService.register(Shoe.class);
               ObjectifyAdapter<Shoe> adapter = new ObjectifyAdapter<>(Shoe.class);
               Shoe shoe = new Shoe("aaatest", 33L, "A test Shoe");
               shoeId = adapter.insert(shoe);


            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }

         }
      });
      when(mockRequestSpecificGet.getRequestURI()).thenReturn(FAKE_URL + "?shoeId=" + shoeId.toString());
   }

   @After
   public void tearDown()
   {
      helper.tearDown();
   }

   @Test
   public void doPost_writesResponse() throws Exception
   {
      ObjectifyService.run(new VoidWork()
      {
         public void vrun()
         {
            try
            {
               ObjectifyService.register(Shoe.class);
               servletUnderTest.doPost(mockRequest, mockResponse);
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }

         }
      });

      assertThat(responseWriter.toString())
         .named("ShoeInventoryServlet response")
         .contains("\"name\":\"xyz\"");
   }

   @Test
   public void doGet_writesResponse() throws Exception
   {
      ObjectifyService.run(new VoidWork()
      {
         public void vrun()
         {
            try
            {
               ObjectifyService.register(Shoe.class);
               servletUnderTest.doGet(mockRequest, mockResponse);
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }

         }
      });

      assertThat(responseWriter.toString())
         .named("ShoeInventoryServlet response")
         .contains("id");

      //Check to make sure we can deserialize to list
      List<Shoe> shoes = new ArrayList<>();
      ObjectMapper mapper = new ObjectMapper();
      shoes = mapper.readValue(responseWriter.toString(), new TypeReference<List<Shoe>>()
      {
      });
      assertThat(shoes).isNotEmpty();
   }

   @Test
   @Ignore
   public void doGet_findsSpecific() throws Exception
   {
      ObjectifyService.run(new VoidWork()
      {
         public void vrun()
         {
            try
            {
               ObjectifyService.register(Shoe.class);
               servletUnderTest.doGet(mockRequestSpecificGet, mockResponse);
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }

         }
      });
      Thread.sleep(2000);
      assertThat(responseWriter.toString())
         .named("ShoeInventoryServlet response")
         .contains("\"id\":" + shoeId.toString());
   }

   @Test
   public void HelloInfo_test()
   {
      String result = ShoeInventoryServlet.getInfo();
      assertThat(result)
         .named("ShoeInventoryServlet.getInfo")
         .containsMatch("^Version:\\s+.+OS:\\s+.+User:\\s");
   }
}
