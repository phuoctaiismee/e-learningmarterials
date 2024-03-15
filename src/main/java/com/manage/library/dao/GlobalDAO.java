
package com.manage.library.dao;


import java.util.List;

abstract class GlobalDAO <E , K> {
// them
   abstract public void insert(E entity);
// xoa
   abstract public void delete(K id);
// sua
   abstract public  void update(E entity);
//truy van
   abstract public  List<E> select();
// select id
   abstract public E selectID(K id); 
// select 
   abstract protected List<E> selectBySql(String sql, Object... args);
 
}
