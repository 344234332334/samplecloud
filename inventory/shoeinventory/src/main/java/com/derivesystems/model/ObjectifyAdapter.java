
package com.derivesystems.model;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Result;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class ObjectifyAdapter<T>
{
   final Class<T> typeParameterClass;


   public ObjectifyAdapter(Class<T> clazz){
      this.typeParameterClass = clazz;
   }

   public Long insert(AbstractDataObject<T> item) throws ObjectNotExist
   {
      if(item.getId()==null)
      {
         ofy().save().entity(item).now();
      }
      else{
         T fetched = get(item.getId());
         if(fetched!=null){
            ofy().save().entity(item).now();

         }else{
            throw new ObjectNotExist(item);
         }

      }
      return item.getId();
   }

   public T get(Long id){
      Result<T> result = ofy().load().key(Key.create(typeParameterClass, id));  // Result is async
      return result.now();
   }

   public List<T> getAll(){
      List<T> fetchedList = ofy().load().type(typeParameterClass).limit(10).list();
      return fetchedList;
   }


}
