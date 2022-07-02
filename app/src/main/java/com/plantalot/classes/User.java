package com.plantalot.classes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
    private final String TAG = "User";

    private String username;
    private String email;
    private Map<String, Giardino> giardini;
    private String nome_giardino_selected;

    public User(){
        giardini = new HashMap<>();
    }

    public User(String username, String email){
        this();
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Giardino getGiardino(String nome){
        return giardini.get(nome);
    }

    public Giardino getFirstGiardino(){
        if(giardini.size() == 0)
            return null;
        return giardini.entrySet().iterator().next().getValue();
    }

    public Map<String, Giardino> getGiardini() {
        return giardini;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addGiardino(Giardino giardino) {
        giardini.put(giardino.getName(), giardino);
    }

    public List<String> getGiardiniNames(){
        return new ArrayList<>(giardini.keySet());
    }

    public void setGiardini(Map<String, Giardino> giardini) {
        this.giardini = giardini;
    }

    public String getNome_giardino_selected() {
        return nome_giardino_selected;
    }

    public void setNome_giardino_selected(String nome_giardino_selected) {
        this.nome_giardino_selected = nome_giardino_selected;
    }
}
