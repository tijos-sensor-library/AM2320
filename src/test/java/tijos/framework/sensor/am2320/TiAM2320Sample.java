package tijos.framework.sensor.am2320;

import java.io.IOException;

import tijos.framework.devicecenter.TiI2CMaster;
import tijos.framework.sensor.am2320.TiAM2320;
import tijos.framework.util.Delay;

public class TiAM2320Sample {

	public static void main(String[] args) {

		try {

			/*
			 * 定义使用的TiI2CMaster port
			 */
			int i2cPort0 = 0;

			/*
			 * 资源分配， 将i2cPort0分配给TiI2CMaster对象i2c0
			 */
			TiI2CMaster i2c0 = TiI2CMaster.open(i2cPort0);

			TiAM2320 am2320 = new TiAM2320(i2c0);

			int num = 100;
			while (num-- > 0) {
				try {

					am2320.measure();

					System.out
							.println("humidity = " + am2320.getHumidity() + "temperature = " + am2320.getTemperature());

					Delay.msDelay(2000);
				} catch (Exception ex) {

					ex.printStackTrace();
				}

			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}

	}
}
