/**
 * Copyright 2012 John Lawson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.jwlawson.hyperbolic.client.geometry.euclidean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import uk.co.jwlawson.hyperbolic.client.geometry.Line;
import uk.co.jwlawson.hyperbolic.client.geometry.Point;
import uk.co.jwlawson.hyperbolic.client.geometry.euclidean.EuclLine.Factory;

/**
 * @author John
 * 
 */
public class EuclLineFactoryTest {

	private EuclLine.Factory factory;

	@Before
	public void setup() {
		factory = new Factory(400, 400);
	}

	/**
	 * Test method for
	 * {@link uk.co.jwlawson.hyperbolic.client.geometry.euclidean.EuclLine.Factory#getPerpendicularBisector(uk.co.jwlawson.hyperbolic.client.geometry.Point, uk.co.jwlawson.hyperbolic.client.geometry.Point)}
	 * .
	 */
	@Test
	public void testHorizontalPointsPerpendicularBisector() {
		EuclPoint p1, p2;
		Line line;
		p1 = new EuclPoint(5, 5);
		p2 = new EuclPoint(0, 5);
		line = factory.getPerpendicularBisector(p1, p2);

		assertTrue(line.contains(new Point(2.5, 5)));
		assertTrue(line.contains(new Point(2.5, 0)));
		assertTrue(line.contains(new Point(2.5, -20)));

	}

	@Test
	public void testOriginPerpBisector() {
		EuclPoint p1 = new EuclPoint(0, 0);
		EuclPoint p2 = new EuclPoint(1, 1);
		Line line = factory.getPerpendicularBisector(p1, p2);

		assertTrue(line.contains(new Point(0.5, 0.5)));
		assertTrue(line.contains(new Point(1, 0)));
	}

	@Test
	public void testVerticalPointsPerpBisector() {
		EuclPoint p1;
		EuclPoint p2;
		Line line;
		p1 = new EuclPoint(0, 0);
		p2 = new EuclPoint(0, 2);
		line = factory.getPerpendicularBisector(p1, p2);

		assertFalse("Contains input point 1", line.contains(p1));
		assertFalse("Contains input point 2", line.contains(p2));
		assertTrue(line.contains(new Point(0, 1)));
		assertTrue(line.contains(new Point(1, 1)));
	}

	/**
	 * Test method for
	 * {@link uk.co.jwlawson.hyperbolic.client.geometry.euclidean.EuclLine.Factory#getGeodesicThrough(uk.co.jwlawson.hyperbolic.client.geometry.Point, uk.co.jwlawson.hyperbolic.client.geometry.Point)}
	 * .
	 */
	@Test
	public void testVerticalGeodesic() {
		EuclPoint p1 = new EuclPoint(1, 1);
		EuclPoint p2 = new EuclPoint(1, 5);
		Line line = factory.getGeodesicThrough(p1, p2);

		assertTrue(line.contains(new Point(1, 3)));
		assertTrue(line.contains(new Point(1, 10)));
		assertFalse(line.contains(new Point(0.999, 0)));
	}

	@Test
	public void testHorizontalGeodesic() {
		EuclPoint p1 = new EuclPoint(5, 5);
		EuclPoint p2 = new EuclPoint(20, 5);
		Line line = factory.getGeodesicThrough(p1, p2);

		assertTrue(line.contains(p1));
		assertTrue(line.contains(p2));
		assertTrue(line.contains(new Point(-10, 5)));

	}

	@Test
	public void testGradGeodesic() {
		EuclPoint p1 = new EuclPoint(1, 1);
		EuclPoint p2 = new EuclPoint(5, 9);
		Line line = factory.getGeodesicThrough(p1, p2);

		assertTrue(line.contains(new Point(3, 5)));
	}

	@Test
	public void testGeodesicEndPoints() {
		EuclPoint p1 = new EuclPoint(-8.27, 7.1290);
		EuclPoint p2 = new EuclPoint(19.8632, 1.1111111111111);
		Line line = factory.getGeodesicThrough(p1, p2);

		assertTrue(line.contains(p1));
		assertTrue(line.contains(p2));

	}

}
