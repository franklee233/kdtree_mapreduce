package kdtreeStructure;


import java.util.Arrays;
import java.util.Comparator;
import java.util.ArrayList;

import dataSet.KDTreeData;

public class KDTreeBuildUp{
	protected KDTreeNodeStructure root = null;
	protected KDTreeData [] data = null;
	protected int k = 2;
	////////////////create//////////////////////
	public KDTreeBuildUp(KDTreeData [] data) {
		if(data == null || data.length == 0) return; 
		k = data[0].getDim();
		this.data = data;
		int [][] dataIdxInOrder = new int[k][data.length];
		//sort in two directions
		for(int j=0; j<k; j++ ) {
			for(int i=0; i<data.length; i++) {
				dataIdxInOrder[j][i] = i;
			}
			sortIdxInAix(dataIdxInOrder[j], j);
		}
		
		root = makeSubKDTree(dataIdxInOrder, 0);
	}
	
	//get the left most one if mids are the same
	int findMidIdx(int [] idxArr, int aix) {

		int idx = idxArr.length/2;
		if(idx == 0) return 0;
		idx--;
		while(idx>=0 && 
				data[idxArr[idx+1]].getPosAtAix(aix) == 
				data[idxArr[idx]].getPosAtAix(aix)) {
			idx--;
		}
		return idx+1;
	}
		
	//idxArr is a k * n array
	//  idxArr[i] stores array of idx in order of i-th direction
	//  time complexity T(n) = 2*T(n/2)+k*n => T(n) = nlogn
	private KDTreeNodeStructure makeSubKDTree(int [][] idxArr, int depth) {
		if(idxArr[0].length == 0) return null;
		
		int n = idxArr[0].length;
		int curAix = depth%k;
		
		int pivot = findMidIdx(idxArr[curAix], curAix);
		int pivotDataIdx = idxArr[curAix][pivot];
		KDTreeData pivotData = data[pivotDataIdx];
		
		int [][] leftData = new int[k][pivot];
		int [][] rightData = new int[k][n-pivot-1];
		
		//for each direction, we need to generate new sorted array in order
		//  time complexity k*n
		for(int i=0; i<k; i++) {
			int leftIdx = 0;
			int rightIdx = 0;
			for(int j=0; j<n; j++) {
				int dataIdx = idxArr[i][j];
				if(dataIdx == pivotDataIdx) {
					continue;
				}
				if(data[dataIdx].getPosAtAix(curAix) 
						< pivotData.getPosAtAix(curAix)) {
					leftData[i][leftIdx++] = dataIdx;
				} else {
					rightData[i][rightIdx++] = dataIdx;
				}
			}
		}
			
		//Recursion
		//  time complexity 2*T(n/2)
		KDTreeNodeStructure lc = makeSubKDTree(leftData, depth+1);	
		KDTreeNodeStructure rc = makeSubKDTree(rightData, depth+1);
			
		return new KDTreeNodeStructure(pivotDataIdx, lc, rc);
	}
		

	private void sortIdxInAix(int [] idxArr, int aix) {
		Cmp cmp = new Cmp(data, aix);
		Integer [] idxArrObj = new Integer[idxArr.length];
		for(int i=0; i<idxArr.length; i++) {
			idxArrObj[i] = idxArr[i];
		}
		Arrays.sort(idxArrObj, cmp);
		for(int i=0; i<idxArrObj.length; i++) {
			idxArr[i] = idxArrObj[i].intValue();
		}
	}
	
	//sort idxArr
	private class Cmp implements Comparator<Integer> {
		private KDTreeData [] dataChunk;
		private int aix;
		public Cmp(KDTreeData [] dataChunk, int aix) {
			this.dataChunk = dataChunk;
			this.aix = aix;
		}
		@Override
		public int compare(Integer idx0, Integer idx1) {
			return dataChunk[idx0.intValue()].getPosAtAix(aix) 
					- dataChunk[idx1.intValue()].getPosAtAix(aix);
		}
		
	}
	
	///////////range query//////////////
	//acc is accumulator
	
	

}
