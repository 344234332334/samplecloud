
package com.derivesystems.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;


@Entity
public class Shoe extends AbstractDataObject<Shoe>
{
   static{
      ObjectifyService.register(Shoe.class);
   }

   public String name;
   public Long size;
   public String description;
   public Date dateCreated;

   /**
    * Simple constructor just sets the date
    **/
   public Shoe() {
      dateCreated = new Date();
   }

   /**
    * A convenience constructor
    **/
   public Shoe(String name, Long size, String description) {
      this.name = name;
      this.size = size;
      this.description = description;
   }

   public Shoe(com.google.cloud.datastore.Entity  entity){
      super(entity.getKey().getId());
      name = entity.getString("name");
      size = entity.getLong("size");
      dateCreated = entity.getDateTime("dateCreated").toDate();
      description = entity.getString("description");
   }


   @Override
   public Long delete(Shoe item)
   {
      return null;
   }

   @Override
   public String toString(){
      String jsonString = "";
      try
      {
         jsonString =  new ObjectMapper().writeValueAsString(this);
      }
      catch (JsonProcessingException e)
      {
         e.printStackTrace();
         //Do nothing
      }
      return jsonString;
   }
}
