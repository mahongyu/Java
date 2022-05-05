package com.uestc;

import java.io.Serializable;

/**
 * @Author: mhy
 * @Description:
 * @Date:Create：in 2022/5/4 12:29
 * @Modified By：
 */
public class Grade implements Serializable {
    private Integer id;
    private String name;

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", name='" + name + '\'' +
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
}