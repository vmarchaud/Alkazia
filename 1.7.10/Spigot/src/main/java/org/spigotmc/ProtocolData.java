package org.spigotmc;

public class ProtocolData {
	public static class ByteShort extends Number {

		private short value;

		public ByteShort(short value) {
			this.value = value;
		}

		@Override
		public int intValue() {
			return value;
		}

		@Override
		public long longValue() {
			return value;
		}

		@Override
		public float floatValue() {
			return value;
		}

		@Override
		public double doubleValue() {
			return value;
		}
	}

	public static class DualByte extends Number {

		public byte value;
		public byte value2;

		public DualByte(byte value, byte value2) {
			this.value = value;
			this.value2 = value2;
		}

		@Override
		public int intValue() {
			return value;
		}

		@Override
		public long longValue() {
			return value;
		}

		@Override
		public float floatValue() {
			return value;
		}

		@Override
		public double doubleValue() {
			return value;
		}
	}

	public static class HiddenByte extends Number {

		private byte value;

		public HiddenByte(byte value) {
			this.value = value;
		}

		@Override
		public int intValue() {
			return value;
		}

		@Override
		public long longValue() {
			return value;
		}

		@Override
		public float floatValue() {
			return value;
		}

		@Override
		public double doubleValue() {
			return value;
		}
	}

	public static class IntByte extends Number {

		public int value;
		public byte value2;

		public IntByte(int value, byte value2) {
			this.value = value;
			this.value2 = value2;
		}

		@Override
		public byte byteValue() {
			return value2;
		}

		@Override
		public int intValue() {
			return value;
		}

		@Override
		public long longValue() {
			return value;
		}

		@Override
		public float floatValue() {
			return value;
		}

		@Override
		public double doubleValue() {
			return value;
		}
	}

	public static class DualInt extends Number {

		public int value;
		public int value2;

		public DualInt(int value, int value2) {
			this.value = value;
			this.value2 = value2;
		}

		@Override
		public int intValue() {
			return value;
		}

		@Override
		public long longValue() {
			return value;
		}

		@Override
		public float floatValue() {
			return value;
		}

		@Override
		public double doubleValue() {
			return value;
		}
	}
}
