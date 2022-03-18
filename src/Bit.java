public class Bit implements IBit {

    private int val;

    Bit(){
        this.val = 0;
    }

    Bit(int value){
        this.val = value;
    }

    @Override
    public void set(int value) {
        this.val = value;
    }

    @Override
    public void toggle() {
        val = val == 0 ? 1 : 0;
    }

    @Override
    public void set() {
        val = 1;
    }

    @Override
    public void clear() {
        val = 0;
    }

    @Override
    public int getValue() {
        return val;
    }

    @Override
    public Bit and(Bit other) {
        if(this.val == 1){
            if(other.val == 1){
                return new Bit(1);
            }
        }
        return new Bit(0);
    }

    @Override
    public Bit or(Bit other) {
        if(this.val == 1){
            return new Bit(1);
        }
        if(other.val == 1){
            return new Bit(1);
        }
        return new Bit(0);

    }

    @Override
    public Bit xor(Bit other) {
        if(this.val == 1){
            if(other.val == 1){
                return new Bit(0);
            }
            else{
                return new Bit(1);
            }
        }
        else{
            if(other.val == 1){
                return new Bit(1);
            }
            else{
                return new Bit(0);
            }
        }
    }

    @Override
    public Bit not() {
        return this.val == 1 ? new Bit(0) : new Bit(1);
    }

    public String toString(){
        return ""+val;
    }

    @Override
    public boolean equals(Object other){
        if (other == this) {
            return true;
        }
        if (!(other instanceof Bit)) {
            return false;
        }
        Bit otherBit = (Bit)other;
        return otherBit.getValue() == this.getValue();
    }
    @Override
    public int hashCode(){
        return this.val;
    }
}
