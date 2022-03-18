public class RippleAdder {
    public static Longword add(Longword a, Longword b){
        Longword sum = new Longword();
        Bit CarryBit = new Bit(0); //Incoming Carry Bit
        for(int i = 31; i >= 0; i--){
            /* We are going to construct a full-adder by combining two half-adders. */

            /* XOR the two 'original' bits, next AND them to see if they produce a carry bit */
            Bit firstPartialSum = a.getBit(i).xor(b.getBit(i));
            Bit firstCarryOut = a.getBit(i).and(b.getBit(i));

            /* Now XOR the the first partial sum with the incoming Carry bit. Then AND the sum with incoming carry bit to see if they produce a new carry bit*/
            Bit secondPartialSum = firstPartialSum.xor(CarryBit);
            Bit secondCarryOut = firstPartialSum.and(CarryBit);

            /* If the result of our partial sum is 1, set the bit at this position in the result */
            if(secondPartialSum.getValue() == 1){
                sum.getBit(i).set();
            }
            /* If either of our partial additions produced a carry bit, we need to carry it over to the next bit */
            CarryBit = firstCarryOut.or(secondCarryOut);
        }
        return sum;
    }
    public static Longword subtract(Longword a, Longword b){
        /* Subtraction is just adding by the inverse of the subtrahend. So take the inverse of b then add a to b. */
        Longword subtrahend = b.not();
        subtrahend = add(subtrahend,new Longword(1));
        return add(a,subtrahend);
    }

}
