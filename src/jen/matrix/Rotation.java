package jen.matrix;

public class Rotation extends Transformation {

    public Rotation() {
        super();
    }

    public Rotation(double Tx, double Cx, double Cy) {
        super();
        setTx(Tx);
        setCx(Cx);
        setCy(Cy);
    }

    @Override
    public double setTx(double Tx) {
        set(0, Math.cos(Tx));
        set(1, -Math.sin(Tx));
        set(3, Math.sin(Tx));
        set(4, Math.cos(Tx));
        return this.Tx = Tx;
    }

    @Override
    public double setTy(double Ty) {
        return 0;
    }
}
