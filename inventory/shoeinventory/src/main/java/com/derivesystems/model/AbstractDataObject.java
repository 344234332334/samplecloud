/*
 * COPYRIGHT 2017.  ALL RIGHTS RESERVED.  THIS MODULE CONTAINS
 * TIME WARNER CABLE CONFIDENTIAL AND PROPRIETARY INFORMATION.
 * THE INFORMATION CONTAINED HEREIN IS GOVERNED BY LICENSE AND
 * SHALL NOT BE DISTRIBUTED OR COPIED WITHOUT WRITTEN PERMISSION
 * FROM TIME WARNER CABLE.
 */
  
/*
 * Author:   kmoran
 * File:     AbstractDataObject.java
 * Created:  6/24/17
 *
 * Description:
 *
 * PERFORCE
 *
 * Last Revision: $Change$
 * Last Checkin:  $DateTime$
 */
package com.derivesystems.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public abstract class AbstractDataObject<T>
{
   @Id
   private Long id;

   protected AbstractDataObject(Long id){
      this.id = id;
   }

   protected AbstractDataObject(){}

   public abstract Long delete(T item);


   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }
}
