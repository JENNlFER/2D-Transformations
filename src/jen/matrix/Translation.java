package jen.matrix;

public class Translation extends Transformation {

    public Translation() {
        super();
    }

    public Translation(double Tx, double Ty) {
        super();
        setTx(Tx);
        setTy(Ty);
    }

    @Override
    public double setTx(double Tx) {
        return this.Tx = set(6, Tx);
    }

    @Override
    public double setTy(double Ty) {
        return this.Ty = set(7, Ty);
    }

    @Override
    public void setComplex(boolean b) {
        return;
    }

}
