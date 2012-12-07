package uk.co.jwlawson.hyperbolic.client.geometry.euclidean;

import uk.co.jwlawson.hyperbolic.client.geometry.Line;
import uk.co.jwlawson.hyperbolic.client.geometry.LineFactory;
import uk.co.jwlawson.hyperbolic.client.geometry.Point;

public class EuclLineFactory implements LineFactory {

	private int width;
	private int height;

	private double m, c;

	public EuclLineFactory(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public Line getPerpendicularBisector(Point p1, Point p2) {

		calcM(p1, p2);

		Point half = getHalfWayPoint(p1, p2);

		if (doubleEquals(m, 0)) {
			return new EuclLine(half.getX(), -height, half.getX(), height);
		}
		m = -1 / m;
		calcCthroughPoint(half);

		return getLineFromMandC();
	}

	private Point getHalfWayPoint(Point p1, Point p2) {
		return new Point((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
	}

	@Override
	public Line getGeodesicThrough(Point p1, Point p2) {

		if (doubleEquals(p1.getX(), p2.getX())) {
			// Vertical line, so handle separately
			return new EuclLine(p1.getX(), -height, p1.getX(), height);
		}
		calcM(p1, p2);
		calcCthroughPoint(p1);

		return getLineFromMandC();
	}

	private Line getLineFromMandC() {
		Point start = getStart();
		Point end = getEnd();

		Line line = new EuclLine(start.getX(), start.getY(), end.getX(), end.getY());

		return line;
	}

	private void calcM(Point p1, Point p2) {
		m = (p1.getY() - p2.getY()) / (p1.getX() - p2.getX());
	}

	private void calcCthroughPoint(Point p1) {
		c = p1.getY() - m * p1.getX();
	}

	private Point getStart() {
		double x = -width;
		double y = getY(x);

		if (validY(y)) {
			return new Point(x, y);
		}
		x = width;
		y = getY(x);
		if (validY(y)) {
			return new Point(x, y);
		}
		y = height;
		x = getX(y);
		if (validX(x)) {
			return new Point(x, y);
		}
		y = -height;
		x = getX(y);
		if (validX(x)) {
			return new Point(x, y);
		}
		return new Point(0, 0);
	}

	public boolean validY(double y) {
		return y <= height && y >= -height;
	}

	public boolean validX(double x) {
		return x <= width && x >= -width;
	}

	private Point getEnd() {

		double y = -height;
		double x = getX(y);
		if (validX(x)) {
			return new Point(x, y);
		}
		y = height;
		x = getX(y);
		if (validX(x)) {
			return new Point(x, y);
		}
		x = width;
		y = getY(x);
		if (validY(y)) {
			return new Point(x, y);
		}
		x = -width;
		y = getY(x);
		if (validY(y)) {
			return new Point(x, y);
		}
		return new Point(0, 0);
	}

	private boolean doubleEquals(double a, double b) {
		return Math.abs(a - b) < 0.00001;
	}

	private double getX(double y) {
		return (y - c) / m;
	}

	private double getY(double x) {
		return m * x + c;
	}

	@Override
	public Line getSegmentJoining(Point p1, Point p2) {
		return new EuclLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}
}