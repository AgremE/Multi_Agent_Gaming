package pomcpUtili;

public class BelifeParticle {
	
	private int[] particle;
	private int value;
	
	public BelifeParticle(int[] particle, int value){
		this.particle = particle;
		this.value = value;
	}
	public int getValue(){
		return this.value;
	}
	public int[] getParticle(){
		return this.particle;
	}
	public void setValue(int value){
		this.value = value;
	}
	public void setParticle(int[] particle){
		this.particle = particle;
	}
}
