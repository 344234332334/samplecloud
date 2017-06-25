/*
 * COPYRIGHT 2017.  ALL RIGHTS RESERVED.  THIS MODULE CONTAINS
 * TIME WARNER CABLE CONFIDENTIAL AND PROPRIETARY INFORMATION.
 * THE INFORMATION CONTAINED HEREIN IS GOVERNED BY LICENSE AND
 * SHALL NOT BE DISTRIBUTED OR COPIED WITHOUT WRITTEN PERMISSION
 * FROM TIME WARNER CABLE.
 */
  
/*
 * Author:   kmoran
 * File:     ObjectNotExist.java
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

public class ObjectNotExist extends Exception
{
   AbstractDataObject object = null;
   public ObjectNotExist(AbstractDataObject o){
      object = o;
   }

   @Override
   public String toString(){
      StringBuilder sb = new StringBuilder();
      sb.append(super.toString());
      sb.append(this.object);
      return sb.toString();
   }
}
