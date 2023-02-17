package com.plantalot.classes;

import android.os.Build;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.plantalot.database.DbPlants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


// Una carriola per giardino, contiene le varietà aggiunte alla carriola e le relative quantità

public class PlantsCounter {
	
	private final HashMap<String, HashMap<String, Integer>> countMap;  // <nomeOrtaggio, <nomeVarieta, quantita>>
	private final HashMap<String, Varieta> varietaMap;                 // <nomeVarieta, varieta>
	
	public PlantsCounter() {
		this.countMap = new HashMap<>();
		this.varietaMap = new HashMap<>();
	}
	
	public PlantsCounter(PlantsCounter other) {
		this.countMap = new HashMap<>(other.countMap.size());
		for (HashMap.Entry<String, HashMap<String, Integer>> entry : other.countMap.entrySet()) {
			this.countMap.put(entry.getKey(), new HashMap<>(entry.getValue()));
		}
		this.varietaMap = new HashMap<>(other.varietaMap);
	}
	
	public HashMap<String, HashMap<String, Integer>> getCountMap() {  // used only for the DB
		return countMap;
	}
	
	public Varieta getVarieta(String ortaggio, String varieta) {
		return varietaMap.get(ortaggio + varieta);
	}
	
	public List<Varieta> varietaList() {
		List<Varieta> list = new ArrayList<>(varietaMap.values());
		Collections.sort(list);
		return list;
	}
	
	public int getPianteCount(String ortaggio, String varieta) {
		return countMap.get(ortaggio).get(varieta);
	}
	
	public void remove(String ortaggio, String varieta) {
		if (countMap.get(ortaggio) != null) {
			countMap.get(ortaggio).remove(varieta);
			if (isEmpty(ortaggio)) countMap.remove(ortaggio);
		}
		varietaMap.remove(ortaggio + varieta);
	}
	
	public void removeEmpty() {
		for (String ortaggio : nomiOrtaggi()) {
			for (String varieta : nomiVarieta(ortaggio)) {
				if (getPianteCount(ortaggio, varieta) == 0) {
					remove(ortaggio, varieta);
				}
			}
		}
	}
	
	public void clear() {
		countMap.clear();
		varietaMap.clear();
	}
	
	public void put(String ortaggio, String varieta, Integer count) {
		if (countMap.get(ortaggio) == null) countMap.put(ortaggio, new HashMap<>());
		countMap.get(ortaggio).put(varieta, count);
		if (varietaMap.get(ortaggio + varieta) == null) {
			FirebaseFirestore db = FirebaseFirestore.getInstance();
			db.collection("varieta")
					.whereEqualTo(DbPlants.VARIETA_CLASSIFICAZIONE_ORTAGGIO, ortaggio)
					.whereEqualTo(DbPlants.VARIETA_CLASSIFICAZIONE_VARIETA, varieta)
					.get().addOnSuccessListener(queryDocumentSnapshots -> {
						Varieta varietaObj = queryDocumentSnapshots.iterator().next().toObject(Varieta.class);
						varietaMap.put(ortaggio + varieta, varietaObj);
					});
		}
	}
	
	@RequiresApi(api = Build.VERSION_CODES.N)
	public void merge(String ortaggio, String varieta, Varieta varietaObj, Integer count) {
		if (countMap.get(ortaggio) == null) countMap.put(ortaggio, new HashMap<>());
		countMap.get(ortaggio).merge(varieta, count, Integer::sum);
		varietaMap.put(ortaggio + varieta, varietaObj);
	}
	
	public void fetchVarieta() {
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		for (String ortaggio : nomiOrtaggi()) {
			List<String> varietaList = nomiVarieta(ortaggio);
			db.collection("varieta")
					.whereEqualTo(DbPlants.VARIETA_CLASSIFICAZIONE_ORTAGGIO, ortaggio)
					.whereIn(DbPlants.VARIETA_CLASSIFICAZIONE_VARIETA, varietaList)
					.get().addOnSuccessListener(queryDocumentSnapshots -> {
						for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
							Varieta varietaObj = document.toObject(Varieta.class);
							varietaMap.put(ortaggio + varietaObj.getClassificazione_varieta(), varietaObj);
						}
					});
		}
	}
	
	public boolean notEmpty() {
		return !countMap.isEmpty();
	}
	
	public boolean isEmpty(String ortaggio) {
		return countMap.get(ortaggio).isEmpty();
	}
	
	public boolean isEmpty() {
		return countMap.isEmpty();
	}
	
	public ArrayList<String> nomiOrtaggi() {
		return new ArrayList<>(countMap.keySet());
	}
	
	public ArrayList<String> nomiVarieta(String ortaggio) {
		return new ArrayList<>(countMap.get(ortaggio).keySet());
	}
	
	public int countVarieta(String ortaggio) {
		return countMap.get(ortaggio) != null ? countMap.get(ortaggio).size() : 0;
	}
	
	public int countOrtaggi() {
		return countMap.size();
	}
	
	@RequiresApi(api = Build.VERSION_CODES.N)
	public int countPiante() {
		int total = 0;
		for (String ortaggio : nomiOrtaggi()) {
			total += countPiante(ortaggio);
		}
		return total;
	}
	
	@RequiresApi(api = Build.VERSION_CODES.N)
	public int countPiante(String ortaggio) {
		if (countMap.get(ortaggio) == null) return 0;
		return (new ArrayList<>(countMap.get(ortaggio).values())).stream().mapToInt(Integer::intValue).sum();
	}
	
	public boolean contains(String ortaggio) {
		return countMap.get(ortaggio) != null;
	}
	
	public boolean contains(String ortaggio, String varieta) {
		return this.contains(ortaggio) && countMap.get(ortaggio).get(varieta) != null;
	}
	
	public int calcArea() {
		int area = 0;
		for (Varieta varieta : varietaMap.values()) {
			area += varieta.calcArea() * getPianteCount(
					varieta.getClassificazione_ortaggio(),
					varieta.getClassificazione_varieta());
		}
		Log.d("AREA", "" + area);
		return area;
	}
	
	public List<Pair<String, List<Pair<Varieta, Integer>>>> toList() {
		HashMap<String, List<Pair<Varieta, Integer>>> carriolaMap = new HashMap<>();
		for (Varieta varieta : varietaList()) {
			String ortaggioName = varieta.getClassificazione_ortaggio();
			String varietaName = varieta.getClassificazione_varieta();
			int pianteCount = getPianteCount(ortaggioName, varietaName);
			if (carriolaMap.get(ortaggioName) == null) {
				carriolaMap.put(ortaggioName, new ArrayList<>());
			}
			carriolaMap.get(ortaggioName).add(new Pair<>(varieta, pianteCount));
		}
		List<Pair<String, List<Pair<Varieta, Integer>>>> carriolaList = new ArrayList<>();
		List<String> ortaggiNames = new ArrayList<>(carriolaMap.keySet());
		Collections.sort(ortaggiNames);
		for (String ortaggio : ortaggiNames) {
			carriolaList.add(new Pair<>(ortaggio, carriolaMap.get(ortaggio)));
		}
		return carriolaList;
	}
	
	
	@RequiresApi(api = Build.VERSION_CODES.N)
	public HashMap<String, Integer> getFamiglieMeasure(int measure) {
		HashMap<String, Integer> famiglie = new HashMap<>();
		for (String ortaggio : this.nomiOrtaggi()) {
			String famiglia = "";
			for (String varieta : this.nomiVarieta(ortaggio)) {
				Varieta varietaObj = this.getVarieta(ortaggio, varieta);
				if (varietaObj != null) {
					famiglia = Objects.equals(famiglia, "") ? varietaObj.getTassonomia_famiglia() : famiglia;
					int count = this.getPianteCount(ortaggio, varieta);
					if (measure == 0) {  // FIXME enum !!!!!
						count *= varietaObj.getDistanze_file() * varietaObj.getDistanze_piante();
					} else if (measure == 2) {  // FIXME enum !!!!!
						count *= varietaObj.getProduzione_peso();  // FIXME udm !
					}
					if (count > 0) {
						famiglie.merge(famiglia, count, Integer::sum);
					}
				}
			}
		}
		return famiglie;
	}
	
}
