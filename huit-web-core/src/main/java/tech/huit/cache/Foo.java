package tech.huit.cache;

import java.util.Date;

/**
 * Created by huit on 2017/8/15.
 */
class Foo {
    int id;
    String name;
    Date time;

    public Foo() {

    }

    public Foo(int id, String name, Date time) {
        this.id = id;
        this.name = name;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
