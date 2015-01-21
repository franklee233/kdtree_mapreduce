package dataSet;

import java.nio.ByteBuffer;


public class readInput {

	public static objectBase getObjFromLine(String line) {
		if (line.contains("EMPTY"))
			return null;

		int pos1 = line.indexOf('(');
		int pos2 = line.indexOf(')');
		String data = line.substring(pos1 + 1, pos2);
		String delims = "[,| ]+";
		String[] strings = data.split(delims);
		if (((strings.length) & 1) == 1)
			return null;
		java.util.List<Double> nums = new java.util.ArrayList<Double>();
		for (int i = 0; i < strings.length; i++)
			nums.add(Double.parseDouble(strings[i]));

		int npoints = strings.length / 2;
		double[] xs = new double[npoints];
		double[] ys = new double[npoints];
		for (int i = 0; i < npoints; i++) {
			xs[i] = nums.get(2 * i);
			ys[i] = nums.get(2 * i + 1);
		}
		return new objPoly(xs, ys, npoints);
	}


}
