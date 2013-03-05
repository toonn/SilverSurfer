package unused;

import java.awt.Polygon;


//Gelijkbenige driehoek met 2 basishoeken A en B,
//een tophoek C en het zwaartepunt Z
//als de driehoek naar boven wijst (alpha = 270 graden) , staat A links tov C en B rechts.
@SuppressWarnings("serial")
public class Triangle extends Polygon {

    private final int standardLength = 22;
    private int length = 22; // lengte van de tophoek tot loodrecht op basisas
    private final double topAngle = 42; // graden van de tophoek
    private double gravityCenterX;
    private double gravityCenterY;
    private double alpha = 0; // Hoeveel je gedraaid bent ten opzichte van de
                              // X-as
    private double scalingfactor;

    Triangle(final double x, final double y, final double alpha,
            final double scalingfactor) {
        super();
        this.scalingfactor = scalingfactor;
        length = (int) (length * this.scalingfactor);
        gravityCenterX = x;
        gravityCenterY = y;
        this.alpha = alpha;

        resetTriangle();
    }

    private double calculateHoekBetweenZCandZA() {
        return 360 - calculateHoekBetweenZCandZB();
    }

    private double calculateHoekBetweenZCandZB() {
        return ExtMath.cosineRuleAngle(getLengthAC(), getLengthZA(),
                getLengthZC());
    }

    private double calculateXCoordinateA() {
        final double hoekTenOpzichteVanXas = ExtMath.addDegree(
                calculateHoekBetweenZCandZA(), getAlpha());
        return (getGravityCenterX() + getLengthZA()
                * Math.cos(Math.toRadians(hoekTenOpzichteVanXas)));
    }

    private double calculateXCoordinateB() {
        final double hoekTenOpzichteVanXas = ExtMath.addDegree(
                calculateHoekBetweenZCandZB(), getAlpha());
        return (getGravityCenterX() + getLengthZA()
                * Math.cos(Math.toRadians(hoekTenOpzichteVanXas)));
    }

    private double calculateXCoordinateC() {
        return (getGravityCenterX() + getLengthZC()
                * Math.cos(Math.toRadians(getAlpha())));
    }

    private double calculateYCoordinateA() {
        final double hoekTenOpzichteVanXas = ExtMath.addDegree(
                calculateHoekBetweenZCandZA(), getAlpha());
        return (getGravityCenterY() + getLengthZA()
                * Math.sin(Math.toRadians(hoekTenOpzichteVanXas)));
    }

    private double calculateYCoordinateB() {
        final double hoekTenOpzichteVanXas = ExtMath.addDegree(
                calculateHoekBetweenZCandZB(), getAlpha());
        return (getGravityCenterY() + getLengthZA()
                * Math.sin(Math.toRadians(hoekTenOpzichteVanXas)));
    }

    private double calculateYCoordinateC() {
        return (getGravityCenterY() + getLengthZC()
                * Math.sin(Math.toRadians(getAlpha())));
    }

    public double getAlpha() {
        return alpha;
    }

    public double getArrowCenterX() {
        return getGravityCenterX() - scalingfactor * 5.5
                * Math.cos(Math.toRadians(getAlpha()));
    }

    public double getArrowCenterY() {
        return getGravityCenterY() - scalingfactor * 5.5
                * Math.sin(Math.toRadians(getAlpha()));
    }

    public double getGravityCenterX() {
        return gravityCenterX;
    }

    public double getGravityCenterY() {
        return gravityCenterY;
    }

    public int getLength() {
        return length;
    }

    /**
     * lengte AC is gelijk aan lengte BC (de gelijke benen van de driehoek)
     * 
     * @return
     */
    private double getLengthAC() {
        return (getLength() / (Math.cos(Math.toRadians(getTopAngle() / 2))));
    }

    /**
     * de lengte van ZA is gelijk aan de lengte van ZB
     * 
     * @return
     */
    private double getLengthZA() {
        return ExtMath.cosinusRegelToCalculateZijde(getLengthZC(),
                getLengthAC(), getTopAngle() / 2);
    }

    private double getLengthZC() {
        return (double) getLength() * 2 / 3;
    }

    public double getTopAngle() {
        return topAngle;
    }

    private void resetTriangle() {
        addPoint((int) calculateXCoordinateA(), (int) calculateYCoordinateA());
        addPoint((int) getArrowCenterX(), (int) getArrowCenterY());
        addPoint((int) calculateXCoordinateB(), (int) calculateYCoordinateB());
        addPoint((int) calculateXCoordinateC(), (int) calculateYCoordinateC());
    }

    public void setAlpha(final double alpha) {
        this.alpha = alpha;

        reset();
        resetTriangle();
    }

    public void setGravityCenterX(final double gravityCenterX) {
        this.gravityCenterX = gravityCenterX;

        reset();
        resetTriangle();

    }

    public void setGravityCenterY(final double gravityCenterY) {
        this.gravityCenterY = gravityCenterY;

        reset();
        resetTriangle();
    }

    public void setScalingfactor(final double scalingfactor) {
        this.scalingfactor = scalingfactor;
        length = (int) (standardLength * scalingfactor);
    }
}
