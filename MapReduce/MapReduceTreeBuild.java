package MapReduce;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;






import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Counters;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;

import dataSet.objRect;
import dataSet.objectBase;
import dataSet.readInput;
import kdtreeStructure.KDTreeWriteToDisk;

class MapReduceTreeBuild {
	
	public static void run(String [] args) {
		String in_path = null;
		String out_path = null;
		in_path = args[0];
		out_path = args[1];		
		JobConf conf = new JobConf(MapReduceTreeBuild.class);
		
		conf.setJobName("index");
		
		conf.setMapperClass(KDTreeIndexMapper.class);
		conf.setReducerClass(KDTreeIndexReducer.class);
		
		conf.setMapOutputKeyClass(LongWritable.class);
		conf.setMapOutputValueClass(objRect.class);
		
		conf.setOutputKeyClass(LongWritable.class);
		conf.setOutputValueClass(KDTreeWriteToDisk.class);
		
		conf.setOutputFormat(SequenceFileOutputFormat.class);

		FileInputFormat.setInputPaths(conf, new Path(in_path));
		FileOutputFormat.setOutputPath(conf, new Path(out_path));
		try {
			JobClient.runJob(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

//input (key, value) = lineno, line
//output (key, value) = count to number of calls of map, Rect
class KDTreeIndexMapper extends MapReduceBase
	implements Mapper<LongWritable, Text, LongWritable, objRect>{
	
	static int inst_count = 0;
	
	static enum IndexCounters { SIZE_E, COUNT_E };
	
	final static int sizeof_boolean = 8;
	final static int sizeof_int = 8;
	// refer to QuadTreeWritable
	final static int max_block_size = 1024; //63*1024*1024;
	
	synchronized LongWritable getTreeId(objRect rect, Reporter rpt) {
		Counters.Counter size_cc = rpt.getCounter(IndexCounters.SIZE_E); 
		Counters.Counter count_cc = rpt.getCounter(IndexCounters.COUNT_E);
		//size of data plus tree node
		int dsize = rect.size() + sizeof_boolean*2 + sizeof_int;
		// new data size + original value + data_length size
		if(dsize + size_cc.getValue() + sizeof_int> max_block_size) {
			long cur=dsize-size_cc.getValue();
			size_cc.increment(cur);
			count_cc.increment(1);
			
			return new LongWritable (count_cc.getValue());
			
		} else {
			
			size_cc.increment(dsize);
			
			return new LongWritable (count_cc.getValue());
			
		}
	}

	@Override
	public void map(LongWritable no, Text content,
			OutputCollector<LongWritable, objRect> oc, Reporter rpt)
			throws IOException {

		/*
		 * if (npoints == 1) { // Point object = new Point(nums.get(1),
		 * nums.get(2)); } else if (npoints == 4) { // Rectangle // suppose
		 * order is first --- second // | | // | | // fourth--- third double
		 * width = Math.abs(nums.get(2) - nums.get(0)); // x2-x1 double height =
		 * Math.abs(nums.get(1) - nums.get(3)); // y1-y4
		 * 
		 * object = new Rectangle(nums.get(1), nums.get(2), width, height); }
		 * else if (npoints >= 6) { // Polygon
		 */
		String line = content.toString();
		objectBase obj = readInput.getObjFromLine(line);
		objRect rect = new objRect(obj);
		
		LongWritable tid = getTreeId(rect, rpt);
		

		
		oc.collect(tid, rect);
		
	}

}

//input (key, value) = count in each mapper, Rect
//output (key, value) = _, KDTree
class KDTreeIndexReducer extends MapReduceBase
	implements Reducer<LongWritable, objRect, LongWritable, KDTreeWriteToDisk>{

	static int count = 0;
	@Override
	public void reduce(LongWritable key, Iterator<objRect> value,
			OutputCollector<LongWritable, KDTreeWriteToDisk> oc, Reporter rpt)
			throws IOException {
		//Debug.println("reduce = " + count);
		count++;
		ArrayList<objRect> rlist = new ArrayList<objRect>();
		while(value.hasNext()) {
			objRect r = value.next();
			rlist.add(new objRect(r));
		}
		
		objRect [] rect_list = new objRect[rlist.size()];
		rect_list = rlist.toArray(rect_list);
		
		KDTreeWriteToDisk kdtree = new KDTreeWriteToDisk(rect_list);
		
		oc.collect(key,  kdtree);
		
	}
}

