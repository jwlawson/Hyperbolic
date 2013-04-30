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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import uk.co.jwlawson.hyperbolic.client.geometry.Point;
import uk.co.jwlawson.hyperbolic.client.group.OrbitIter;
import uk.co.jwlawson.hyperbolic.client.ui.PointHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;

/**
 * @author Administrator
 * 
 */
public abstract class PointGen {

	private static final Logger log = Logger.getLogger(PointGen.class.getName());

	private boolean stop = false;
	private Point mOrigin;

	private List<PointHandler> mHandlers;
	private OrbitIter orbit;

	public PointGen() {
		mHandlers = new ArrayList<PointHandler>();
	}

	public void addPointHandler(PointHandler handler) {
		mHandlers.add(handler);
	}

	public void stop() {
		stop = true;
	}

	public void start() {
		initPoints();
	}

	protected void setInitialPoint(Point initial) {
		log.fine("Point set " + initial);
		mOrigin = initial;
	}

	protected void setOrbitIter(OrbitIter iter) {
		orbit = iter;
	}

	protected void initPoints() {
		for (PointHandler handler : mHandlers) {
			handler.clear();
		}
		log.info("Loading points");
		orbit.setStart(mOrigin);
		mOrigin = orbit.next();
		for (PointHandler handler : mHandlers) {
			handler.addInitialPoint(mOrigin);
		}
		stop = false;

		Scheduler.get().scheduleIncremental(new RepeatingCommand() {

			@Override
			public boolean execute() {
				if (stop) {
					return false;
				}
				Point next = orbit.next();
				for (PointHandler handler : mHandlers) {
					handler.addPoint(next);
					if (stop) {
						return false;
					}
				}

				if (!orbit.hasNext()) {
					computeFinished();
				}

				return orbit.hasNext() && !stop;
			}

		});

	}

	private void computeFinished() {
		log.info("Points loaded");
		for (PointHandler handler : mHandlers) {
			handler.pointsAdded();
		}
	}
}
