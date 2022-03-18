import java.util.Arrays;

public class ALU {
    private static final Longword LOWEST_FIVE_BITMASK = new Longword(31);
    private static final Bit _1_BIT = new Bit(1);
    private static final Bit _0_BIT = new Bit(0);
    private static final Bit[] AND =            {_1_BIT, _0_BIT, _0_BIT, _0_BIT};
    private static final Bit[] OR =             {_1_BIT, _0_BIT, _0_BIT, _1_BIT};
    private static final Bit[] XOR =            {_1_BIT, _0_BIT, _1_BIT, _0_BIT};
    private static final Bit[] NOT =            {_1_BIT, _0_BIT, _1_BIT, _1_BIT};
    private static final Bit[] LEFT_SHIFT =     {_1_BIT, _1_BIT, _0_BIT, _0_BIT};
    private static final Bit[] RIGHT_SHIFT =    {_1_BIT, _1_BIT, _0_BIT, _1_BIT};
    private static final Bit[] ADD =            {_1_BIT, _1_BIT, _1_BIT, _0_BIT};
    private static final Bit[] SUBTRACT =       {_1_BIT, _1_BIT, _1_BIT, _1_BIT};
    private static final Bit[] MULTIPLY =       {_0_BIT, _1_BIT, _1_BIT, _1_BIT};

    public static Longword doOp(Bit[] operation, Longword a, Longword b){

        if(Arrays.deepEquals(operation,AND)){
            return a.and(b);
        }
        else if(Arrays.deepEquals(operation,OR)){
            return a.or(b);
        }
        else if(Arrays.deepEquals(operation,XOR)){
            return a.xor(b);
        }
        else if(Arrays.deepEquals(operation,NOT)){
            return a.not();
        }
        else if(Arrays.deepEquals(operation,LEFT_SHIFT)){
            //The last five bits of LOWEST_FIVE_BITMASK are set. So And-ing the shift-amount word (Longword b) with this mask allows us to look at only the last 5 bits.
            Longword leftShiftAmount = b.and(LOWEST_FIVE_BITMASK);
            return a.leftShift(leftShiftAmount.getSigned());
        }
        else if(Arrays.deepEquals(operation,RIGHT_SHIFT)){
            Longword rightShiftAmount = b.and(LOWEST_FIVE_BITMASK);
            return a.rightShift(rightShiftAmount.getSigned());
        }
        else if(Arrays.deepEquals(operation,ADD)){
            return RippleAdder.add(a,b);
        }
        else if(Arrays.deepEquals(operation,SUBTRACT)){
            return RippleAdder.subtract(a,b);
        }
        else if(Arrays.deepEquals(operation,MULTIPLY)){
            return Multiplier.multiply(a,b);
        }
        else {
            return a; //If an invalid OP code is passed in, return the first longword.
        }

    }
}
