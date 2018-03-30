package com.schaffer.base.test;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class GreenDaoUser {

    @Id(autoincrement = true)
    private Long id;

    private String firstName;

    @Transient
    private String lastName;

    private int age;

    private int gender;

    @Generated(hash = 1493051644)
    public GreenDaoUser(Long id, String firstName, int age, int gender) {
        this.id = id;
        this.firstName = firstName;
        this.age = age;
        this.gender = gender;
    }

    @Generated(hash = 83249558)
    public GreenDaoUser() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GreenDaoUser{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                '}';
    }
}
