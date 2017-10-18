package org.spring.springboot.service;

import org.spring.springboot.dao.domain.User;

/**
 * Created by ddfhznq on 2017/10/18.
 */
public interface UserService
{

     void save (User user);
     User getById(int id);
}
