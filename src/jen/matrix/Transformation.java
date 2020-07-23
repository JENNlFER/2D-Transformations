package jen.matrix;

import lombok.Getter;

public abstract class Transformation extends Matrix {

    @Getter double Tx = 0;
    @Getter double Ty = 0;
    @Getter double Cx = 0;
    @Getter double Cy = 0;

    Transformation() {
        super();
        if (Cx != 0 || Cy != 0) complex = true;
    }

    public abstract double setTx(double Tx);
    public abstract double setTy(double Ty);

    public double setCx(double Cx) {
        return this.Cx = Cx;
    }

    public double setCy(double Cy) {
        return this.Cy = Cy;
    }

    public void setComplex(boolean b) {
        complex = b;
    }
}
