package org.spring.springboot.dao.domain.repository;

import org.spring.springboot.dao.domain.User;
import org.springframework.data.repository.CrudRepository;

import javax.annotation.Resource;

/**
 * Created by ddfhznq on 2017/10/18.
 */
public interface UserRepository extends CrudRepository<User,Integer>
{

}
