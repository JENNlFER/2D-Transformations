package jen.matrix;

public class Scale extends Transformation {

    public Scale() {
        super();
        setTx(1);
        setTy(1);
    }

    public Scale(double Tx, double Ty, double Cx, double Cy) {
        super();
        setTx(Tx);
        setTy(Ty);
        setCx(Cx);
        setCy(Cy);
    }

    @Override
    public double setTx(double Tx) {
        return this.Tx = set(0, Tx);
    }

    @Override
    public double setTy(double Ty) {
        return this.Ty = set(4, Ty);
    }
}
