package neo.geo;

/**
 * Minimal geographical polar coordinate representation class.
 *
 * @author dekassegui@gmail.com
*/
public class PolarCoordinate
{
  /**
   * Standard constructor.
   *
   * @param id place identification of this coordinate
   * @param distance in kilometers
   * @param angle bearing angle in degrees
  */
  public PolarCoordinate(String id, double distance, double angle)
  {
    this.id       = id;
    this.distance = distance;
    this.angle    = angle % 360;
  }

  /** Constructor of an anonymous place. */
  public PolarCoordinate(double distance, double angle)
  {
    this(null, distance, angle);
  }

  /** Set the id with given string. */
  public void setId(String s) { id = s; }

  /** Returns the identification of the place with this coordinate. */
  public String getId() { return id == null ? "N/D" : id; }

  /** Returns the distance of coordinate. */
  public double getDistance() { return distance; }

  /** Returns the bearing angle of coordinate. */
  public double getAngle() { return angle; }

  /**
   * Returns a friendly string representation.
   *
   * @return the coordinate as a pair of values: distance plus unit and bearing
   *         angle in DMS format, both enclosed in brackets prefixed with id.
  */
  public String asString()
  {
    return String.format("%s at [%,.3f km  %s]", getId(), distance, Ordinate.toDMS(angle));
  }

  /** Optional place identification. */
  private String id;
  /** Distance in kilometers. */
  private double distance;
  /** Bearing angle in degrees. */
  private double angle;
}