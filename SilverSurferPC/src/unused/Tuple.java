package unused;

public class Tuple<Type1, Type2> {

    private Type1 item1;
    private Type2 item2;

    public Tuple(final Type1 item1, final Type2 item2) {
        setItem1(item1);
        setItem2(item2);
    }

    public Type1 getItem1() {
        return item1;
    }

    public Type2 getItem2() {
        return item2;
    }

    public void setItem1(final Type1 item1) {
        this.item1 = item1;
    }

    public void setItem2(final Type2 item2) {
        this.item2 = item2;
    }

}
