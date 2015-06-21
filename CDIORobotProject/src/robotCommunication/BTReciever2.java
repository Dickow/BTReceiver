package robotCommunication;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class BTReciever2 {

	private static DataOutputStream dos;
	private static final int QUIT = -2, TURNLEFT = 1, TURNRIGHT = 2,
			FORWARD = 3, BACKWARDS = 4, STOP = 5, OPEN = 6, CLOSE = 7,
			DELIVER = 8, CALIBRATE = 9, FINISHED = 10, HASBALL = 11,
			GOTBALL = 12, NOBALL = 13;
	private static int initialAngle, openAngle, deliverAngle;

	public static void main(String[] args) {

		UltrasonicSensor sensor = new UltrasonicSensor(SensorPort.S1);
		double motorASpeed;
		double motorBSpeed;
		while (true) {

			BTConnection btc = Bluetooth.waitForConnection();

			DataInputStream dis = btc.openDataInputStream();
			dos = btc.openDataOutputStream();
			initialAngle = Motor.C.getPosition();
			openAngle = initialAngle + 40;
			deliverAngle = initialAngle + 100;
			while (true) {
				try {
					int n = dis.readInt();
					int currentAngle = Motor.C.getPosition();
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

						Motor.A.backward();
						Motor.B.forward();

						dos.writeInt(FINISHED);
						dos.flush();
						break;

					case TURNRIGHT:
						motorASpeed = dis.readDouble();
						motorBSpeed = dis.readDouble();

						Motor.A.setSpeed((float) motorASpeed);
						Motor.B.setSpeed((float) motorBSpeed);

						Motor.A.forward();
						Motor.B.backward();
						dos.writeInt(FINISHED);
						dos.flush();
						break;

					case FORWARD:

						motorASpeed = dis.readDouble();
						motorBSpeed = dis.readDouble();

						Motor.A.setSpeed((float) motorASpeed);
						Motor.B.setSpeed((float) motorBSpeed);
						if (motorASpeed < 0) {
							Motor.A.backward();
							Motor.A.setSpeed((float) -motorASpeed);
						} else {
							Motor.A.forward();
						}

						if (motorBSpeed < 0) {
							Motor.B.setSpeed((float) -motorBSpeed);
							Motor.B.backward();
						} else {
							Motor.B.forward();
						}

						dos.writeInt(FINISHED);
						dos.flush();
						break;

					case BACKWARDS:

						break;

					case STOP:

						Motor.A.stop(true);
						Motor.B.stop();
						dos.writeInt(FINISHED);
						dos.flush();
						break;

					case OPEN:

						Motor.A.stop(true);
						Motor.B.stop();

						Motor.C.rotate(openAngle - currentAngle);

						dos.writeInt(FINISHED);
						dos.flush();
						break;

					case CLOSE:

						Motor.A.stop(true);
						Motor.B.stop();
						// close the arms
						Motor.C.rotate(initialAngle - currentAngle);

						// go back a little
						Motor.A.rotate(-260, true);
						Motor.B.rotate(-260);

						dos.writeInt(FINISHED);
						dos.flush();
						break;

					case HASBALL:

						int value = sensor.getDistance();
						Thread.sleep(40L);

						// say we are finished and then write the value to the
						// connector.
						dos.writeInt(FINISHED);
						// if the distance is less or equal to 8 then we have a
						// ball
						if (value <= 9) {
							dos.writeInt(GOTBALL);
						} else {
							dos.writeInt(NOBALL);
						}
						dos.flush();

						break;
					case DELIVER:

						Motor.A.stop(true);
						Motor.B.stop();
						Motor.C.rotate(deliverAngle - currentAngle, true);

						Motor.A.rotate(150, true);
						Motor.B.rotate(150);
						// do the delivery routine

						Motor.A.rotate(-360, true);
						Motor.B.rotate(-360);
						// tell the computer that we executed the command
						// close the arms again afterwards, we make sure to open
						// them at another time
						Motor.C.rotate(initialAngle - currentAngle);
						dos.writeInt(FINISHED);
						dos.flush();
						break;

					case CALIBRATE:
						// TODO
						Motor.A.rotate(360, true);
						Motor.B.rotate(360);

						dos.writeInt(FINISHED);
						dos.flush();
						break;
					}
					// we are done so write that back to the program

				} catch (Exception e) {
					LCD.drawString("Error happend", 1, 1);
					System.exit(-1);
				}
			}
		}

	}

	private static int sum(int[] values) {
		int sum = 0;
		for (int i : values) {
			sum += i;
		}
		return sum;
	}
}
