package com.uestc;

import java.io.Serializable;

/**
 * @Author: mhy
 * @Description:
 * @Date:Create：in 2022/5/4 12:27
 * @Modified By：
 */
public class Student implements Serializable {
    private Integer id;
    private String name;
    private Integer age;
    private Grade grade;

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", grade=" + grade +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }
}