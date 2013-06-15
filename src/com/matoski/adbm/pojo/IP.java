package com.matoski.adbm.pojo;

public class IP {

	/*
	 * (non-Javadoc)
	 * 
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
	 * 
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

	public String ipv4, ipv6;

	/**
	 * @param ipv4
	 * @param ipv6
	 */
	public IP(String ipv4, String ipv6) {
		this.ipv4 = ipv4;
		this.ipv6 = ipv6;
	}

}
