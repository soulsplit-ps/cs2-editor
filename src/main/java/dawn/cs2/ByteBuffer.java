package dawn.cs2;

public class ByteBuffer {
	
	private int position;
	private byte[] buffer;
	private static char[] unicodeTable = new char[] {'€', '\u0000', '‚', 'ƒ', '„', '…', '†', '‡', 'ˆ', '‰', 'Š', '‹', 'Œ', '\u0000', 'Ž', '\u0000', '\u0000', '‘', '’', '“', '”', '•', '–', '—', '˜', '™', 'š', '›', 'œ', '\u0000', 'ž', 'Ÿ'};
	private static final int xteaDelta = -1640531527;
	private static final int xteaRounds = 32;
	
	public ByteBuffer() {
		this(new byte[5000], 0);
	}
	
	public ByteBuffer(byte[] data) {
		this(data, 0);
	}
	
	public ByteBuffer(int capacity) {
		this(new byte[capacity], 0);
	}
	
	public ByteBuffer(byte[] data, int offset) {
		this.buffer = data;
		this.position = offset;
	}
	
	public void setPosition(int position) {
		if (position >= 0 && position < this.buffer.length) {
			this.position = position;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public int getPosition() {
		return this.position;
	}
	
	public byte[] toArray(int offset, int length) {
		byte[] bf = new byte[length - offset];
		
		for (int i = 0; i < length; ++i) {
			bf[i] = this.buffer[offset + i];
		}
		
		return bf;
	}
	
	public byte[] getBuffer() {
		return this.buffer;
	}
	
	public final byte readSignedByte() {
		return this.buffer[this.position++];
	}
	
	public final int readUByte() {
		return this.buffer[this.position++] & 255;
	}
	
	public final void writeByte(int v) {
		this.buffer[this.position++] = (byte) v;
	}
	
	public final void readBytes(byte[] buffer, int offset, int length) {
		for (int pos = offset; pos < length + offset; ++pos) {
			this.buffer[pos] = this.buffer[this.position++];
		}
		
	}
	
	public final void writeBytes(byte[] buffer, int offset, int length) {
		for (int pos = offset; pos < offset + length; ++pos) {
			this.buffer[this.position++] = buffer[pos];
		}
		
	}
	
	public final int readUnsignedSmart() {
		int v = this.buffer[this.position] & 255;
		return v >= 128 ? this.readUnsignedShort() - '耀' : this.readUByte();
	}
	
	public final int readSignedSmart() {
		int v = this.buffer[this.position] & 255;
		return v < 128 ? this.readUByte() - 64 : this.readUnsignedShort() - '쀀';
	}
	
	public final void writeSmart(int v) {
		if (v >= 0 && v < 128) {
			this.writeByte(v);
		} else {
			if (v < 0 || v >= 32768) {
				throw new IllegalArgumentException();
			}
			
			this.writeShort(v + '耀');
		}
		
	}
	
	public final int readSignedShort() {
		this.position += 2;
		int i = (this.buffer[this.position - 1] & 255) + (this.buffer[this.position - 2] << 8 & '\uff00');
		if (i > 32767) {
			i -= 65536;
		}
		
		return i;
	}
	
	public final int readUnsignedShort() {
		this.position += 2;
		return (this.buffer[this.position - 2] << 8 & '\uff00') + (this.buffer[this.position - 1] & 255);
	}
	
	public final void writeShort(int s) {
		this.buffer[this.position++] = (byte) (s >> 8);
		this.buffer[this.position++] = (byte) s;
	}
	
	public final int readUnsignedMedInt() {
		this.position += 3;
		return (this.buffer[this.position - 2] << 8 & '\uff00') + ((this.buffer[this.position - 3] & 255) << 16) + (this.buffer[this.position - 1] & 255);
	}
	
	public final int readSignedMedInt() {
		this.position += 3;
		int v = (this.buffer[this.position - 1] & 255) + (this.buffer[this.position - 2] << 8 & '\uff00') + (this.buffer[this.position - 3] << 16 & 16711680);
		if (v > 8388607) {
			v -= 16777216;
		}
		
		return v;
	}
	
	public final void writeMedInt(int v) {
		this.buffer[this.position++] = (byte) (v >> 16);
		this.buffer[this.position++] = (byte) (v >> 8);
		this.buffer[this.position++] = (byte) v;
	}
	
	public final int readInt() {
		this.position += 4;
		return (this.buffer[this.position - 1] & 255) + ((this.buffer[this.position - 3] & 255) << 16) + (this.buffer[this.position - 4] << 24 & -16777216) + (this.buffer[this.position - 2] << 8 & '\uff00');
	}
	
	public final void writeInt(int value) {
		this.buffer[this.position++] = (byte) (value >> 24);
		this.buffer[this.position++] = (byte) (value >> 16);
		this.buffer[this.position++] = (byte) (value >> 8);
		this.buffer[this.position++] = (byte) value;
	}
	
	public final long read5Byte() {
		long v0 = (long) this.readUByte() & 4294967295L;
		long v1 = (long) this.readInt() & 4294967295L;
		return v1 + (v0 << 32);
	}
	
	public final long readLong() {
		long v0 = (long) this.readInt() & 4294967295L;
		long v1 = (long) this.readInt() & 4294967295L;
		return (v0 << 32) + v1;
	}
	
	public final void writeLong(long v) {
		this.buffer[this.position++] = (byte) ((int) (v >> 56));
		this.buffer[this.position++] = (byte) ((int) (v >> 48));
		this.buffer[this.position++] = (byte) ((int) (v >> 40));
		this.buffer[this.position++] = (byte) ((int) (v >> 32));
		this.buffer[this.position++] = (byte) ((int) (v >> 24));
		this.buffer[this.position++] = (byte) ((int) (v >> 16));
		this.buffer[this.position++] = (byte) ((int) (v >> 8));
		this.buffer[this.position++] = (byte) ((int) v);
	}
	
	public final String readNullString() {
		if (this.buffer[this.position] == 0) {
			++this.position;
			return null;
		} else {
			return this.readString();
		}
	}
	
	public final String readVersionedString() {
		return this.readVersionedString((byte) 0);
	}
	
	public final String readVersionedString(byte versionNumber) {
		byte vNumber = this.buffer[this.position++];
		if (vNumber != versionNumber) {
			throw new IllegalStateException("Bad string version number!");
		} else {
			int pos = this.position;
			
			while (this.buffer[this.position++] != 0) {
			}
			
			int strLen = this.position - pos - 1;
			return strLen == 0 ? "" : decodeString(this.buffer, pos, strLen);
		}
	}
	
	public final void writeVersionedString(String str) {
		this.writeVersionedString(str, (byte) 0);
	}
	
	public final void writeVersionedString(String str, byte version) {
		int nullIdx = str.indexOf(0);
		if (nullIdx >= 0) {
			throw new IllegalArgumentException("NUL character at " + nullIdx + "!");
		} else {
			this.buffer[this.position++] = version;
			this.position += encodeString(this.buffer, this.position, str, 0, str.length());
			this.buffer[this.position++] = 0;
		}
	}
	
	public final String readString() {
		int pos = this.position;
		
		while (this.buffer[this.position++] != 0) {
		}
		
		int strlen = this.position - pos - 1;
		return strlen == 0 ? "" : decodeString(this.buffer, pos, strlen);
	}
	
	public final void writeString(String string) {
		int n = string.indexOf(0);
		if (n >= 0) {
			throw new IllegalArgumentException("NUL character at " + n + "!");
		} else {
			this.position += encodeString(this.buffer, this.position, string, 0, string.length());
			this.buffer[this.position++] = 0;
		}
	}
	
	public final int readSum() {
		int sum = 0;
		
		int incr;
		for (incr = this.readUnsignedSmart(); incr == 32767; sum += 32767) {
			incr = this.readUnsignedSmart();
		}
		
		sum += incr;
		return sum;
	}
	
	public final int readVarSeized() {
		int f = this.buffer[this.position++];
		
		int sum;
		for (sum = 0; f < 0; f = this.buffer[this.position++]) {
			sum = (sum | f & 127) << 7;
		}
		
		return sum | f;
	}
	
	public final void writeVarSeized(int val) {
		if ((val & -128) != 0) {
			if ((val & -16384) != 0) {
				if ((val & -2097152) != 0) {
					if ((val & -268435456) != 0) {
						this.writeByte(val >>> 28 | 128);
					}
					
					this.writeByte((val | 269102108) >>> 21);
				}
				
				this.writeByte(val >>> 14 | 128);
			}
			
			this.writeByte((val | 16417) >>> 7);
		}
		
		this.writeByte(val & 127);
	}
	
	public final long readDynamic(int numBytes) throws IllegalArgumentException {
		--numBytes;
		if (numBytes >= 0 && numBytes <= 7) {
			long value = 0L;
			
			for (int bitsLeft = numBytes * 8; bitsLeft >= 0; bitsLeft -= 8) {
				value |= ((long) this.buffer[this.position++] & 255L) << bitsLeft;
			}
			
			return value;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public final void writeDynamic(int numBytes, long value) {
		--numBytes;
		if (numBytes >= 0 && numBytes <= 7) {
			for (int bitsLeft = numBytes * 8; bitsLeft >= 0; bitsLeft -= 8) {
				this.buffer[this.position++] = (byte) ((int) (value >> bitsLeft));
			}
			
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public final void encryptXTEA(int[] keys, int offset, int length) {
		int originalPosition = this.position;
		this.position = offset;
		int numCycles = (length - offset) / 8;
		
		for (int cycle = 0; cycle < numCycles; ++cycle) {
			int v0 = this.readInt();
			int v1 = this.readInt();
			int sum = 0;
			
			for (int var10 = 32; var10-- > 0; v1 += sum + keys[(sum & 7471) >>> 11] ^ v0 + (v0 << 4 ^ v0 >>> 5)) {
				v0 += sum + keys[sum & 3] ^ (v1 << 4 ^ v1 >>> 5) + v1;
				sum += -1640531527;
			}
			
			this.position -= 8;
			this.writeInt(v0);
			this.writeInt(v1);
		}
		
		this.position = originalPosition;
	}
	
	public final void xteaDecrypt(int[] keys, int offset, int length) {
		int originalPosition = this.position;
		this.position = offset;
		int numCycles = (length - offset) / 8;
		
		for (int cycle = 0; cycle < numCycles; ++cycle) {
			int v0 = this.readInt();
			int v1 = this.readInt();
			int numRounds = 32;
			
			for (int sum = -1640531527 * numRounds; numRounds-- > 0; v0 -= keys[sum & 3] + sum ^ v1 + (v1 >>> 5 ^ v1 << 4)) {
				v1 -= sum + keys[(sum & 6510) >>> 11] ^ (v0 << 4 ^ v0 >>> 5) + v0;
				sum -= -1640531527;
			}
			
			this.position -= 8;
			this.writeInt(v0);
			this.writeInt(v1);
		}
		
		this.position = originalPosition;
	}
	
	static final String decodeString(byte[] buffer, int offset, int strLen) {
		char[] strBuffer = new char[strLen];
		int write = 0;
		
		for (int dc = 0; dc < strLen; ++dc) {
			int data = buffer[dc + offset] & 255;
			if (data != 0) {
				if (data >= 128 && data < 160) {
					char uni = unicodeTable[data - 128];
					if (uni == 0) {
						uni = '?';
					}
					
					strBuffer[write++] = uni;
				} else {
					strBuffer[write++] = (char) data;
				}
			}
		}
		
		return new String(strBuffer, 0, write);
	}
	
	public static final int encodeString(byte[] buffer, int bufferOffset, String str, int strOffset, int strLen) {
		int charsToEncode = strLen - strOffset;
		
		for (int cc = 0; cc < charsToEncode; ++cc) {
			char c = str.charAt(cc + strOffset);
			if ((c <= 0 || c >= 128) && (c < 160 || c > 255)) {
				switch (c) {
					case 'Œ':
						buffer[bufferOffset + cc] = -116;
						break;
					case 'œ':
						buffer[bufferOffset + cc] = -100;
						break;
					case 'Š':
						buffer[bufferOffset + cc] = -118;
						break;
					case 'š':
						buffer[bufferOffset + cc] = -102;
						break;
					case 'Ÿ':
						buffer[bufferOffset + cc] = -97;
						break;
					case 'Ž':
						buffer[bufferOffset + cc] = -114;
						break;
					case 'ž':
						buffer[bufferOffset + cc] = -98;
						break;
					case 'ƒ':
						buffer[bufferOffset + cc] = -125;
						break;
					case 'ˆ':
						buffer[bufferOffset + cc] = -120;
						break;
					case '˜':
						buffer[bufferOffset + cc] = -104;
						break;
					case '–':
						buffer[bufferOffset + cc] = -106;
						break;
					case '—':
						buffer[bufferOffset + cc] = -105;
						break;
					case '‘':
						buffer[bufferOffset + cc] = -111;
						break;
					case '’':
						buffer[bufferOffset + cc] = -110;
						break;
					case '‚':
						buffer[bufferOffset + cc] = -126;
						break;
					case '“':
						buffer[bufferOffset + cc] = -109;
						break;
					case '”':
						buffer[bufferOffset + cc] = -108;
						break;
					case '„':
						buffer[bufferOffset + cc] = -124;
						break;
					case '†':
						buffer[bufferOffset + cc] = -122;
						break;
					case '‡':
						buffer[bufferOffset + cc] = -121;
						break;
					case '•':
						buffer[bufferOffset + cc] = -107;
						break;
					case '…':
						buffer[bufferOffset + cc] = -123;
						break;
					case '‰':
						buffer[bufferOffset + cc] = -119;
						break;
					case '‹':
						buffer[bufferOffset + cc] = -117;
						break;
					case '›':
						buffer[bufferOffset + cc] = -101;
						break;
					case '€':
						buffer[bufferOffset + cc] = -128;
						break;
					case '™':
						buffer[bufferOffset + cc] = -103;
						break;
					default:
						buffer[bufferOffset + cc] = 63;
				}
			} else {
				buffer[bufferOffset + cc] = (byte) c;
			}
		}
		
		return charsToEncode;
	}
}