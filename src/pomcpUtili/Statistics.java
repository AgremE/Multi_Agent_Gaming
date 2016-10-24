package pomcpUtili;


/*
 * Makara Phav @ Agreme
 * */
public class Statistics {

	int count;
	double mean;
	double variance;
	double min, max;
	
	public Statistics(){
		this.clear();
	}
	public void add(double reward){
		double meanOld = mean;
		int countOld = count;
		++count;
		mean += (reward-mean)/count;
		variance = (countOld * (variance + meanOld * meanOld)
                + reward * reward) / count - mean * mean;
		if(reward > max){
			max = reward;
		}
		if(reward < min){
			min = reward;
		}
	}
	public void clear(){
		count = 0;
	    mean = 0;
	    variance = 0;
	    min = Double.POSITIVE_INFINITY;
	    max = Double.NEGATIVE_INFINITY;
	}
	public double getTotal(){
		return mean*count;
	}
	public double getMean(){
		return mean;
	}
	public double getVariance(){
		return variance;
	}
	public double getStdDev(){
		return Math.sqrt(variance);
	}
	public double getStdErr(){
		return Math.sqrt(variance/mean);
	}
	public double getMax(){
		return max;
	}
	public double getMin(){
		return min;
	}
}
