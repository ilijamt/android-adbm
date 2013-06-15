package com.matoski.adbm.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import com.matoski.adbm.enums.IPMode;
import com.matoski.adbm.pojo.IP;

import android.util.Log;

/**
 * Network helper utilities
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class NetworkUtil {

	/**
	 * The tag used when logging with {@link Log}
	 */
	private static String LOG_TAG = NetworkUtil.class.getName();

	/**
	 * Convert a {@link Integer} based encoded IP address to {@link String}
	 * 
	 * @param addr
	 *            Address to convert
	 * 
	 * @return Converted IP address
	 */
	public static String intToIp(int addr) {
		Log.d(LOG_TAG, String.format("Converting %d address", addr));
		return ((addr & 0xFF) + "." + ((addr >>>= 8) & 0xFF) + "."
				+ ((addr >>>= 8) & 0xFF) + "." + ((addr >>>= 8) & 0xFF));
	}

	/**
	 * Gets the local network address
	 * 
	 * @return {@link IP} containing the IP addresses
	 */
	public static IP getLocalAddress() {
		return new IP(NetworkUtil.getLocalIPAddress(IPMode.ipv4),
				NetworkUtil.getLocalIPAddress(IPMode.ipv6));
	}

	/**
	 * Gets the local network address based on mode
	 * 
	 * @param mode
	 *            What IP address to retrieve, from {@link IPMode}
	 * 
	 * @return The IP address, or <code>null</code> if not found
	 */
	public static String getLocalIPAddress(IPMode mode) {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();

					switch (mode) {
						case ipv4:
							if (!inetAddress.isLoopbackAddress()
									&& InetAddressUtils
											.isIPv4Address(inetAddress
													.getHostAddress())) {
								return inetAddress.getHostAddress().toString();
							}
							break;
						case ipv6:
							String address = inetAddress.getHostAddress();
							if (!inetAddress.isLoopbackAddress()
									&& (InetAddressUtils.isIPv6Address(address)
											|| InetAddressUtils
													.isIPv6HexCompressedAddress(address) || InetAddressUtils
												.isIPv6StdAddress(address))) {
								return inetAddress.getHostAddress().toString();
							}
							break;

					}
				}
			}
		} catch (SocketException ex) {
			Log.e("Socket exception in GetIP Address of Utilities",
					ex.toString());
		}
		return null;
	}
}
