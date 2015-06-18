package robotCommunication;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class BTReciever2 {

	private static DataOutputStream dos;
	private static final int QUIT = -2, TURNLEFT = 1, TURNRIGHT = 2,
			FORWARD = 3, BACKWARDS = 4, STOP = 5, OPEN = 6, CLOSE = 7,
			DELIVER = 8, CALIBRATE = 9, FINISHED = 10;

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
					int n = dis.readInt();
					switch (n) {
					case QUIT:
						dis.close();
						Thread.sleep(100); // wait for data to drain
						LCD.clear();
						LCD.drawString("exiting", 0, 0);
						LCD.refresh();
						btc.close();
						LCD.clear();
						break;
					case TURNLEFT:

						motorASpeed = dis.readDouble();
						motorBSpeed = dis.readDouble();

						Motor.A.setSpeed((float) motorASpeed);
						Motor.B.setSpeed((float) motorBSpeed);

						Motor.A.forward();
						Motor.B.backward();

						break;

					case TURNRIGHT:
						motorASpeed = dis.readDouble();
						motorBSpeed = dis.readDouble();

						Motor.A.setSpeed((float) motorASpeed);
						Motor.B.setSpeed((float) motorBSpeed);

						Motor.A.backward();
						Motor.B.forward();
						break;

					case FORWARD:

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

						break;

					case BACKWARDS:

						break;

					case STOP:

						Motor.A.stop(true);
						Motor.B.stop();

						dos.writeInt(FINISHED);
						break;

					case OPEN:

						Motor.A.stop(true);
						Motor.B.stop();
						Motor.C.rotate(40);

						dos.writeInt(FINISHED);

						break;

					case CLOSE:
						
						Motor.A.stop(true);
						Motor.B.stop();
						
						Motor.C.rotate(-40);

						dos.writeInt(FINISHED);

						break;

					case DELIVER:

						Motor.C.rotate(100, true);

						Motor.A.rotate(150, true);
						Motor.B.rotate(150);
						// do the delivery routine

						Motor.A.rotate(-180, true);
						Motor.B.rotate(-180);
						// tell the computer that we executed the command
						// close the arms again afterwards, we make sure to open
						// them at another time
						Motor.C.rotate(-100);
						dos.writeInt(FINISHED);
						break;

					case CALIBRATE:
						// TODO
						Motor.A.rotate(360, true);
						Motor.B.rotate(360);

						dos.writeInt(FINISHED);
						break;
					}
					// we are done so write that back to the program
					dos.flush();

				} catch (Exception e) {
					LCD.drawString("Error happend", 1, 1);
					System.exit(-1);
				}
			}
		}

	}
}
