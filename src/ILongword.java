public interface ILongword {
    Bit getBit(int i); // Get bit i
    void setBit(int i, Bit value); // set bit i's intVal
    Longword and(Longword other); // and two Longwords, returning a third
    Longword or(Longword other); // or two Longwords, returning a third
    Longword xor(Longword other);// xor two Longwords, returning a third
    Longword not(); // negate this Longword, creating another
    Longword rightShift(int amount); // rightshift this Longword by amount bits, creating a new Longword
    Longword leftShift(int amount);// leftshift this Longword by amount bits, creating a new Longword
    @Override
    String toString(); // returns a comma separated string of 0's and 1's: "0,0,0,0,0 (etcetera)" for example
    long getUnsigned(); // returns the intVal of this Longword as a long
    int getSigned(); // returns the intVal of this Longword as an int
    void copy(Longword other); // copies the values of the bits from another Longword into this one
    void set(int value); // set the intVal of the bits of this Longword (used for tests)
}
