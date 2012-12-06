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

import java.util.logging.Logger;

import uk.co.jwlawson.hyperbolic.client.ui.Focal;
import uk.co.jwlawson.hyperbolic.client.ui.Tiling;
import uk.co.jwlawson.hyperbolic.client.widget.Slider;
import uk.co.jwlawson.hyperbolic.client.widget.SliderEvent;
import uk.co.jwlawson.hyperbolic.client.widget.SliderListener;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author John Lawson
 */
public class Hyperbolic implements EntryPoint, SliderListener {

	private static final String TILING_MOUNT_ID = "tilingcanvas";
	private static final String FOCAL_MOUNT_ID = "focalcanvas";
	private static final String SLIDER_ID = "options";
	private static final String SLIDER_VALUE_STYLE = "slider-values";
	private static final String A_LABEL = "a = sqrt(2) + ";
	private static final String B_LABEL = "t = ";

	private static final Logger log = Logger.getLogger("Hyperbolic");

	private Tiling mTiling;
	private Focal mFocal;

	private Slider aSlider;
	private Slider bSlider;

	private Label aValue, bValue;

	@Override
	public void onModuleLoad() {

		aValue = new Label(A_LABEL + "0");
		aValue.addStyleName(SLIDER_VALUE_STYLE);
		aSlider = new Slider("a", -69, 100, 0);
		aSlider.addListener(this);

		RootPanel.get(SLIDER_ID).add(aValue);
		RootPanel.get(SLIDER_ID).add(aSlider);

		bValue = new Label(B_LABEL + "-1");
		bValue.addStyleName(SLIDER_VALUE_STYLE);
		bSlider = new Slider("b", -200, 0, -100);
		bSlider.addListener(this);

		RootPanel.get(SLIDER_ID).add(bValue);
		RootPanel.get(SLIDER_ID).add(bSlider);

		mTiling = new Tiling();
		mFocal = new Focal();

		mTiling.addToPanel(RootPanel.get(TILING_MOUNT_ID));
		mFocal.addToPanel(RootPanel.get(FOCAL_MOUNT_ID));

		Window.addResizeHandler(new ResizeHandler() {

			Timer resizeTimer = new Timer() {
				@Override
				public void run() {
					doLayoutCalculations();
				}

			};

			@Override
			public void onResize(ResizeEvent event) {
				resizeTimer.schedule(100);
			}
		});
	}

	private void doLayoutCalculations() {
		mTiling.initSize();
		mFocal.initSize();
	}

	@Override
	public void onStart(SliderEvent e) {

	}

	@Override
	public boolean onSlide(SliderEvent e) {
		Slider source = e.getSource();

		if (aSlider == source) {
			aValue.setText(A_LABEL + (double) e.getValues()[0] / 100);
			reloadTimer.schedule(500);
			return true;
		}
		if (bSlider == source) {
			bValue.setText(B_LABEL + (double) e.getValues()[0] / 100);
			reloadTimer.schedule(500);
			return true;
		}
		return false;
	}

	private Timer reloadTimer = new Timer() {

		@Override
		public void run() {
			mFocal.setParams(1 / Math.sqrt(2) + ((double) aSlider.getValueAtIndex(0) / 100),
					((double) bSlider.getValueAtIndex(0) / 100));
		}
	};

	@Override
	public void onChange(SliderEvent e) {

	}

	@Override
	public void onStop(SliderEvent e) {

	}
}