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
package uk.co.jwlawson.hyperbolic.client.group;

import uk.co.jwlawson.hyperbolic.client.geometry.Point;

/**
 * @author John
 * 
 */
public class OrbitPoint extends Point {

	private IsomEnum e;

	public OrbitPoint(double x, double y) {
		super(x, y);
	}

	public OrbitPoint(Point p) {
		super(p);
	}

	public OrbitPoint(double x, double y, IsomEnum e) {
		super(x, y);
		this.e = e;
	}

	public OrbitPoint(OrbitPoint p) {
		super(p);
		this.e = p.getE();
	}

	public void setE(IsomEnum e) {
		this.e = e;
	}

	public IsomEnum getE() {
		return e;
	}
}
