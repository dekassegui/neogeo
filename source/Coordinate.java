package neo.geo;

/**
 * Minimal geographical coordinate representation class.
 *
 * @author dekassegui@gmail.com
*/
public class Coordinate
{
  /** Standard constructor. */
  public Coordinate(String id, Latitude latitude, Longitude longitude, Altitude altitude)
  {
    this.id        = id;
    this.latitude  = latitude;
    this.longitude = longitude;
    this.altitude  = altitude;
  }

  /** Constructor for an anonymous place. */
  public Coordinate(Latitude latitude, Longitude longitude, Altitude altitude)
  {
    this(null, latitude, longitude, altitude);
  }

  /**
   *
  */
  public Coordinate(String id, Latitude latitude, Longitude longitude)
  {
    this(id, latitude, longitude, null);
  }

  /** Constructor for an anonymous place. */
  public Coordinate(Latitude latitude, Longitude longitude)
  {
    this(null, latitude, longitude, null);
  }

  /** Set the id with given string. */
  public void setId(String s) { id = s; }

  /** Returns the identification of the place with this coordinate. */
  public String getId() { return id == null ? "N/D" : id; }

  /** Returns the latitude of coordinate. */
  public Latitude getLatitude() { return latitude; }

  /** Returns the longitude of coordinate. */
  public Longitude getLongitude() { return longitude; }

  /** Returns the altitude of coordinate. */
  public Altitude getAltitude() { return altitude; }

  /**
   * Returns a friendly string representation.
   *
   *@return The pair of ordinate values in DMS format, each one with related
   *        cardinal point suffix, both enclosed in brackets with the id as
   *        suffix.
  */
  public String asString()
  {
    return String.format("%s at [%s  %s] %s", getId(), latitude.asString(), longitude.asString(), (altitude != null ? altitude.asString() : ""));
  }

  /** Optional place identification. */
  private String    id;

  /** North:South axis ordinate. */
  private Latitude  latitude;

  /** East:West axis ordinate. */
  private Longitude longitude;

  /** Altimetric measurement. */
  private Altitude altitude;
}