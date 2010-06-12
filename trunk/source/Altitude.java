/*
  This file is part of NeoGeo.

  NeoGeo is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  NeoGeo is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with NeoGeo.  If not, see <http://www.gnu.org/licenses/>.
*/
package neo.geo;

/**
 * Class representing an altimetric measurement, convenient as a component
 * of geographical coordinates in navigation and geoprocessment.
*/
public class Altitude
{
  /**
   * Standard constructor storing the value and unit name.
   *
   * @param x    The value of this measurement.
   * @param unit The name or acronym of the related unit of this measurement.
  */
  public Altitude(double x, String unit)
  {
    this.value = x;
    this.unit = unit;
  }

  /**
   * Constructor just storing the value.
   *
   * @param x The value of this measurement.
  */
  public Altitude(double x)
  {
    this(x, null);
  }

  /**
   * Getter of the value of this measurement.
   *
   * @return The value of this measurement.
  */
  public double getValue() { return value; }

  /**
   * Getter of the unit of this measurement.
   *
   * @return The related unit as String.
  */
  public String getUnit() { return (unit != null ? unit : "N/D"); }

  /**
   * Returns a friendly string representation of this measurement.
   *
   * @return A friendly String representation of this measurement.
  */
  public String asString()
  {
    return String.format("%s %s", (Double.isNaN(value) ? "NaN" : String.format("%7.3f", value)), getUnit());
  }

  /** The altimetric value. */
  private double value;

  /** The unit of this measurement. */
  private String unit;
}