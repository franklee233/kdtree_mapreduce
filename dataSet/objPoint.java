package dataSet;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Writable;

public class objPoint extends objectBase implements Writable{

	public objPoint(double x0, double y0){
		x = x0;
		y = y0;
	}
	
	@Override
	public int getType() {
		return POINT;
	}
	
	private boolean intersects(double x0, double y0, double width, double height) {
		if( (x>=x0 && x<=x0+width) &&(y>=y0 && y<=y0+height) )
			return true;
		return false;
	}
	
	@Override
	public boolean intersects(Rectangle rect) {
		return intersects(rect.x, rect.y, rect.width, rect.height);
	}
	/*
	private boolean intersects(double x0, double y0, double radius) {
		double w = x - x0;
		double h = y - y0;
		if(w*w + h*h < radius * radius)
			return true;
		return false;
	}*/
	
	@Override
	public Iterator<objPoint> iterator() {
		return new PointIterator();
	}
	
	public class PointIterator implements Iterator<objPoint> {
		boolean firstCall = true;
	    public boolean hasNext() {
	        if(firstCall)
	        	return true;
	        return false;
	    }

	    public objPoint next() {
	    	firstCall = false;
	        return new objPoint(x,y);
	    }

	    public void remove() {
	    	// not supported.
	    }
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		x = in.readDouble();
		y = in.readDouble();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeDouble(x);
		out.writeDouble(y);
	}

	@Override
	public void DebugPrint() {
		System.out.println("[POINT] "+ x + " "+ y);
	}
	
	@Override
	public String toString() {
		return "[POINT] "+ x + " "+ y;
	}

	@Override
	public int size() {
		return 8*2;
	}
	
	@Override
	public Rectangle getMBR() {
		return new Rectangle(x,y,0.0,0.0);
	}

	private double x;
	private double y;

}
