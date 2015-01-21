package kdtreeStructure;

class KDTreeNodeStructure {
	int data_idx = -1;
	KDTreeNodeStructure left = null;
	KDTreeNodeStructure right = null;
	
	public KDTreeNodeStructure(int idx, KDTreeNodeStructure lc, KDTreeNodeStructure rc) {
		data_idx = idx;
		left = lc;
		right = rc;
	}


}
