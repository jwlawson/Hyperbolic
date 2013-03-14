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
package uk.co.jwlawson.hyperbolic.client;

import uk.co.jwlawson.hyperbolic.client.geometry.Point;
import uk.co.jwlawson.hyperbolic.client.group.BoundedTorusOrbit;

/**
 * @author Administrator
 * 
 */
public class TorusPointGen extends PointGen {

	private double x = 1;
	private double y = 1;
	private float width, height;

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	@Override
	public void start() {
		setInitialPoint(new Point((y - 1) / (y + 1), 0));
		setOrbitIter(new BoundedTorusOrbit((float) x, (float) y, width, height));
		super.start();
	}

}
