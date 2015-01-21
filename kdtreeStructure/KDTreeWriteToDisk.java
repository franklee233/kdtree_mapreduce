package kdtreeStructure;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

import dataSet.KDTreeData;
import dataSet.objRect;


public class KDTreeWriteToDisk extends KDTreeBuildUp implements Writable{
	public KDTreeWriteToDisk() {
		super(null);
	}

	public KDTreeWriteToDisk(KDTreeData[] data) {
		super(data);
    }

	@Override
	public void readFields(DataInput in) throws IOException {
	
		int data_size = in.readInt();	

		objRect [] rect_array = new objRect[data_size];
		for(int i=0; i<data_size; i++) {
			rect_array[i] = new objRect();
			rect_array[i].readFields(in);
		}
		
		this.data = rect_array;
		
		root = readSubTree(in);
		
	}

	private KDTreeNodeStructure readSubTree(DataInput in) throws IOException {
		boolean is_null = in.readBoolean();
		if(is_null) {
			return null;
		} else {
			int idx = in.readInt();
			KDTreeNodeStructure left = readSubTree(in);
			KDTreeNodeStructure right = readSubTree(in);
			return new KDTreeNodeStructure(idx, left, right);
		}
	}

	@Override
	public void write(DataOutput out) throws IOException {

		//write data
		out.writeInt(data.length);

		for(int i=0; i<data.length; i++) {
			objRect r = (objRect) data[i];
			r.write(out);

		}
		
		//write tree
		writeSubTree(root, out);
		
	}

	private void writeSubTree(KDTreeNodeStructure root, DataOutput out) throws IOException {
		if(root == null) {
			out.writeBoolean(true);

		} else {
			out.writeBoolean(false);
			out.writeInt(root.data_idx);

		
			writeSubTree(root.left, out);
			writeSubTree(root.right, out);
		}
	}

}
