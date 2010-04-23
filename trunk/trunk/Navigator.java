package neo.geo;

import static java.lang.Math.*;
import static neo.geo.Ordinate.*;

/**
 * Helper set of methods to support navigation calculus strongly considering
 * <i>Earth radius variance along latitudes</i>, i.e. decreasing from its
 * maximum value at Equatorial Line to its minimum value at poles.
 * <pre>The radius of curvature varies with direction and latitude; according
 * to Snyder ("Map Projections - A Working Manual", by John P. Snyder, U.S.
 * Geological Survey Professional Paper 1395, United States Government
 * Printing Office, Washington DC, 1987, p24).</pre>
 * Earth shape is <i>Oblate Spheroid</i> but to ease calculations with a minor
 * error it is suitable to use <i>Ellipsoid of Revolution</i> as a shape.
 * <pre>Important: The above is optional and can be turned off just setting
 * the variable {@link #calculateRadius} to <b>false</b>.</pre>
 * @author dekassegui@gmail.com
*/
public class Navigator
{
  /** Earth Polar radius estimate in kilometers. */
  protected static final double EARTH_POLAR_RADIUS = 6356.78;

  /** Earth Equatorial radius estimate in kilometers. */
  protected static final double EARTH_EQUATORIAL_RADIUS = 6378.14;

  /**
   * Earth quadratic mean radius between {@link #EARTH_POLAR_RADIUS} and
   * {@link #EARTH_EQUATORIAL_RADIUS} in kilometers.
  */
  protected static final double EARTH_MEAN_RADIUS = sqrt((EARTH_POLAR_RADIUS * EARTH_POLAR_RADIUS + 3 * EARTH_EQUATORIAL_RADIUS * EARTH_EQUATORIAL_RADIUS) / 4);

  /** Square of Earth's eccentricity. */
  protected static final double ECC2 = 1 - EARTH_POLAR_RADIUS * EARTH_POLAR_RADIUS / EARTH_EQUATORIAL_RADIUS / EARTH_EQUATORIAL_RADIUS;

  /**
   * API parameter to allow Earth radius calculation for a given latitude
   * unless there is a single coordinate as happens in <i>Destination
   * Point Calculations</i>.
   * <pre>The default value is <b>true</b> not persistent along sessions.</pre>
   * @see <a href="#setCalculateRadius(boolean)">setCalculateRadius</a>
  */
  protected static boolean calculateRadius = true;

  /**
   * Set the new value of {@link #calculateRadius} API parameter.
   * @param value The new value of the API parameter.
   * @see <a href="#getCalculateRadius()">getCalculateRadius</a>
  */
  public static void setCalculateRadius(boolean value)
  {
    calculateRadius = value;
  }

  /**
   * Get the value of {@link #calculateRadius} API parameter.
   * @return The boolean value of API parameter.
   * @see <a href="#setCalculateRadius(boolean)">setCalculateRadius</a>
  */
  public static boolean getCalculateRadius() { return calculateRadius; }

  /**
   * Return the Earth radius for a given latitude of the middle point
   * between two coordinates unless {@link #calculateRadius} parameter
   * was set to <b>false</b>, leading to {@link #EARTH_MEAN_RADIUS} as
   * return value.
   * @param latitude Numeric value in degrees ranging from -90 to 90 degrees.
   * @return The Earth radius in kilometers.
   * @see <a href="#midPoint(neo.geo.Coordinate, neo.geo.Coordinate)">midPoint</a>
  */
  public static double EarthRadius(double latitude)
  {
    if (calculateRadius) {
      double c = cos( toRadians(latitude) );
      return EARTH_POLAR_RADIUS / sqrt(1 - ECC2 * c * c);
    }
    return EARTH_MEAN_RADIUS;
  }

  /**
   * Returns the middle point between source and destination point.
   * <pre>Note: Just as the initial bearing may vary from the final bearing,
   * the midpoint may not be located half-way between latitudes/longitudes.
   * E.g. The midpoint between 35&deg;N, 45&deg;E and 35&deg;N, 135&deg;E
   * is around 45&deg;N, 90&deg;E.</pre>
   * @param origem Coordinate of source point.
   * @param destino Coordinate of destination point.
   * @return The coordinate of middle point between source and destination.
  */
  public static Coordinate midPoint(Coordinate origem, Coordinate destino)
  {
    double dLon = toRadians( destino.getLongitude().getValue() - origem.getLongitude().getValue() );
    double dlr = destino.getLatitude().inRadians();
    double Bx = cos(dlr) * cos(dLon);
    double By = cos(dlr) * sin(dLon);
    double olr = origem.getLatitude().inRadians();
    double lat3 = atan2(sin(olr) + sin(dlr), sqrt((cos(olr) + Bx) * (cos(olr) + Bx) + By * By));
    double lon3 = origem.getLongitude().inRadians() + atan2(By, cos(olr) + Bx);
    lat3 = toDegrees(lat3);
    lon3 = toDegrees(lon3);
    return new Coordinate(new Latitude(lat3), new Longitude(lon3));
  }

  /**
   * Returns the distance in kilometers between given places coordinates via
   * <i>Spherical Law Of Cosines</i> enhanced with correction for Earth radius.
   * @param origem Coordinate of source point.
   * @param destino Coordinate of destination point.
   * @return The distance in kilometers between given places coordinates.
  */
  public static double distance(Coordinate origem, Coordinate destino)
  {
    double radius = EarthRadius( midPoint(origem, destino).getLatitude().getValue() );
    double lat1 = origem.getLatitude().inRadians();
    double lat2 = destino.getLatitude().inRadians();
    return radius * acos( sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos( toRadians( destino.getLongitude().getValue() - origem.getLongitude().getValue() ) ) );
  }

  /**
   * Returns the distance in kilometers between given places coordinates via
   * <i>Spherical Law Of Cosines</i> enhanced with correction for Earth radius.
   * <pre>Note: This is an alternative implementation leading to same result
   * of homonym method.</pre>
   * @param origem coordinate of source point.
   * @param destino coordinate of destination point.
   * @return The distance in kilometers between given places coordinates.
  */
  public static double _distance(Coordinate origem, Coordinate destino)
  {
    double x = (origem.getLatitude().getValue() - destino.getLatitude().getValue()) / 2;
    double slat = sin( toRadians(x) );
    x = (origem.getLongitude().getValue() - destino.getLongitude().getValue()) / 2;
    double slon = sin( toRadians(x) );
    double radius = EarthRadius( midPoint(origem, destino).getLatitude().getValue() );
    return 2 * asin( sqrt(slat * slat + cos(origem.getLatitude().inRadians()) * cos(destino.getLatitude().inRadians()) * slon * slon) ) * radius;
  }

  /**
   * Returns the distance in kilometers between given places coordinates via
   * <i>Haversine Formula</i> enhanced with correction for Earth radius.
   * @param origem Coordinate of source point.
   * @param destino Coordinate of destination point.
   * @return The distance in kilometers between given places coordinates.
  */
  public static double hDistance(Coordinate origem, Coordinate destino)
  {
    double dLat = toRadians( destino.getLatitude().getValue() - origem.getLatitude().getValue() );
    double dLon = toRadians( destino.getLongitude().getValue() - origem.getLongitude().getValue() );
    double sla = sin( dLat / 2 ), slo = sin( dLon / 2 );
    double a = sla * sla + cos( origem.getLatitude().inRadians() ) * cos( destino.getLatitude().inRadians() ) * slo * slo;
    // get the Earth radius from latitude of the middle point coordinate
    double radius = EarthRadius( midPoint(origem, destino).getLatitude().getValue() );
    return 2 * radius * atan2( sqrt(a), sqrt(1 - a) );
  }

  /**
   * Returns the initial bearing from source to destination.
   * @param origem Coordinate of source point.
   * @param destino Coordinate of destination point.
   * @return The initial bearing angle ranging from 0 to 360 degrees.
  */
  public static double initialBearing(Coordinate origem, Coordinate destino)
  {
    double dLon = toRadians( destino.getLongitude().getValue() - origem.getLongitude().getValue() );
    double dlr = destino.getLatitude().inRadians();
    double y = sin( dLon ) * cos( dlr );
    double olr = origem.getLatitude().inRadians();
    double x = cos( olr ) * sin( dlr ) - sin( olr ) * cos( dlr ) * cos( dLon );
    return toBearing( atan2( y, x ) );
  }

  /**
   * Returns the final bearing from source to destination.
   * @param origem Coordinate of source point.
   * @param destino Coordinate of destination point.
   * @return The final bearing angle ranging from 0 to 360 degrees.
  */
  public static double finalBearing(Coordinate origem, Coordinate destino)
  {
    return (initialBearing(destino, origem) + 180) % 360;
  }

  /**
   * Returns the coordinates of destination point.
   * <pre>Important: Uses the {@link #EARTH_MEAN_RADIUS}.</pre>
   * @param origem Coordinate of source point.
   * @param bearing Initial bearing angle in degrees.
   * @param distance Distance in kilometeres.
   * @return The coordinates of destination point<br>or <b>Null</b>
   * if can't reach a numeric value trough sequence of calculus.
  */
  public static Coordinate destinationPoint(Coordinate origem, double bearing, double distance)
  {
    double radius = EARTH_MEAN_RADIUS;
    double lat1 = origem.getLatitude().inRadians();
    double cl1 = cos(lat1), sl1 = sin(lat1);
    bearing = toRadians( bearing );
    double dr, cdr = cos(dr = distance / radius), sdr = sin(dr);
    double lat2 = asin( sl1 * cdr + cl1 * sdr * cos(bearing) );
    double lon2 = origem.getLongitude().inRadians() + atan2( sin(bearing) * sdr * cl1, cdr - sl1 * sin(lat2) );
    if (Double.isNaN(lat2) || Double.isNaN(lon2)) return null;
    lat2 = toDegrees(lat2);
    lon2 = toDegrees(lon2);
    return new Coordinate(new Latitude(lat2), new Longitude(lon2));
  }

  /**
   * <pre>Note: A <b>Rhumb Line</b> (<i>or loxodrome</i>) is a path of
   * constant bearing which crosses all meridians at the same angle.
   * Sailors used to navigate along Rhumb Lines since it is easier to follow
   * a constant compass bearing than to constantly adjust the bearing as is
   * needed to follow a great circle. <b>Rhumb Lines</b> are straight lines on
   * a <i>Mercator Projection map</i>.</pre>
   * @param origem Coordinate of source point.
   * @param destino Coordinate of destination point.
   * @return The polar coordinate <i>(Distance; Angle)</i> of a place.
  */
  public static PolarCoordinate rhumbLines(Coordinate origem, Coordinate destino)
  {
    double dLat = toRadians( destino.getLatitude().getValue() - origem.getLatitude().getValue() );
    double dLon = toRadians( destino.getLongitude().getValue() - origem.getLongitude().getValue() );
    double dPhi = log(tan(destino.getLatitude().inRadians() / 2 + PI / 4) / tan(origem.getLatitude().inRadians() / 2 + PI / 4));
    double q = dLat / dPhi;
    if (Double.isInfinite(q)) {
      q = cos(origem.getLatitude().inRadians());
    }
    // if dLon over 180° take shorter rhumb across 180° meridian:
    if (abs(dLon) > PI) {
      dLon = dLon > 0 ? -(2 * PI - dLon) : (2 * PI + dLon);
    }
    double radius = EARTH_MEAN_RADIUS;
    double distance = sqrt(dLat * dLat + q * q * dLon * dLon) * radius;
    double bearing = toBearing( atan2(dLon, dPhi) );
    return new PolarCoordinate(distance, bearing);
  }

  /**
   * <pre>Note: Given a start point and a distance along a constant bearing,
   * this will calculate the destination point. If someone maintain
   * a constant bearing along a rhumb line, he will gradually spiral
   * in towards one of the poles.</pre>
   * <pre>Important: Uses the {@link #EARTH_MEAN_RADIUS}.</pre>
   * @param origem Coordinate of source point.
   * @param bearing Initial bearing angle in degrees.
   * @param distance Distance in kilometeres.
   * @return The coordinate of the <b>rhumb</b> destination point.
  */
  public static Coordinate rhumbDestinationPoint(Coordinate origem, double bearing, double distance)
  {
    bearing = toRadians(bearing);
    // angular distance
    double a = distance / EARTH_MEAN_RADIUS;
    double olr = origem.getLatitude().inRadians();
    double lat2 = olr + a * cos(bearing);
    double dPhi = log( tan(lat2 / 2 + PI / 4) / tan(olr / 2 + PI / 4) );
    double q = (lat2 - olr) / dPhi;
    // if E:W line then q is length of a circle of latitude
    if (Double.isInfinite(q)) q = Math.cos(olr);
    double dLon = a * sin(bearing) / q;
    // check for some daft bugger going past the pole
    if (abs(lat2) > PI / 2) {
      lat2 = lat2 > 0 ? PI - lat2 : -PI - lat2;
    }
    double lon2 = ((origem.getLongitude().inRadians() + dLon + PI) % (2 * PI)) - PI;
    lat2 = toDegrees(lat2);
    lon2 = toDegrees(lon2);
    return new Coordinate( new Latitude(lat2), new Longitude(lon2) );
  }

  /** API usage demonstration. */
  public static void main(String[] args)
  {
    if (args.length != 18) {
      System.out.format("%nUsage: java -jar neogeo.jar place_X latitude_X longitude_X place_Y latitude_Y longitude_Y%n%nEach coordinate in degrees, minutes, seconds and direction e.g.:%n%n    java -jar geo.jar SP 23 32 52 S 46 38 9 W CPS 22 54 21 S 47 3 39 W%n%n");
      System.exit(0);
    }

    System.out.format("%nNavigator API Usage Demonstration.%n");

    int i = 0;
    Coordinate
      x = new Coordinate(
        args[i++],
        new Latitude(Integer.parseInt(args[i++]),
            Integer.parseInt(args[i++]), Double.parseDouble(args[i++]),
            args[i++]),
        new Longitude(Integer.parseInt(args[i++]),
            Integer.parseInt(args[i++]), Double.parseDouble(args[i++]),
            args[i++]));

    Coordinate
      y = new Coordinate(
        args[i++],
        new Latitude(Integer.parseInt(args[i++]),
            Integer.parseInt(args[i++]), Double.parseDouble(args[i++]),
            args[i++]),
        new Longitude(Integer.parseInt(args[i++]),
            Integer.parseInt(args[i++]), Double.parseDouble(args[i++]),
            args[i++]));

    double dist = distance(x, y);
    System.out.format("%nDistance from %s to %s is %,.3f km.%n",
      x.asString(), y.asString(), dist);

    System.out.format("%nMidPoint is %s%n", midPoint(x, y).asString());

    double brg = initialBearing(x, y);
    System.out.format("%nInitial bearing is %s.%n", toDMS( brg ));

    System.out.format("%nRhumb destination Point: %s%n",  rhumbDestinationPoint(x, brg, dist).asString() );

    brg = finalBearing(x, y);
    System.out.format("%nFinal bearing is %s.%n%n", toDMS( brg ));

    PolarCoordinate p = rhumbLines(x, y);
    p.setId("Rhumb lines coordinate");
    System.out.format("%s%n%n", p.asString() );
  }
}