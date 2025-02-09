package com.plantalot.classes;

import android.util.Log;

import com.plantalot.database.DbUsers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// FIXME un po' di casino nei metodi

public class User implements Serializable {
	private final String TAG = "User";
	
	private String username;
	private String email;
	private Map<String, Giardino> giardini;
	private String nome_giardino_corrente;
	
	public User() {
		giardini = new HashMap<>();
	}
	
	public User(String username, String email) {
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
	
	public Giardino getGiardino(String nome) {
		return giardini.get(nome);
	}
	
	public Giardino getGiardinoCorrente() {
		return giardini.get(getNome_giardino_corrente());
	}
	
	public String getFirstGiardinoName() {
		if (giardini == null || giardini.size() == 0) return null;
		return giardini.entrySet().iterator().next().getKey();
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
	
	public boolean addGiardino(Giardino giardino) {
		if (getGiardiniNames().contains(giardino.getNome())) return false;
		if (giardini.isEmpty()) setNome_giardino_corrente(giardino.getNome());
		giardini.put(giardino.getNome(), giardino);
		setNome_giardino_corrente(giardino.getNome());
		DbUsers.updateGiardino(giardino);
		return true;
	}
	
	public void removeGiardino(String nomeGiardino) {
		giardini.remove(nomeGiardino);
		DbUsers.removeGiardino(nomeGiardino);
		setNome_giardino_corrente(getFirstGiardinoName());
	}
	
	public List<String> getGiardiniNames() {
		return new ArrayList<>(giardini.keySet());
	}
	
	public void setGiardini(Map<String, Giardino> giardini) {
		this.giardini = giardini;
	}
	
	public String getNome_giardino_corrente() {
		if (this.nome_giardino_corrente == null) return this.nome_giardino_corrente = getFirstGiardinoName();
		return this.nome_giardino_corrente;
	}
	
	public void setNome_giardino_corrente(String nomeGiardino) {
		this.nome_giardino_corrente = nomeGiardino;
		DbUsers.updateNomeGiardinoCorrente(nomeGiardino);

		Log.d(TAG, "Updating giardino " + nomeGiardino);
	}
	
	public boolean editNomeGiardino(String oldName, String newName) {
		if (oldName.equals(newName)) return true;
		if (getGiardiniNames().contains(newName)) return false;
		Giardino giardino = giardini.get(oldName);
		giardino.setNome(newName);
		giardini.put(newName, giardino);
		giardini.remove(oldName);
		DbUsers.updateNomeGiardino(oldName, newName);
		if (nome_giardino_corrente.equals(oldName) || nome_giardino_corrente == null) {
			setNome_giardino_corrente(newName);
		}
		return true;
	}
	
}
