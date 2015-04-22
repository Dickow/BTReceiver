package robotCommunication;

import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.Motor;

public class BTReciever {
	private static final int QUIT = -2, TURNLEFT = 1, TURNRIGHT = 2,
			FORWARD = 3, BACKWARDS = 4, STOP = 5, OPEN = 6, CLOSE = 7,
			DELIVER = 8;
	private static final int ANGLE_CALIBRATION = 4;

	public static void main(String[] args) throws Exception {
		String connected = "Connected";
		String waiting = "Waiting...";
		String closing = "Closing...";
		String excecuting = "Excecuting";
		String test = "Test";

		while (true) {

			LCD.clear();
			LCD.drawString(test, 0, 0);
			LCD.refresh();

			BTConnection btc = Bluetooth.waitForConnection();

			LCD.clear();
			LCD.drawString(connected, 0, 0);
			LCD.refresh();

			DataInputStream dis = btc.openDataInputStream();
			while (true) {
				try {
					int n = dis.readInt();
					LCD.clear();
					LCD.drawInt(n, 2, 0, 0);
					LCD.refresh();
					switch (n) {
					case QUIT:
						dis.close();
						Thread.sleep(100); // wait for data to drain
						LCD.clear();
						LCD.drawString(closing, 0, 0);
						LCD.refresh();
						btc.close();
						LCD.clear();
						break;
					case TURNLEFT:
						int leftAngle = dis.readInt();
						if(leftAngle == 0){leftAngle =1;}
						
						LCD.drawInt(leftAngle, 0, 1);
						Motor.A.rotate(-leftAngle*ANGLE_CALIBRATION,true);;
						Motor.B.rotate(leftAngle*ANGLE_CALIBRATION, true);
						
						break;

					case TURNRIGHT:
						int rightAngle = dis.readInt();
						if(rightAngle==0){rightAngle = 1;}
						
						Motor.A.rotate(rightAngle*ANGLE_CALIBRATION, true);
						Motor.B.rotate(-rightAngle*ANGLE_CALIBRATION, true);
						break;

					case FORWARD:
						int forwardDistance = dis.readInt();

						break;

					case BACKWARDS:
						int backwardDistance = dis.readInt();
						break;

					case STOP:
						
						break;

					case OPEN:

						break;

					case CLOSE:

						break;

					case DELIVER:

						break;
					}
				} catch (IOException e) {
					LCD.drawString("exception", 0, 2);
					LCD.refresh();
					continue; 
				}

			}
		}

	}

}
