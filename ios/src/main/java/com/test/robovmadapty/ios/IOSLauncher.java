package com.test.robovmadapty.ios;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.test.robovmadapty.RobovmAdaptyMain;
import com.test.robovmadapty.ios.proto.App;
import com.test.robovmadapty.ios.proto.ExternalStore;
import com.test.robovmadapty.ios.proto.ExternalStoreItemData;

import java.util.List;

/** Launches the iOS (RoboVM) application. */
public class IOSLauncher extends IOSApplication.Delegate {
	@Override
	protected IOSApplication createApplication() {

		IosAdaptyStore adaptyStore = new IosAdaptyStore();

		// Test user
		adaptyStore.setUser(3);

		adaptyStore.retrieveItems(new ExternalStore.ItemsListener() {
			@Override
			public void onReceive(List<ExternalStoreItemData> items) {
				App.log("Items received");
			}
		});

		IOSApplicationConfiguration configuration = new IOSApplicationConfiguration();
		return new IOSApplication(new RobovmAdaptyMain(), configuration);
	}

	public static void main(String[] argv) {
		NSAutoreleasePool pool = new NSAutoreleasePool();
		UIApplication.main(argv, null, IOSLauncher.class);
		pool.close();
	}
}