package robotCommunication;

import lejos.nxt.Motor;

public class TestSpeed {
	private static final int MOTORSPEED = 1000; 
	public static void main(String[] args) {
		while(true){
		Motor.A.forward();
		Motor.B.forward();
		}
		
		
		
	}

}
