package MapReduce;

import java.util.Arrays;

public class MapReduceMain {
    
	//KDTreeMain index src dest
	//KDTreeMain range_query r tree output
	//TODO: KDTreeMain knn ...
	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("Invalid syntax.");
			return;
		}
		
		String sub_cmd = args[0];
	
		String [] arguments = Arrays.copyOfRange(args, 1,  args.length);
	//	String sub_cmd="index";
	//	String []arguments={"/usr/input/format_lakes.txt","/usr/output/"};
		if(sub_cmd.equals("index")) {
				MapReduceTreeBuild.run(arguments);
		}  else {
				System.out.println("Unrecognized sub commands.");
		}
	}

}
