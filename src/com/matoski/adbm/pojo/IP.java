package com.matoski.adbm.pojo;

/**
 * Contains the IP4 and IP6 of the network, it's used to store the IP addresses
 * for an easier transfer mechanism
 * 
 * <pre>
 * new IP("127.0.0.1", "::1");
 * new IP("127.0.0.1");
 * </pre>
 * 
 * @author Ilija Matoski (ilijamt@gmail.com)
 */
public class IP {

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ipv4 == null) ? 0 : ipv4.hashCode());
		result = prime * result + ((ipv6 == null) ? 0 : ipv6.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof IP)) {
			return false;
		}
		IP other = (IP) obj;
		if (ipv4 == null) {
			if (other.ipv4 != null) {
				return false;
			}
		} else if (!ipv4.equals(other.ipv4)) {
			return false;
		}
		if (ipv6 == null) {
			if (other.ipv6 != null) {
				return false;
			}
		} else if (!ipv6.equals(other.ipv6)) {
			return false;
		}
		return true;
	}

	final public String ipv4;
	final public String ipv6;

	/**
	 * Instantiates a new IP
	 * 
	 * @param ipv4
	 *            the IP4 version of the IP
	 * @param ipv6
	 *            the IP6 version of the IP
	 */
	public IP(String ipv4, String ipv6) {
		this.ipv4 = ipv4;
		this.ipv6 = ipv6;
	}

	/**
	 * Instantiates a new IP
	 * 
	 * @param ipv4
	 *            the IP4 version of the IP
	 */
	public IP(String ipv4) {
		this.ipv4 = ipv4;
		this.ipv6 = null;
	}

}
