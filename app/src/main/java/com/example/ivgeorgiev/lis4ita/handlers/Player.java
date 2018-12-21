package com.example.ivgeorgiev.lis4ita.handlers;

public class Player {

    String id;
    String nick_name;

    public Player(String id, String nick_name){
        this.id=id;
        this.nick_name=nick_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }
}
