package org.spring.springboot.service.impl;

import org.spring.springboot.dao.domain.User;
import org.spring.springboot.dao.repository.UserRepository;
import org.spring.springboot.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by ddfhznq on 2017/10/18.
 */
@Service("userService")
public class UserServiceImpl implements UserService
{
    @Resource
    private UserRepository userRepository;
    @Override
    public void save(User user)
    {
        userRepository.save(user);
    }

    @Override
    public User getById(int id)
    {
        return userRepository.findOne(id);
    }
}
