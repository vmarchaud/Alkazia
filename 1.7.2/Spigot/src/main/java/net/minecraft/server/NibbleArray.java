package net.minecraft.server;

import java.util.Arrays; // Spigot

public class NibbleArray {

    private byte[] a; // Spigot - remove final, make private (anyone directly accessing this is broken already)
    private final int b;
    private final int c;
    // Spigot start
    private byte trivialValue;
    private byte trivialByte;
    private int length;
    private static final int LEN2K = 2048; // Universal length used right now - optimize around this
    private static final byte[][] TrivLen2k;

    static {
        TrivLen2k = new byte[16][];
        for (int i = 0; i < 16; i++) {
            TrivLen2k[i] = new byte[LEN2K];
            Arrays.fill(TrivLen2k[i], (byte) (i | (i << 4)));
        }
    }

    // Try to convert array to trivial array
    public void detectAndProcessTrivialArray() {
        trivialValue = (byte) (a[0] & 0xF);
        trivialByte = (byte) (trivialValue | (trivialValue << 4));
        for (int i = 0; i < a.length; i++) {
            if (a[i] != trivialByte) return;
        }
        // All values matches, so array is trivial
        this.length = a.length;
        this.a = null;
    }

    // Force array to non-trivial state
    public void forceToNonTrivialArray() {
        if (this.a == null) {
            this.a = new byte[this.length];
            if (this.trivialByte != 0) {
                Arrays.fill(this.a, this.trivialByte);
            }
        }
    }

    // Test if array is in trivial state
    public boolean isTrivialArray() {
        return (this.a == null);
    }

    // Get value of all elements (only valid if array is in trivial state)
    public int getTrivialArrayValue() {
        return this.trivialValue;
    }

    // Get logical length of byte array for nibble data (whether trivial or non-trivial)
    public int getByteLength() {
        if (this.a == null) {
            return this.length;
        } else {
            return this.a.length;
        }
    }

    // Return byte encoding of array (whether trivial or non-trivial) - returns read-only array if trivial (do not modify!)
    public byte[] getValueArray() {
        if (this.a != null) {
            return this.a;
        } else {
            byte[] rslt;

            if (this.length == LEN2K) {  // All current uses are 2k long, but be safe
                rslt = TrivLen2k[this.trivialValue];
            } else {
                rslt = new byte[this.length];
                if (this.trivialByte != 0) {
                    Arrays.fill(rslt, this.trivialByte);
                }
            }
            return rslt;
        }
    }

    // Copy byte representation of array to given offset in given byte array
    public int copyToByteArray(byte[] dest, int off) {
        if (this.a == null) {
            Arrays.fill(dest, off, off + this.length, this.trivialByte);
            return off + this.length;
        } else {
            System.arraycopy(this.a, 0, dest, off, this.a.length);
            return off + this.a.length;
        }
    }

    // Resize array to given byte length
    public void resizeArray(int len) {
        if (this.a == null) {
            this.length = len;
        } else if (this.a.length != len) {
            byte[] newa = new byte[len];
            System.arraycopy(this.a, 0, newa, 0, ((this.a.length > len) ? len : this.a.length));
            this.a = newa;
        }
    }
    // Spigot end

    public NibbleArray(int i, int j) {
        // Spigot start
        //this.a = new byte[i >> 1];
        this.a = null; // Start off as trivial value (all same zero value)
        this.length = i >> 1;
        this.trivialByte = this.trivialValue = 0;
        // Spigot end
        this.b = j;
        this.c = j + 4;
    }

    public NibbleArray(byte[] abyte, int i) {
        this.a = abyte;
        this.b = i;
        this.c = i + 4;
        detectAndProcessTrivialArray(); // Spigot
    }

    public int a(int i, int j, int k) {
        if (this.a == null) return this.trivialValue; // Spigot
        int l = j << this.c | k << this.b | i;
        int i1 = l >> 1;
        int j1 = l & 1;

        return j1 == 0 ? this.a[i1] & 15 : this.a[i1] >> 4 & 15;
    }

    public void a(int i, int j, int k, int l) {
        // Spigot start
        if (this.a == null) {
            if (l != this.trivialValue) { // Not same as trivial value, array no longer trivial
                this.a = new byte[this.length];
                if (this.trivialByte != 0) {
                    Arrays.fill(this.a, this.trivialByte);
                }
            } else {
                return;
            }
        }
        // Spigot end
        int i1 = j << this.c | k << this.b | i;
        int j1 = i1 >> 1;
        int k1 = i1 & 1;

        if (k1 == 0) {
            this.a[j1] = (byte) (this.a[j1] & 240 | l & 15);
        } else {
            this.a[j1] = (byte) (this.a[j1] & 15 | (l & 15) << 4);
        }
    }
}
