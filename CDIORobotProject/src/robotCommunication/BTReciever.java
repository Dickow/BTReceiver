package robotCommunication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.Motor;

public class BTReciever {
	private static DataOutputStream dos;
	private static final int QUIT = -2, TURNLEFT = 1, TURNRIGHT = 2,
			FORWARD = 3, BACKWARDS = 4, STOP = 5, OPEN = 6, CLOSE = 7,
			DELIVER = 8, CALIBRATE = 9, FINISHED = 10;
	private static final int ANGLE_CALIBRATION = 4;

	public static void main(String[] args) throws Exception {
		String connected = "Connected";
		String closing = "Closing...";
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
			dos = btc.openDataOutputStream();
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
						turnLeft(dis);

						break;

					case TURNRIGHT:
						turnRight(dis);

						break;

					case FORWARD:
						robotForward(dis);

						break;

					case BACKWARDS:
						robotBackwards(dis);

						break;

					case STOP:

						Motor.A.stop(true);
						Motor.B.stop();

						dos.writeInt(FINISHED);
						break;

					case OPEN:
						Motor.C.rotate(40);

						dos.writeInt(FINISHED);

						break;

					case CLOSE:

						Motor.C.rotate(-40);

						dos.writeInt(FINISHED);

						break;

					case DELIVER:

						Motor.C.rotate(100, true);

						Motor.A.rotate(-50, true);
						Motor.B.rotate(-50);
						// do the delivery routine

						// tell the computer that we executed the command
						// close the arms again afterwards, we make sure to open
						// them at another time
						Motor.C.rotate(-100);
						dos.writeInt(FINISHED);
						break;

					case CALIBRATE:
						// TODO
						Motor.A.rotate(-360, true);
						Motor.B.rotate(-360);

						dos.writeInt(FINISHED);
						break;
					}
					// we are done so write that back to the program
					dos.flush();
				} catch (IOException e) {
					LCD.drawString("exception", 0, 2);
					LCD.refresh();
					System.exit(-1);
				}

			}
		}

	}

	private static synchronized void robotBackwards(DataInputStream dis)
			throws IOException {
		int backwardDistance = (int) (dis.readInt());
		backwardDistance = backwardDistance < 1 ? 1 : backwardDistance;

		Motor.A.rotate(backwardDistance, true);
		Motor.B.rotate(backwardDistance);

		dos.writeInt(FINISHED);
	}

	private static synchronized void robotForward(DataInputStream dis)
			throws IOException {
		int forwardDistance = (int) (dis.readDouble());
		forwardDistance = forwardDistance < 1 ? 1 : forwardDistance;
		
		Motor.A.rotate(-forwardDistance, true);
		Motor.B.rotate(-forwardDistance);

		dos.writeInt(FINISHED);
	}

	private static synchronized void turnRight(DataInputStream dis)
			throws IOException {
		int rightAngle = dis.readInt();
		if (rightAngle == 0) {
			rightAngle = 1;
		}
		if (rightAngle >= 15) {
			Motor.A.rotate(rightAngle / 2 * ANGLE_CALIBRATION, true);
			Motor.B.rotate((-rightAngle / 2) * ANGLE_CALIBRATION);

		} else if (rightAngle >= 10) {
			Motor.A.rotate(3 * ANGLE_CALIBRATION, true);
			Motor.B.rotate(-3 * ANGLE_CALIBRATION);
		} else {
			Motor.A.rotate(1 * ANGLE_CALIBRATION, true);
			Motor.B.rotate(-1 * ANGLE_CALIBRATION);
		}

		dos.writeInt(FINISHED);
	}

	private static synchronized void turnLeft(DataInputStream dis)
			throws IOException {
		int leftAngle = dis.readInt();
		if (leftAngle == 0) {
			leftAngle = 1;
		}
		if (leftAngle >= 15) {
			Motor.A.rotate((-leftAngle / 2) * ANGLE_CALIBRATION, true);
			Motor.B.rotate(leftAngle / 2 * ANGLE_CALIBRATION);
		} else if (leftAngle >= 10) {
			Motor.A.rotate(-3 * ANGLE_CALIBRATION, true);
			Motor.B.rotate(3 * ANGLE_CALIBRATION);

		} else {
			Motor.A.rotate(-1 * ANGLE_CALIBRATION, true);
			Motor.B.rotate(1 * ANGLE_CALIBRATION);
		}
		dos.writeInt(FINISHED);
	}

}
