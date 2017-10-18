package org.spring.springboot.controller;

import org.spring.springboot.dao.domain.User;
import org.spring.springboot.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by ddfhznq on 2017/10/18.
 */
@RestController
@RequestMapping("/user")
public class UserController
{
    @Resource
    private UserService userService;

    @RequestMapping("/save")
    public User saveUser()
    {
        User user = new User();
        user.setName("znq");
        user.setDescription("test");
        user.setUserName("test");
        userService.save(user);
        return user;
    }

    @RequestMapping("/findOne")
    public User getUserById(@RequestParam Integer id)
    {
        return userService.getById(id);
    }
}
