package com.plantalot.utils;

import java.util.HashMap;

public class Consts {
	
	public static final int CARD_PLANTS = 8;


	public static final HashMap<String, Integer> colors = new HashMap<>();

	static {
		colors.put("Chenopodiacee", 0xff4CBB17);
		colors.put("Composite", 0xff8B4492);
		colors.put("Crucifere", 0xff556B2F);
		colors.put("Cucurbitacee", 0xff228B22);
		colors.put("Graminacee", 0xffFFD700);
		colors.put("Lamiacee", 0xffFFA387);
		colors.put("Leguminose", 0xffA0522D);
		colors.put("Liliacee", 0xffDB7093);
		colors.put("Malvacee", 0xffB7DD00);
		colors.put("Ombrellifere", 0xffFF8C00);
		colors.put("Solanacee", 0xffFF4500);
		colors.put("Valerianacee", 0xff8B2500);
	}
	
}
