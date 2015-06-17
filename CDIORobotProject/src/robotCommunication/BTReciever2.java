package robotCommunication;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class BTReciever2 {

	private static DataOutputStream dos;
	private static final int FINISHED = 10;

	public static void main(String[] args) {

		double motorASpeed;
		double motorBSpeed;
		double motorCAngle;
		while (true) {

			BTConnection btc = Bluetooth.waitForConnection();

			DataInputStream dis = btc.openDataInputStream();
			dos = btc.openDataOutputStream();

			while (true) {
				try {
					motorASpeed = dis.readDouble();
					motorBSpeed = dis.readDouble();
					motorCAngle = dis.readDouble();
					if (motorASpeed == -1 || motorBSpeed == -1
							|| motorCAngle == -1) {
						continue;
					}

					Motor.A.setSpeed((float) motorASpeed);
					Motor.B.setSpeed((float) motorBSpeed);
					if (motorASpeed < 0.0)
						Motor.A.forward();
					else
						Motor.A.backward();
					if (motorBSpeed < 0.0)
						Motor.B.forward();
					else
						Motor.B.backward();

					Motor.C.rotate((int) motorCAngle);
					dos.writeInt(FINISHED);
					dos.flush();

				} catch (Exception e) {
					LCD.drawString("Error happend", 1, 1);
					System.exit(-1);
				}
			}
		}

	}

}
