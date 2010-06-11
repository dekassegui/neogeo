package neo.geo;

import static java.lang.Math.abs;

/**
 *
 * @author dekassegui@gmail.com
*/
public class Altitude
{
  /** Standard constructor storing the value and unit name. */
  public Altitude(double x, String unit)
  {
    this.value = x;
    this.unit = unit;
  }

  /** Standard constructor just storing the value. */
  public Altitude(double x)
  {
    this(x, null);
  }

  public double getAltitude() { return value; }

  public String getUNit() { return (unit != null ? unit : "N/D"); }

  /**
   * Returns a friendly string representation of this measurement.
  */
  public String asString()
  {
    return String.format("%s %s", (Double.isNaN(value) ? "NaN" : String.format("%7.3f", value)), getUNit());
  }

  private double value;

  private String unit;
}