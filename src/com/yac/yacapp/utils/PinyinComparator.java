package com.yac.yacapp.utils;

import java.util.Comparator;

import com.yac.yacapp.domain.CarBrand;

public class PinyinComparator implements Comparator<CarBrand> {

	@Override
	public int compare(CarBrand c1, CarBrand c2) {
		if (c1.first_letter.equals("@") || c2.first_letter.equals("#")) {
			return -1;
		} else if (c1.first_letter.equals("#") || c2.first_letter.equals("@")) {
			return 1;
		} else {
			return c1.first_letter.compareTo(c2.first_letter);
		}
	}

}
