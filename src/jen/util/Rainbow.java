package jen.util;

import javafx.scene.paint.Color;

public class Rainbow
{
    private final int speed;

    int state = 0;
    int red = 255;
    int green = 0;
    int blue = 0;

    public Rainbow() {
        this(1);
    }

    /**
     * @param speed How many unique colors to skip forward at a time.
     */
    public Rainbow(int speed) {
        this.speed = speed;
    }

    public Color next() {
        if(state == 0) {
            green += speed;
            if(green >= 255) {
                green = 255;
                state = 1;
            }
        }

        if(state == 1) {
            red -= speed;
            if(red <= 0) {
                red = 0;
                state = 2;
            }
        }

        if(state == 2) {
            blue += speed;
            if(blue >= 255) {
                blue = 255;
                state = 3;
            }
        }

        if(state == 3) {
            green -= speed;
            if(green <= 0) {
                green = 0;
                state = 4;
            }
        }

        if(state == 4) {
            red += speed;
            if(red >= 255) {
                red = 255;
                state = 5;
            }
        }

        if(state == 5) {
            blue -= speed;
            if(blue <= 0) {
                blue = 0;
                state = 0;
            }
        }
        return Color.color(red / 255d, green / 255d, blue / 255d);
    }
}