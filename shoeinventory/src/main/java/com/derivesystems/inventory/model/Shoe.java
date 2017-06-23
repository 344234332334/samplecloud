/*
 * COPYRIGHT 2017.  ALL RIGHTS RESERVED.  THIS MODULE CONTAINS
 * TIME WARNER CABLE CONFIDENTIAL AND PROPRIETARY INFORMATION.
 * THE INFORMATION CONTAINED HEREIN IS GOVERNED BY LICENSE AND
 * SHALL NOT BE DISTRIBUTED OR COPIED WITHOUT WRITTEN PERMISSION
 * FROM TIME WARNER CABLE.
 */
  
/*
 * Author:   kmoran
 * File:     Shoe.java
 * Created:  6/23/17
 *
 * Description:
 *
 * PERFORCE
 *
 * Last Revision: $Change$
 * Last Checkin:  $DateTime$
 */
package com.derivesystems.inventory.model;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

/**
 * The @Entity tells Objectify about our entity.  We also register it in
 * OfyHelper.java -- very important.
 *
 * This is never actually created, but gives a hint to Objectify about our Ancestor key.
 */
/**
 * The @Entity tells Objectify about our entity.  We also register it in {@link OfyHelper}
 * Our primary key @Id is set automatically by the Google Datastore for us.
 *
 * We add a @Parent to tell the object about its ancestor. We are doing this to support many
 * guestbooks.  Objectify, unlike the AppEngine library requires that you specify the fields you
 * want to index using @Index.  Only indexing the fields you need can lead to substantial gains in
 * performance -- though if not indexing your data from the start will require indexing it later.
 *
 * NOTE - all the properties are PUBLIC so that we can keep the code simple.
 **/
@Entity
public class Shoe {
   //@Parent Key<Guestbook> theBook;
   @Id public Long id;
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
      this();
      this.name = name;
      this.size = size;
      this.description = description;

   }

   public Shoe(com.google.cloud.datastore.Entity  entity){
      id = entity.getKey().getId();
      name = entity.getString("name");
      size = entity.getLong("size");

      description = entity.getString("description");
   }


}
