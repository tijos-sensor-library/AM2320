package tijos.framework.sensor.am2320;

import java.io.IOException;

import tijos.framework.devicecenter.TiI2CMaster;
import tijos.util.BigBitConverter;
import tijos.util.Delay;
import tijos.util.LittleBitConverter;

/**
 * TiJOS-AM232X Temperature Humidity Sensor Library
 * AM2320,AM2321,AM2322 sensor driver with I2C bus
 * @author TiJOS 
 */
public class TiAM2320 {

	/**
	 * TiI2CMaster object
	 */
	private TiI2CMaster i2cmObj;

	/**
	 * TiAM2320 I2C device address
	 */
	private static int AM2320_ADDR = 0x5C;

	private double humidity = Double.NaN;

	private double temperature = Double.NaN;

	public TiAM2320(TiI2CMaster i2c) {
		if (i2c == null)
			throw new NullPointerException("TiI2CMaster object is required");

		this.i2cmObj = i2c;
	}

	/**
	 * Startup measurement
	 * 
	 * @return 0:success, < 0 : IO error
	 */
	public void measure() throws IOException {
		synchronized (i2cmObj) {

			byte[] buf = new byte[16];

			// step 1:wake up
			try{
				i2cmObj.write(AM2320_ADDR, null, 0, 0, true);
			}
			catch(IOException ie){
				
			}
			// step 2:command
			// Get Humidity and Temperature
			buf[0] = 0x03;

			// Start address
			buf[1] = 0x00;

			// Length
			buf[2] = 0x04;

			i2cmObj.write(AM2320_ADDR, buf, 0, 3);

			Delay.msDelay(2);

			// step 3:data
			i2cmObj.read(AM2320_ADDR, buf, 0, 8);

			// step 4: CRC validation
			int crc = LittleBitConverter.ToInt32(buf, 6);
			int crc2 = crc16(buf, 6);

			if (crc != crc2) {
				throw new IOException("CRC error");
			}

			// step 5:
			this.humidity = BigBitConverter.ToInt16(buf, 2);

			// has sign bit < 0
			short temp = BigBitConverter.ToInt16(buf, 4);
			this.temperature = temp;

			this.humidity /= 10.0; // unit in %RH
			this.temperature /= 10.0; // unit in degree
		}
	}

	/**
	 * get the temperature result unit in degree
	 * 
	 * @return temperature
	 */
	public double getTemperature() {
		return this.temperature;
	}

	/**
	 * get the humidity result unit in %RH
	 * 
	 * @return humidity
	 */
	public double getHumidity() {
		return this.humidity;
	}

	/**
	 * CRC calculation
	 * 
	 * @param ptr
	 *            input data
	 * @param len
	 *            data length
	 * @return crc value
	 */
	private static int crc16(byte[] ptr, int len) {
		int crc = 0xFFFF;
		int i;

		int idx = 0;
		while (len-- > 0) {
			crc ^= ptr[idx++] & 0xFF;
			for (i = 0; i < 8; i++) {
				if ((crc & 0x01) > 0) {
					crc >>= 1;
					crc ^= 0xA001;
					crc &= 0xffff;

				} else {
					crc >>= 1;
				}
			}
		}

		return crc;
	}

}
