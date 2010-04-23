package neo.geo;

import static java.lang.Math.abs;

/**
 * Specialized class representing angular measurements along the orthogonal
 * direction to Earth spin axis aka <b>East:West direction</b>.
 * <pre>Note: Longitudes ranges from -180 degrees to 180 degrees
 * with zero degrees at Greenwich meridian.</pre>
 *
 * @author dekassegui@gmail.com
*/
public class Longitude extends Ordinate
{
  /** Standard constructor just storing the longitude value. */
  public Longitude(double x)
  {
    super(x);
    value = value % 180;
  }

  /** Constructor translating data from DMS format to floating point. */
  public Longitude(int d, int m, double s)
  {
    super(d, m, s);
    value = value % 180;
  }

  /**
   * Constructor translating data from DMS format to floating point
   * signed in conformity to given cardinal point suffix i.e.
   * negative if suffix is "W" (west) else positive.
  */
  public Longitude(int d, int m, double s, String suffix)
  {
    super(d, m, s);
    value = value % 180;
    value = (suffix.matches("(?i:W|E)") ? -1 : 1) * abs(value);
  }

  /**
   * Returns a friendly string representation of this ordinate in DMS format
   * plus related cardinal point suffix.
  */
  public String asString()
  {
    return String.format("%s %c", toDMS( value ), (abs(value) > EPS && value < 0 ? 'W' : 'E'));
  }
}