package neo.geo;

import static java.lang.Math.abs;
import static java.lang.Math.toRadians;
import static java.lang.Math.PI;

/**
 * An abstraction class representing angular measurement ordinate in degrees.
 *
 * @author dekassegui@gmail.com
*/
public abstract class Ordinate
{
  /** Standard constructor just storing the measurement value. */
  public Ordinate(double x) { value = x; }

  /** Constructor translating data from DMS format to floating point. */
  public Ordinate(int d, int m, double s) { value = rational(d, m, s); }

  /** Returns the floating point representation of value. */
  public double getValue() { return value; }

  /** Returns the ordinate value in radians. */
  public double inRadians() { return toRadians(value); }

  /** Returns a friendly string representation. */
  public abstract String asString();

  /**
   * Returns an floating point value built with given angle in DMS format
   * inheriting sign from first parameter signum i.e. from degrees.
  */
  public static double rational(int d, int m, double s)
  {
    return (d < 0 ? -1 : 1) * (abs(d) + (abs(m) + abs(s) / 60) / 60);
  }

  /**
   * Returns the value as a bearing angle where zero points to north.
   * <br>Note: The bearing to a point is the angle measured in degrees
   * in a clockwise direction from the north line.
  */
  public static double toBearing(double x)
  {
    return (x * 180 / PI + 360) % 360;
  }

  /**
   * Returns a friendly string representation of given angle in DMS format.
  */
  public static String toDMS(double x)
  {
    x = abs(x);
    x += 1 / 3600d;   // trick against internal representation errors
    int d = (int) x;  // degrees
    x = (x - d) * 60;
    int m = (int) x;  // minutes
    x = (x - m) * 60; // seconds
    x = abs(x - 1);   // complete the trick
    String s = String.format("%06.3f", x);
    // search and remove redundant zero right padding
    int j = 5;
    while (j > 1 && s.charAt(j) < '1') j--;
    if (j < 5) s = s.substring(0, j+1);
    return String.format("%02d\u00B0 %02d\u2019 %s\u201D", d, m, s);
  }

  /** "Against zero" tolerance value. */
  protected static final double EPS = 1e-9;

  /** Angular measurement in degrees. */
  protected double value;
}