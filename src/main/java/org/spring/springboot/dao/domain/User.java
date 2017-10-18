package org.spring.springboot.dao.domain;

import javax.persistence.*;

/**
 * Created by ddfhznq on 2017/10/18.
 */
@Entity
@Table(name = "user")
public class User
{
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "user_name")
    private String userName;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }
}
