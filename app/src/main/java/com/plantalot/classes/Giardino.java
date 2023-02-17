package com.plantalot.classes;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.plantalot.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Giardino {
	// TODO: add color to giardino
	
	private String nome;
	private String pos;
	//	private LatLngGiardino pos;
	private HashMap<String, Orto> orti;
	private PlantsCounter carriola;
	
	public Giardino() {
		orti = new HashMap<>();
		carriola = new PlantsCounter();
	}
	
	public Giardino(String nome, String pos) {
//	public Giardino(String nome, LatLng pos) {
		this();
		this.nome = nome;
		this.pos = pos;
//		if (pos != null) this.pos = new LatLngGiardino(pos);
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getPos() {
//	public LatLngGiardino getPos() {
		return pos;
	}
	
	public HashMap<String, Orto> getOrti() {
		return orti;
	}

//	@RequiresApi(api = Build.VERSION_CODES.N)
//	public HashMap<String, Integer> getFamiglieCount() {
//		HashMap<String, Integer> famiglie = new HashMap<>();
//		for (Orto orto : orti.values()) {
//			PlantsCounter ortaggi = orto.getOrtaggi();
//			for (String ortaggio : ortaggi.nomiOrtaggi()) {
//				String varieta = ortaggi.nomiVarieta(ortaggio).iterator().next();
//				Varieta varietaObj = ortaggi.getVarieta(ortaggio, varieta);
//				if (varietaObj != null) {
//					String famiglia = varietaObj.getTassonomia_famiglia();
//					int count = ortaggi.countPiante(ortaggio);
//					if (count > 0) {
//						famiglie.merge(famiglia, ortaggi.countPiante(ortaggio), Integer::sum);
//					}
//				}
//			}
//		}
//		return famiglie;
//	}
	
	public ArrayList<String> getOrtiNames() {
		return new ArrayList<>(orti.keySet());
	}
	
	public ArrayList<Orto> ortiList() {
		ArrayList<Orto> ortiValues = new ArrayList<>(orti.values());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Collections.sort(ortiValues, Comparator.comparing(Orto::getNome));
		}
		return ortiValues;
	}
	
	public PlantsCounter getCarriola() {
		return carriola;
	}
	
	@RequiresApi(api = Build.VERSION_CODES.N)
	public PlantsCounter getPlantsCounter() {
		PlantsCounter plantsCounter = new PlantsCounter();
		for (Orto orto : orti.values()) {
			PlantsCounter ortaggi = orto.getOrtaggi();
			for (String ortaggio : ortaggi.nomiOrtaggi()) {
				for (String varieta : ortaggi.nomiVarieta(ortaggio)) {
					plantsCounter.merge(ortaggio, varieta, ortaggi.getVarieta(ortaggio, varieta), ortaggi.getPianteCount(ortaggio, varieta));
				}
			}
		}
//		for (String ortaggio : plantsCounter.nomiOrtaggi()) {
//			for (String varieta : plantsCounter.nomiVarieta(ortaggio)) {
//				Log.wtf(ortaggio, varieta + " " + plantsCounter.getPianteCount(ortaggio, varieta));
//			}
//		}
		return plantsCounter;
	}
	
	public void setCarriola(PlantsCounter carriola) {
		this.carriola = carriola;
	}
	
	public void setOrti(HashMap<String, Orto> orti) {
		this.orti = (HashMap<String, Orto>) orti;
	}
	
	public void removeOrto(@NonNull Orto orto) {
		orti.remove(orto.getNome());
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
	
	public void fetchVarieta() {
		carriola.fetchVarieta();
		for (Orto orto : orti.values()) {
			orto.fetchVarieta();
		}
	}
	
	public void addOrto(Orto orto) {
		orti.put(orto.getNome(), orto);
	}
	
	public void editNomeOrto(Orto orto, String newName) {
		removeOrto(orto);
		orto.setNome(newName);
		addOrto(orto);
	}
	
	public String checkNomeOrto(String oldName, String newName, Context context) {
		if (oldName.equals(newName)) {
			return null;
		} else if (newName.isEmpty()) {
			return context.getString(R.string.errore_campo_vuoto);
		} else if (getOrtiNames().contains(newName)) {
			return context.getString(R.string.errore_nome_esistente);
		} else {
			return null;
		}
	}
}
