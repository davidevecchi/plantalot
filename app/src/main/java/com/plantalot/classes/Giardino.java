package com.plantalot.classes;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.plantalot.components.InputDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Giardino {

//	public enum Color {  // TODO
//		GREEN, BLUE, RED, YELLOW, ORANGE, PURPLE, PINK,
//	}
	
	private String nome;
	private LatLngGiardino pos;
	private HashMap<String, Orto> orti;  // FIXME !!!!
	private Carriola carriola;  // FIXME !!!!!
//    Color c;
	
	public Giardino() {
		orti = new HashMap<>();
		carriola = new Carriola();
	}
	
	public Giardino(String nome, LatLng pos) {
		this();
		this.nome = nome;
		this.pos = new LatLngGiardino(pos);
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public LatLngGiardino getPos() {
		return pos;
	}
	
	public HashMap<String, Orto> getOrti() {
		return orti;
	}
	
	public ArrayList<Orto> ortiList() {
		return new ArrayList<>(orti.values());
	}
	
	public Map<String, Object> toMap() {
		HashMap<String, Object> result = new HashMap<>();
		result.put("name", nome);
		result.put("position", pos);
		result.put("orti", orti);
		return result;
	}
	
	public int calcArea() {
		int area = 0;
		for (Orto orto : orti.values()) area += orto.calcArea();
		return area;
	}
	
	public int plantedArea() {
		int area = 0;
		for (Orto orto : orti.values()) area += orto.plantedArea();
		return area;
	}
	
	public Carriola getCarriola() {
		return carriola;
	}
	
	public void fetchVarieta() {
		carriola.fetchVarieta();
		for (Orto orto : orti.values()) {
			orto.fetchVarieta();
		}
	}
	
	public void setCarriola(Carriola carriola) {
		this.carriola = carriola;
	}
	
	public void addOrto(Orto orto) {
		if (!orti.containsKey(orto.getNome())) {
			orti.put(orto.getNome(), orto);
		} else {
			// TODO
		}
	}
	
	public void editNomeOrto(Orto orto, String newName) {
		removeOrto(orto);
		orto.setNome(newName);
		addOrto(orto);
	}
	
	public void setOrti(HashMap<String, Orto> orti) {
		this.orti = (HashMap<String, Orto>) orti;
	}
	
	public void removeOrto(@NonNull Orto orto) {
		orti.remove(orto.getNome());
	}
	
}
