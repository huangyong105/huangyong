package edu.dbke.socket.cp.util;

import java.lang.reflect.Array;

/**
 * 对象hashcode生成工具.
 * 
 * @author figo
 * 
 */
public class HashCodeBuilder {
	private final int iConstant;
	private int iTotal;

	public HashCodeBuilder() {
		this.iConstant = 37;
		this.iTotal = 17;
	}

	public HashCodeBuilder(int iConstant, int iTotal) {
		this.iConstant = iConstant;
		this.iTotal = iTotal;
	}

	public HashCodeBuilder append(boolean field) {
		iTotal = iConstant * iTotal + (field ? 1 : 0);
		return this;
	}

	public HashCodeBuilder append(byte field) {
		iTotal = iConstant * iTotal + field;
		return this;
	}

	public HashCodeBuilder append(char field) {
		iTotal = iConstant * iTotal + field;
		return this;
	}

	public HashCodeBuilder append(short field) {
		iTotal = iConstant * iTotal + field;
		return this;
	}

	public HashCodeBuilder append(int field) {
		iTotal = iConstant * iTotal + field;
		return this;
	}

	public HashCodeBuilder append(long field) {
		iTotal = iConstant * iTotal + (int) (field ^ (field >>> 32));
		return this;
	}

	public HashCodeBuilder append(float field) {
		iTotal = iConstant * iTotal + Float.floatToIntBits(field);
		return this;
	}

	public HashCodeBuilder append(double field) {
		append(Double.doubleToLongBits(field));
		return this;
	}

	public HashCodeBuilder append(Object field) {
		if (field == null) {
			iTotal = 0;
		} else if (field.getClass().isArray()) {
			for (int i = Array.getLength(field) - 1; i >= 0; i--) {
				append(Array.get(field, i));
			}
		} else {
			append(field.hashCode());
		}
		return this;
	}

	public int toHashCode() {
		return iTotal;
	}

	@Override
	public int hashCode() {
		return iTotal;
	}

	@Override
	public String toString() {
		return String.valueOf(iTotal);
	}

	public static int getHashCode(Object... fields) {
		HashCodeBuilder hcBuilder = new HashCodeBuilder();
		for (Object obj : fields) {
			hcBuilder.append(obj);
		}
		return hcBuilder.hashCode();
	}
}