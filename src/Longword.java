import java.util.IllformedLocaleException;

public class Longword implements ILongword {
    static final int WORD_SIZE = 32;
    private Bit[] word = new Bit[WORD_SIZE];

    Longword(){
        this.set(0);

    }
    Longword(int value){
        this.set(value);
    }

    Longword(Bit[] bits){
        if(bits.length != WORD_SIZE) throw new IllegalArgumentException("Trying to instantiate Longword with incorrect number of bits!");
        this.word = bits;
    }

    Longword(String bits){
        if(bits.length() > WORD_SIZE) throw new IllegalArgumentException("Trying to instantiate Longword with more than 32 bits!");
        //Parse the binary string as an integer and use that intVal to construct the word.
        this.set(Integer.parseInt(bits,2));
    }

    @Override
    public Bit getBit(int i) {
        if(i >= WORD_SIZE){
            System.out.println("Word is only of size:" + WORD_SIZE + "!");
            return null;
        }
        return word[i];
    }

    @Override
    public void setBit(int i, Bit value) {
        if(i >= WORD_SIZE){
            System.out.println("Word is only of size:" + WORD_SIZE + "!");
            return;
        }
        word[i] = value;
    }

    @Override
    public Longword and(Longword other) {
        Longword result = new Longword();
        for(int i = 0; i < WORD_SIZE; i++){
            result.setBit(i,other.getBit(i).and(this.getBit(i)));
        }
        return result;
    }

    @Override
    public Longword or(Longword other) {
        Longword result = new Longword();
        for(int i = 0; i < WORD_SIZE; i++){
            result.setBit(i,other.getBit(i).or(this.getBit(i)));
        }
        return result;
    }

    @Override
    public Longword xor(Longword other) {
        Longword result = new Longword();
        for(int i = 0; i < WORD_SIZE; i++){
            result.setBit(i,other.getBit(i).xor(this.getBit(i)));
        }
        return result;
    }

    @Override
    public Longword not() {
        Longword result = new Longword();
        for(int i = 0; i < WORD_SIZE; i++){
            result.setBit(i,this.getBit(i).not());
        }
        return result;
    }

    @Override
    public Longword rightShift(int amount) {
        if(amount > 32){
            System.out.println("Can't Right-Shift by more than the size of the word!\n");
            return this;
        }
        Longword result = new Longword();
        Bit sigBit = this.word[0]; // Keep track of most significant bit for padding after arithmetic right-shift.
        for(int i = 0; i < amount; i++){
            result.setBit(i,sigBit);
        }
        for(int i = amount; i < WORD_SIZE; i++){
            result.setBit(i,this.getBit(i - amount));
        }
        return result;
    }

    @Override
    public Longword leftShift(int amount) {
        if(amount > WORD_SIZE){
            System.out.println("Can't Left-Shift by more than the size of the word!\n");
            return this;
        }
        Longword result = new Longword();
        for(int i = 0; i < WORD_SIZE - amount; i++){
            result.setBit(i, this.getBit(i + amount));
        }
        return result;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < WORD_SIZE - 1; i++){
            sb.append(word[i].toString());
            sb.append(",");
        }
        sb.append(word[WORD_SIZE - 1].toString());
        return sb.toString();
    }


    @Override
    public long getUnsigned() {
        long sum = 0;
        long exponent = 1; // represents the exponent of the current bit. Initial intVal is 1 because the rightmost bit represents 2^0.
        for(int i = WORD_SIZE - 1; i >= 0; i--){
            sum += exponent * this.getBit(i).getValue();
            exponent *= 2; // exponent increased by a factor of 2 each bit.
        }
        return sum;
    }

    @Override
    public int getSigned() {
        int sum = 0;
        boolean signed = this.getBit(0).getValue() == 1; //if the first bit is 1 the number is negative, if not its positive.
        Longword word = this;
        if(signed){
            word = this.not();
        }
        int exponent = 1; // represents the exponent of the current bit. Initial intVal is 1 because the rightmost bit represents 2^0.
        for(int i = WORD_SIZE - 1; i >= 0; i--){
            sum += exponent * word.getBit(i).getValue();
            exponent *= 2; // exponent increased by a factor of 2 each bit.
        }
        return signed ? (sum + 1) * -1: sum;

    }

    @Override
    public void copy(Longword other) {
        for(int i = 0; i < WORD_SIZE; i++){
            this.setBit(i,other.getBit(i));
        }
    }

    @Override
    public void set(int value) {
        String bitRepresentation = Integer.toBinaryString(value);
        int index = WORD_SIZE - 1;
        for(int i = bitRepresentation.length() - 1; i >= 0; i--){
            this.setBit(index,new Bit(Character.getNumericValue(bitRepresentation.charAt(i))));
            index--;
        }
        for(int i = index; i >= 0; i--){
            this.setBit(i,new Bit(0));
        }
    }

    @Override
    public boolean equals(Object other){
        if (other == this) {
            return true;
        }
        if (!(other instanceof Longword)) {
            return false;
        }
        Longword otherWord = (Longword)other;
        for(int i = 0; i < WORD_SIZE; i++){
            if(this.getBit(i).getValue() != otherWord.getBit(i).getValue()){
                return false;
            }
        }
        return true;
    }
    @Override
    public int hashCode(){
        return this.getSigned();
    }
}
