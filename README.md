# React Native Plugin for HotLoad

This plugin provides client-side integration for the [HotLoad service](http://hotload.tools), allowing you to easily add a dynamic update experience to your React Native app(s).

* [How does it work?](#how-does-it-work)
* [Supported React Native Platforms](#supported-react-native-platforms)
* [Supported Components](#supported-components)
* [Getting Started](#getting-started)
* [iOS Setup](#ios-setup)
    * [Plugin Installation](#plugin-installation-ios)
        * [RNPM](#plugin-installation-ios---rnpm)
        * [CocoaPods](#plugin-installation-ios---cocoapods)
        * ["Manual"](#plugin-installation-ios---manual)
    * [Plugin Configuration](#plugin-configuration-ios)
* [Android Setup](#android-setup)
    * [Plugin Installation](#plugin-installation-android)
    * [Plugin Configuration (React Native < v0.18.0)](#plugin-configuration-android---react-native--v0180)
    * [Plugin Configuration (React Native 0.18.0+)](#plugin-configuration-android---react-native-v0180)
* [Plugin Usage](#plugin-usage)
* [Releasing Updates (JavaScript-only)](#releasing-updates-javascript-only)
* [Releasing Updates (JavaScript + images)](#releasing-updates-javascript--images)
* [API Reference](#api-reference)
    * [JavaScript API](#javascript-api-reference)
    * [Objective-C API Reference (iOS)](#objective-c-api-reference-ios)
    * [Java API Reference (Android)](#java-api-reference-android)
* [Example Apps](#example-apps)
* [Debugging / Troubleshooting](#debugging--troubleshooting)

## How does it work?

A React Native app is composed of JavaScript files and any accompanying [images](https://facebook.github.io/react-native/docs/images.html#content), which are bundled together by the [packager](https://github.com/facebook/react-native/tree/master/packager) and distributed as part of a platform-specific binary (i.e. an `.ipa` or `.apk` file). Once the app is released, updating either the JavaScript code (e.g. making bug fixes, adding new features) or image assets, requires you to recompile and redistribute the entire binary, which of course, includes any review time associated with the store(s) you are publishing to.

The HotLoad plugin helps get product improvements in front of your end users instantly, by keeping your JavaScript and images synchronized with updates you release to the HotLoad server. This way, your app gets the benefits of an offline mobile experience, as well as the "web-like" agility of side-loading updates as soon as they are available. It's a win-win!

In order to ensure that your end users always have a functioning version of your app, the HotLoad plugin maintains a copy of the previous update, so that in the event that you accidentally push an update which includes a crash, it can automatically roll back. This way, you can rest assured that your newfound release agility won't result in users becoming blocked before you have a chance to [roll back](http://microsoft.github.io/code-push/docs/cli.html#link-8) on the server. It's a win-win-win! 

*Note: Any product changes which touch native code (e.g. modifying your `AppDelegate.m`/`MainActivity.java` file, adding a new plugin) cannot be distributed via HotLoad, and therefore, must be updated via the appropriate store(s).*

## Supported React Native platforms

- iOS
- Android

We try our best to maintain backwards compatability of our plugin with previous versions of React Native, but due to the nature of the platform, and the existence of breaking changes between releases, it is possible that you need to use a specific version of the HotLoad plugin in order to support the exact version of React Native you are using. The following table outlines which HotLoad plugin versions officially support the respective React Native versions:

| React Native version(s) | Supporting HotLoad version(s)                 |
|-------------------------|------------------------------------------------|
| <0.14.0                 | **Unsupported**                                |
| v0.14.0                 | v1.3.0 *(introduced Android support)*          |
| v0.15.0-v0.18.0         | v1.4.0-v1.6.0 *(introduced iOS asset support)* |
| v0.19.0-v0.22.0         | v1.7.0+ *(introduced Android asset support)*   |
| v0.23.0+                | TBD :) We work hard to respond to new RN releases, but they do occasionally break us. We will update this chart with each RN release, so that users can check to see what our "official" support is.

## Supported Components

When using the React Native assets sytem (i.e. using the `require("./foo.png")` syntax), the following list represents the set of components (and props) that support having their referenced images updated via HotLoad:

| Component                                       | Prop(s)                                  | 
|-------------------------------------------------|------------------------------------------|
| `Image`                                         | `source`                                    |
| `ProgressViewIOS`                               | `progressImage`, `trackImage`            |
| `TabBarIOS.Item`                                | `icon`, `selectedIcon`                   |
| `ToolbarAndroid` <br />*(React Native 0.21.0+)* | `actions[].icon`, `logo`, `overflowIcon` |

The following list represents the set of components (and props) that don't currently support their assets being updated via HotLoad, due to their dependency on static images (i.e. using the `{ uri: "foo"}` syntax):

| Component   | Prop(s)                                                              |
|-------------|----------------------------------------------------------------------|
| `SliderIOS` | `maximumTrackImage`, `minimumTrackImage`, `thumbImage`, `trackImage` |

As new core components are released, which support referencing assets, we'll update this list to ensure users know what exactly they can expect to update using HotLoad.

## Getting Started

Once you've followed the general-purpose ["getting started"](http://hotload.tools/docs/getting-started.html) instructions for setting up your HotLoad account, you can start HotLoad-ifying your React Native app by running the following command from within your app's root directory:

```shell
npm install --save react-native-code-push
```

As with all other React Native plugins, the integration experience is different for iOS and Android, so perform the following setup steps depending on which platform(s) you are targetting.

If you want to see how other projects have integrated with HotLoad, you can check out the excellent [example apps](#example-apps) provided by the community.

## iOS Setup

Once you've acquired the HotLoad plugin, you need to integrate it into the Xcode project of your React Native app and configure it correctly. To do this, take the following steps:

### Plugin Installation (iOS)

In order to accomodate as many developer preferences as possible, the HotLoad plugin supports iOS installation via three mechanisms:

1. [**RNPM**](#plugin-installation-ios---rnpm) - [React Native Package Manager (RNPM)](https://github.com/rnpm/rnpm) is an awesome tool that provides the simplest installation experience possible for React Native plugins. If you're already using it, or you want to use it, then we recommend this approach. 

2. [**CocoaPods**](#plugin-installation-ios---cocoapods) - If you're building a native iOS app that is embedding React Native into it, or you simply prefer using [CocoaPods](https://cocoapods.org), then we recommend using the Podspec file that we ship as part of our plugin.

3. [**"Manual"**](#plugin-installation-ios---manual) - If you don't want to depend on any additional tools or are fine with a few extra installation steps (it's a one-time thing), then go with this approach.

#### Plugin Installation (iOS - RNPM)

1. Run `rnpm link react-native-code-push`

    *Note: If you don't already have RNPM installed, you can do so by simply running `npm i -g rnpm` and then executing the above command.*
    
2. Open your app's Xcode project

3. Select the project node in Xcode and select the "Build Phases" tab of your project configuration.

4. Click the plus sign underneath the "Link Binary With Libraries" list and select the `libz.tbd` library underneath the `iOS` node that matches your target version. 

    ![Libz reference](https://cloud.githubusercontent.com/assets/116461/11605042/6f786e64-9aaa-11e5-8ca7-14b852f808b1.png)
    
    *Note: Alternatively, if you prefer, you can add the `-lz` flag to the `Other Linker Flags` field in the `Linking` section of the `Build Settings`.*

We hope to eventually remove the need for steps #2-4, but in the meantime, RNPM doesn't support automating them.

#### Plugin Installation (iOS - CocoaPods)

1. Add the HotLoad plugin dependency to your `Podfile`, pointing at the path where NPM installed it

    ```ruby
    pod 'HotLoad', :path => './node_modules/react-native-code-push`
    ```
    
2. Run `pod install`

#### Plugin Installation (iOS - Manual)

1. Open your app's Xcode project

2. Find the `HotLoad.xcodeproj` file within the `node_modules/react-native-code-push/ios` directory (or `node_modules/react-native-code-push` for <=`1.7.3-beta` installations) and drag it into the `Libraries` node in Xcode

    ![Add HotLoad to project](https://cloud.githubusercontent.com/assets/8598682/13368613/c5c21422-dca0-11e5-8594-c0ec5bde9d81.png)

3. Select the project node in Xcode and select the "Build Phases" tab of your project configuration.

4. Drag `libHotLoad.a` from `Libraries/HotLoad.xcodeproj/Products` into the "Link Binary With Libraries" section of your project's "Build Phases" configuration.

    ![Link HotLoad during build](https://cloud.githubusercontent.com/assets/516559/10322221/a75ea066-6c31-11e5-9d88-ff6f6a4d6968.png)

5. Click the plus sign underneath the "Link Binary With Libraries" list and select the `libz.tbd` library underneath the `iOS 9.1` node. 

    ![Libz reference](https://cloud.githubusercontent.com/assets/116461/11605042/6f786e64-9aaa-11e5-8ca7-14b852f808b1.png)
    
    *Note: Alternatively, if you prefer, you can add the `-lz` flag to the `Other Linker Flags` field in the `Linking` section of the `Build Settings`.*
    
6. Under the "Build Settings" tab of your project configuration, find the "Header Search Paths" section and edit the value.
Add a new value, `$(SRCROOT)/../node_modules/react-native-code-push` and select "recursive" in the dropdown.

    ![Add HotLoad library reference](https://cloud.githubusercontent.com/assets/516559/10322038/b8157962-6c30-11e5-9264-494d65fd2626.png)

### Plugin Configuration (iOS)

Once your Xcode project has been setup to build/link the HotLoad plugin, you need to configure your app to consult HotLoad for the location of your JS bundle, since it is responsible for synchronizing it with updates that are released to the HotLoad server. To do this, perform the following steps:

1. Open up the `AppDelegate.m` file, and add an import statement for the HotLoad headers:

    ```objective-c
    #import "HotLoad.h"
    ```

2. Find the following line of code, which loads your JS Bundle from the app binary for production releases:

    ```objective-c
    jsCodeLocation = [[NSBundle mainBundle] URLForResource:@"main" withExtension:@"jsbundle"];
    ```
    
3. Replace it with this line:

    ```objective-c
    jsCodeLocation = [HotLoad bundleURL];
    ```

This change configures your app to always load the most recent version of your app's JS bundle. On the first launch, this will correspond to the file that was compiled with the app. However, after an update has been pushed via HotLoad, this will return the location of the most recently installed update.

*NOTE: The `bundleURL` method assumes your app's JS bundle is named `main.jsbundle`. If you have configured your app to use a different file name, simply call the `bundleURLForResource:` method (which assumes you're using the `.jsbundle` extension) or `bundleURLForResource:withExtension:` method instead, in order to overwrite that default behavior*

Typically, you're only going to want to use HotLoad to resolve your JS bundle location within release builds, and therefore, we recommend using the `DEBUG` pre-processor macro to dynamically switch between using the packager server and HotLoad, depending on whether you are debugging or not. This will make it much simpler to ensure you get the right behavior you want in production, while still being able to use the Chrome Dev Tools, live reload, etc. at debug-time. 

```objective-c
NSURL *jsCodeLocation;

#ifdef DEBUG
    jsCodeLocation = [NSURL URLWithString:@"http://localhost:8081/index.ios.bundle?platform=ios&dev=true"];
#else
    jsCodeLocation = [HotLoad bundleURL];
#endif
```
    
To let the HotLoad runtime know which deployment it should query for updates against, perform the following steps:

1. Open your app's `Info.plist` file and add a new entry named `HotLoadDeploymentKey`, whose value is the key of the deployment you want to configure this app against (e.g. the key for the `Staging` deployment for the `FooBar` app). You can retrieve this value by running `code-push deployment ls <appName> -k` in the HotLoad CLI (the `-k` flag is necessary since keys aren't displayed by default) and copying the value of the `Deployment Key` column which corresponds to the deployment you want to use (see below). Note that using the deployment's name (e.g. Staging) will not work. That "friendly name" is intended only for authenticated management usage from the CLI, and not for public consumption within your app.

    ![Deployment list](https://cloud.githubusercontent.com/assets/116461/11601733/13011d5e-9a8a-11e5-9ce2-b100498ffb34.png)
    
2. In your app's `Info.plist` make sure your `Bundle versions string, short` (aka `CFBundleShortVersionString`) value is a valid [semver](http://semver.org/) version. Note that if the value provided is missing a patch version, the HotLoad server will assume it is `0`, i.e. `1.0` will be treated as `1.0.0`.

    ![Bundle version](https://cloud.githubusercontent.com/assets/116461/12307416/f9b82688-b9f3-11e5-839a-f1c6b4acd093.png)
    
## Android Setup

In order to integrate HotLoad into your Android project, perform the following steps:

### Plugin Installation (Android)

1. In your `android/settings.gradle` file, make the following additions:
    
    ```gradle
    include ':app', ':react-native-code-push'
    project(':react-native-code-push').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-code-push/android/app')
    ```
2. In your `android/app/build.gradle` file, add the `:react-native-code-push` project as a compile-time dependency:
    
    ```gradle
    ...
    dependencies {
        ...
        compile project(':react-native-code-push')
    }
    ```   
3. (Only needed in v1.8.0+ of the plugin) In your `android/app/build.gradle` file, add the `hotload.gradle` file as an additional build task definition underneath `react.gradle`:
    
    ```gradle
    ...
    apply from: "react.gradle"
    apply from: "../../node_modules/react-native-code-push/android/hotload.gradle"
    ...
    ```

### Plugin Configuration (Android - React Native < v0.18.0)

*NOTE: These instructions are specific to apps that are using React Native v0.15.0-v0.17.0. If you are using v0.18.0+, then skip ahead to the next section.*

After installing the plugin and syncing your Android Studio project with Gradle, you need to configure your app to consult HotLoad for the location of your JS bundle, since it will "take control" of managing the current and all future versions. To do this, perform the following steps:

1. Update the `MainActivity.java` file to use HotLoad via the following changes:
    
    ```java
    ...
    // 1. Import the plugin class
    import com.microsoft.hotload.react.HotLoad;
    
    // 2. Optional: extend FragmentActivity if you intend to show a dialog prompting users about updates.
    //    If you do this, make sure to also add "import android.support.v4.app.FragmentActivity" below #1.
    public class MainActivity extends FragmentActivity implements DefaultHardwareBackBtnHandler {
        ...
        
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            ...
            // 3. Initialize HotLoad with your deployment key and an instance of your MainActivity. If you don't
            // already have it, you can run "code-push deployment ls <appName> -k" in order to retrieve your key.
            HotLoad hotLoad = new HotLoad("d73bf5d8-4fbd-4e55-a837-accd328a21ba", this, BuildConfig.DEBUG);
            ...
            mReactInstanceManager = ReactInstanceManager.builder()
                    .setApplication(getApplication())
                    ...
                    // 4. DELETE THIS LINE --> .setBundleAssetName("index.android.bundle")
                    
                    // 5. Let HotLoad determine which location to load the most updated bundle from.
                    // If there is no updated bundle from HotLoad, the location will be the assets
                    // folder with the name of the bundle passed in, e.g. index.android.bundle
                    .setJSBundleFile(hotLoad.getBundleUrl("index.android.bundle"))
                    
                    // 6. Expose the HotLoad module to JavaScript.
                    .addPackage(hotLoad.getReactPackage())
                    ...
        }
    }
    ```

2. Ensure that the `android.defaultConfig.versionName` property in your `android/app/build.gradle` file is set to a semver compliant value. Note that if the value provided is missing a patch version, the HotLoad server will assume it is `0`, i.e. `1.0` will be treated as `1.0.0`.
    
    ```gradle
    android {
        ...
        defaultConfig {
            ...
            versionName "1.0.0"
            ...
        }
        ...
    }
    ```
    
### Plugin Configuration (Android - React Native v0.18.0+)

*NOTE: These instructions are specific to apps that are using React Native v0.18.0+. If you are using v0.15.0-v0.17.0, then refer to the previous section.*

After installing the plugin and syncing your Android Studio project with Gradle, you need to configure your app to consult HotLoad for the location of your JS bundle, since it will "take control" of managing the current and all future versions. To do this, perform the following steps:

1. Update the `MainActivity.java` file to use HotLoad via the following changes:
    
    ```java
    ...
    // 1. Import the plugin class
    import com.microsoft.hotload.react.HotLoad;

    public class MainActivity extends ReactActivity {
        // 2. Define a private field to hold the HotLoad runtime instance
        private HotLoad _hotLoad;

        ...

        // 3. Override the getJSBundleFile method in order to let
        // the HotLoad runtime determine where to get the JS
        // bundle location from on each app start
        @Override
        protected String getJSBundleFile() {
            return this._hotLoad.getBundleUrl("index.android.bundle");
        }

        @Override
        protected List<ReactPackage> getPackages() {
            // 4. Instantiate an instance of the HotLoad runtime, using the right deployment key. If you don't
            // already have it, you can run "code-push deployment ls <appName> -k" to retrieve your key.
            this._hotLoad = new HotLoad("0dsIDongIcoH0mqAmoR0CYb5FhBZNy1w4Bf-l", this, BuildConfig.DEBUG);

            // 5. Add the HotLoad package to the list of existing packages
            return Arrays.<ReactPackage>asList(
                new MainReactPackage(), this._hotLoad.getReactPackage());
        }

        ...
    }
    ```
    
2. If you used RNPM to install/link the HotLoad plugin, there are two additional changes you'll need to make due to the fact that RNPM makes some assumptions about 3rd party modules that we don't currently support. If you're not using RNPM then simply skip to step #3:

    ```java
    ...
    // 1. Remove the following import statement
    import com.microsoft.hotload.react.HotLoadReactPackage;
    ...
    public class MainActivity extends ReactActivity {
        ...
        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                ...
                new HotLoadReactPackage() // 2. Remove this line
                ...
            );
        }
        ...
    }
    ```

3. Ensure that the `android.defaultConfig.versionName` property in your `android/app/build.gradle` file is set to a semver compliant value. Note that if the value provided is missing a patch version, the HotLoad server will assume it is `0`, i.e. `1.0` will be treated as `1.0.0`.
    
    ```gradle
    android {
        ...
        defaultConfig {
            ...
            versionName "1.0.0"
            ...
        }
        ...
    }
    ```

## Plugin Usage

With the HotLoad plugin downloaded and linked, and your app asking HotLoad where to get the right JS bundle from, the only thing left is to add the necessary code to your app to control the following policies:

1. When (and how often) to check for an update? (e.g. app start, in response to clicking a button in a settings page, periodically at some fixed interval)
2. When an update is available, how to present it to the end user?

The simplest way to do this is to perform the following in your app's root component:

1. Import the JavaScript module for HotLoad:

    ```javascript
    import hotLoad from "react-native-code-push";
    ```

2. Call the `sync` method from within the `componentDidMount` lifecycle event, to initiate a background update on each app start:

    ```javascript
    hotLoad.sync();
    ```

If an update is available, it will be silently downloaded, and installed the next time the app is restarted (either explicitly by the end user or by the OS), which ensures the least invasive experience for your end users. If an available update is mandatory, then it will be installed immediately, ensuring that the end user gets it as soon as possible. Additionally, if you would like to display a confirmation dialog (an "active install"), or customize the update experience in any way, refer to the `sync` method's [API reference](#hotloadsync) for information on how to tweak this default behavior.

<a id="apple-note">*NOTE: While [Apple's developer agreement](https://developer.apple.com/programs/ios/information/iOS_Program_Information_4_3_15.pdf) fully allows performing over-the-air updates of JavaScript and assets (which is what enables HotLoad!), it is against their policy for an app to display an update prompt. Because of this, we recommend that App Store-distributed apps don't enable the `updateDialog` option when calling `sync`, whereas Google Play and internally distributed apps (e.g. Enterprise, Fabric, HockeyApp) can choose to enable/customize it.*</a>

## Releasing Updates (JavaScript-only)

Once your app has been configured and distributed to your users, and you've made some JS changes, it's time to release it to them instantly! If you're not using React Native to bundle images (via `require('./foo.png')`), then perform the following steps. Otherwise, skip to the next section instead:

1. Execute `react-native bundle` (passing the appropriate parameters, see example below) in order to generate the updated JS bundle for your app. You can place this file wherever you want via the `--bundle-output` flag, since the exact location isn't relevant for HotLoad purposes. It's important, however, that you set `--dev false` so that your JS code is optimized appropriately and any "yellow box" warnings won't be displayed.

2. Execute `code-push release <appName> <jsBundleFilePath> <targetBinaryVersion> --deploymentName <deploymentName>` in order to publish the generated JS bundle to the server. The `<jsBundleFilePath>` parameter should equal the value you provided to the `--bundle-output` flag in step #1. Additionally, the `<targetBinaryVersion>` parameter should equal the [**exact app store version**](http://hotload.tools/docs/cli.html#app-store-version-parameter) (i.e. the semver version end users would see when installing it) you want this HotLoad update to target.

    For more info regarding the `release` command and its parameters, refer to the [CLI documentation](http://hotload.tools/docs/cli.html#releasing-app-updates).
    
Example Usage:

```shell
react-native bundle --platform ios --entry-file index.ios.js --bundle-output hotload.js --dev false
code-push release MyApp hotload.js 1.0.0
```
    
And that's it! for more information regarding the CLI and how the release (or promote and rollback) commands work, refer to the [documentation](http://microsoft.github.io/code-push/docs/cli.html). Additionally, if you run into any issues, you can ping us within the [#code-push](https://discord.gg/0ZcbPKXt5bWxFdFu) channel on Reactiflux, [e-mail us](mailto:hotloadfeed@microsoft.com) and/or check out the [troubleshooting](#debugging--troubleshooting) details below.

*Note: Instead of running `react-native bundle`, you could rely on running `xcodebuild` and/or `gradlew assemble` instead to generate the JavaScript bundle file, but that would be unneccessarily doing a native build, when all you need for distributing updates via HotLoad is your JavaScript bundle.*

## Releasing Updates (JavaScript + images)

*Note: Android support for updating assets was added to React Native v0.19.0, and requires you to use HotLoad v1.7.0. If you are using an older version of RN, you can't update assets via HotLoad, and therefore, need to either upgrade or use the previous workflow for simply updating your JS bundle.*

If you are using the new React Native [assets system](https://facebook.github.io/react-native/docs/images.html#content), as opposed to loading your images from the network and/or platform-specific mechanisms (e.g. iOS asset catalogs), then you can't simply pass your jsbundle to HotLoad as demonstrated above. You **MUST** provide your images as well. To do this, simply use the following workflow:

1. Create a new directory that can be used to organize your app's release assets (i.e. the JS bundle and your images). We recommend calling this directory "release" but it can be named anything. If you create this directory within your project's directory, make sure to add it to your `.gitignore` file, since you don't want it checked into source control.

2. When calling `react-native bundle`, specify that your assets and JS bundle go into the directory you created in #1, and that you want a non-dev build for your respective platform and entry file. For example, assuming you called this directory "release", you could run the following command:

    ```shell
    react-native bundle \
    --platform ios \
    --entry-file index.ios.js \
    --bundle-output ./release/main.jsbundle \
    --assets-dest ./release \
    --dev false
    ```

    *NOTE: The file name that you specify as the value for the `--bundle-output` parameter must have one of the following extensions in order to be detected by the plugin: `.js`, `.jsbundle`, `.bundle` (e.g. `main.jsbundle`, `ios.js`). The name of the file isn't relevant, but the extension is used for detectining the bundle within the update contents, and therefore, using any other extension will result in the update failing.*
    
3. Execute `code-push release`, passing the path to the directory you created in #1 as the ["updateContents"](http://hotload.tools/docs/cli.html#package-parameter) parameter, and the [**exact binary version**](http://hotload.tools/docs/cli.html#app-store-version-parameter) that this update is targetting as the ["targetBinaryVersion"](http://hotload.tools/docs/cli.html#app-store-version-parameter) parameter (e.g. `code-push release Foo ./release 1.0.0`). The code-push CLI will automatically handle zipping up the contents for you, so don't worry about handling that yourself.

    For more info regarding the `release` command and its parameters, refer to the [CLI documentation](http://hotload.tools/docs/cli.html#releasing-app-updates).
    
Additionally, the HotLoad client supports differential updates, so even though you are releasing your JS bundle and assets on every update, your end users will only actually download the files they need. The service handles this automatically so that you can focus on creating awesome apps and we can worry about optimizing end user downloads.

If you run into any issues, you can ping us within the [#code-push](https://discord.gg/0ZcbPKXt5bWxFdFu) channel on Reactiflux, [e-mail us](mailto:hotloadfeed@microsoft.com) and/or check out the [troubleshooting](#debugging--troubleshooting) details below.

*Note: Releasing assets via HotLoad requires that you're using React Native v0.15.0+ and HotLoad v1.4.0+ (for iOS) and React Native v0.19.0+ and HotLoad v1.7.0+ (Android). If you are using assets and an older version of the HotLoad plugin, you should not release updates via HotLoad, because it will break your app's ability to load images from the binary. Please test and release appropriately!*

---

## API Reference

The HotLoad plugin is made up of two components:

1. A JavaScript module, which can be imported/required, and allows the app to interact with the service during runtime (e.g. check for updates, inspect the metadata about the currently running app update).

2. A native API (Objective-C and Java) which allows the React Native app host to bootstrap itself with the right JS bundle location.

The following sections describe the shape and behavior of these APIs in detail:

### JavaScript API Reference

When you require `react-native-code-push`, the module object provides the following top-level methods:

* [checkForUpdate](#hotloadcheckforupdate): Asks the HotLoad service whether the configured app deployment has an update available. 

* [getCurrentPackage](#hotloadgetcurrentpackage): Retrieves the metadata about the currently installed update (e.g. description, installation time, size).

* [notifyApplicationReady](#hotloadnotifyapplicationready): Notifies the HotLoad runtime that an installed update is considered successful. If you are manually checking for and installing updates (i.e. not using the [sync](#hotloadsync) method to handle it all for you), then this method **MUST** be called; otherwise HotLoad will treat the update as failed and rollback to the previous version when the app next restarts.

* [restartApp](#hotloadrestartapp): Immediately restarts the app. If there is an update pending, it will be immediately displayed to the end user. Otherwise, calling this method simply has the same behavior as the end user killing and restarting the process.

* [sync](#hotloadsync): Allows checking for an update, downloading it and installing it, all with a single call. Unless you need custom UI and/or behavior, we recommend most developers to use this method when integrating HotLoad into their apps

#### hotLoad.checkForUpdate

```javascript
hotLoad.checkForUpdate(deploymentKey: String = null): Promise<RemotePackage>;
```

Queries the HotLoad service to see whether the configured app deployment has an update available. By default, it will use the deployment key that is configured in your `Info.plist` file (iOS), or `MainActivity.java` file (Android), but you can override that by specifying a value via the optional `deploymentKey` parameter. This can be useful when you want to dynamically "redirect" a user to a specific deployment, such as allowing "Early access" via an easter egg or a user setting switch.

This method returns a `Promise` which resolves to one of two possible values:

1. `null` if there is no update available. This occurs in the following scenarios:

    1. The configured deployment doesn't contain any releases, and therefore, nothing to update.
    2. The latest release within the configured deployment is targeting a different binary version than what you're currently running (either older or newer).
    3. The currently running app already has the latest release from the configured deployment, and therefore, doesn't need it again.
    
2. A [`RemotePackage`](#remotepackage) instance which represents an available update that can be inspected and/or subsequently downloaded.

Example Usage: 

```javascript
hotLoad.checkForUpdate()
.then((update) => {
    if (!update) {
        console.log("The app is up to date!"); 
    } else {
        console.log("An update is available! Should we download it?");
    }
});
```

#### hotLoad.getCurrentPackage

```javascript
hotLoad.getCurrentPackage(): Promise<LocalPackage>;
```

Retrieves the metadata about the currently installed "package" (e.g. description, installation time). This can be useful for scenarios such as displaying a "what's new?" dialog after an update has been applied or checking whether there is a pending update that is waiting to be applied via a resume or restart.

This method returns a `Promise` which resolves to one of two possible values:

1. `null` if the app is currently running the JS bundle from the binary and not a HotLoad update. This occurs in the following scenarios:

    1. The end-user installed the app binary and has yet to install a HotLoad update
    1. The end-user installed an update of the binary (e.g. from the store), which cleared away the old HotLoad updates, and gave precedence back to the JS binary in the binary.

2. A [`LocalPackage`](#localpackage) instance which represents the metadata for the currently running HotLoad update.

Example Usage: 

```javascript
hotLoad.getCurrentPackage()
.then((update) => {
    // If the current app "session" represents the first time
    // this update has run, and it had a description provided
    // with it upon release, let's show it to the end user
    if (update.isFirstRun && update.description) {
        // Display a "what's new?" modal
    }
});
```

#### hotLoad.notifyApplicationReady

```javascript
hotLoad.notifyApplicationReady(): Promise<void>;
```

Notifies the HotLoad runtime that a freshly installed update should be considered successful, and therefore, an automatic client-side rollback isn't necessary. It is mandatory to call this function somewhere in the code of the updated bundle. Otherwise, when the app next restarts, the HotLoad runtime will assume that the installed update has failed and roll back to the previous version. This behavior exists to help ensure that your end users aren't blocked by a broken update.

If you are using the `sync` function, and doing your update check on app start, then you don't need to manually call `notifyApplicationReady` since `sync` will call it for you. This behavior exists due to the assumption that the point at which `sync` is called in your app represents a good approximation of a successful startup.

#### hotLoad.restartApp		
		
```javascript		
hotLoad.restartApp(onlyIfUpdateIsPending: Boolean = false): void;		
```		
		
Immediately restarts the app. If a truthy value is provided to the `onlyIfUpdateIsPending` parameter, then the app will only restart if there is actually a pending update waiting to be applied.

This method is for advanced scenarios, and is primarily useful when the following conditions are true:		
		
1. Your app is specifying an install mode value of `ON_NEXT_RESTART` or `ON_NEXT_RESUME` when calling the `sync` or `LocalPackage.install` methods. This has the effect of not applying your update until the app has been restarted (by either the end user or OS)	or resumed, and therefore, the update won't be immediately displayed to the end user.

2. You have an app-specific user event (e.g. the end user navigated back to the app's home route) that allows you to apply the update in an unobtrusive way, and potentially gets the update in front of the end user sooner then waiting until the next restart or resume.		

#### hotLoad.sync

```javascript
hotLoad.sync(options: Object, syncStatusChangeCallback: function(syncStatus: Number), downloadProgressCallback: function(progress: DownloadProgress)): Promise<Number>;
```

Synchronizes your app's JavaScript bundle and image assets with the latest release to the configured deployment. Unlike the [checkForUpdate](#hotloadcheckforupdate) method, which simply checks for the presence of an update, and let's you control what to do next, `sync` handles the update check, download and installation experience for you.

This method provides support for two different (but customizable) "modes" to easily enable apps with different requirements:

1. **Silent mode** *(the default behavior)*, which automatically downloads available updates, and applies them the next time the app restarts (e.g. the OS or end user killed it, or the device was restarted). This way, the entire update experience is "silent" to the end user, since they don't see any update prompt and/or "synthetic" app restarts.

2. **Active mode**, which when an update is available, prompts the end user for permission before downloading it, and then immediately applies the update. If an update was released using the `mandatory` flag, the end user would still be notified about the update, but they wouldn't have the choice to ignore it.


Example Usage: 

```javascript
// Fully silent update which keeps the app in
// sync with the server, without ever 
// interrupting the end user
hotLoad.sync();

// Active update, which lets the end user know
// about each update, and displays it to them
// immediately after downloading it
hotLoad.sync({ updateDialog: true, installMode: hotLoad.InstallMode.IMMEDIATE });
```

*Note: If you want to decide whether you check and/or download an available update based on the end user's device battery level, network conditions, etc. then simply wrap the call to `sync` in a condition that ensures you only call it when desired.*

While the `sync` method tries to make it easy to perform silent and active updates with little configuration, it accepts an "options" object that allows you to customize numerous aspects of the default behavior mentioned above:

* __deploymentKey__ *(String)* - Specifies the deployment key you want to query for an update against. By default, this value is derived from the `Info.plist` file (iOS) and `MainActivity.java` file (Android), but this option allows you to override it from the script-side if you need to dynamically use a different deployment for a specific call to `sync`.

* __installMode__ *(hotLoad.InstallMode)* - Specifies when you would like to install optional updates (i.e. those that aren't marked as mandatory). Defaults to `hotLoad.InstallMode.ON_NEXT_RESTART`. Refer to the [`InstallMode`](#installmode) enum reference for a description of the available options and what they do.

* __mandatoryInstallMode__ *(hotLoad.InstallMode)* - Specifies when you would like to install updates which are marked as mandatory. Defaults to `hotLoad.InstallMode.IMMEDIATE`. Refer to the [`InstallMode`](#installmode) enum reference for a description of the available options and what they do.

* __minimumBackgroundDuration__ *(Number)* - Specifies the minimum number of seconds that the app needs to have been in the background before restarting the app. This property only applies to updates which are installed using `InstallMode.ON_NEXT_RESUME`, and can be useful for getting your update in front of end users sooner, without being too obtrusive. Defaults to `0`, which has the effect of applying the update immediately after a resume, regardless how long it was in the background.
 
* __updateDialog__ *(UpdateDialogOptions)* - An "options" object used to determine whether a confirmation dialog should be displayed to the end user when an update is available, and if so, what strings to use. Defaults to `null`, which has the effect of disabling the dialog completely. Setting this to any truthy value will enable the dialog with the default strings, and passing an object to this parameter allows enabling the dialog as well as overriding one or more of the default strings. Before enabling this option within an App Store-distributed app, please refer to [this note](#user-content-apple-note).

    The following list represents the available options and their defaults:
    
    * __appendReleaseDescription__ *(Boolean)* - Indicates whether you would like to append the description of an available release to the notification message which is displayed to the end user. Defaults to `false`.
    
    * __descriptionPrefix__ *(String)* - Indicates the string you would like to prefix the release description with, if any, when displaying the update notification to the end user. Defaults to `" Description: "`
    
    * __mandatoryContinueButtonLabel__ *(String)* - The text to use for the button the end user must press in order to install a mandatory update. Defaults to `"Continue"`.
    
    * __mandatoryUpdateMessage__ *(String)* - The text used as the body of an update notification, when the update is specified as mandatory. Defaults to `"An update is available that must be installed."`.
    
    * __optionalIgnoreButtonLabel__ *(String)* - The text to use for the button the end user can press in order to ignore an optional update that is available. Defaults to `"Ignore"`.
    
    * __optionalInstallButtonLabel__ *(String)* - The text to use for the button the end user can press in order to install an optional update. Defaults to `"Install"`.
    
    * __optionalUpdateMessage__ *(String)* - The text used as the body of an update notification, when the update is optional. Defaults to `"An update is available. Would you like to install it?"`.
    
    * __title__ *(String)* - The text used as the header of an update notification that is displayed to the end user. Defaults to `"Update available"`.

Example Usage:

```javascript
// Use a different deployment key for this
// specific call, instead of the one configured
// in the Info.plist file
hotLoad.sync({ deploymentKey: "KEY" });

// Download the update silently, but install it on
// the next resume, as long as at least 5 minutes
// has passed since the app was put into the background.
hotLoad.sync({ installMode: hotLoad.InstallMode.ON_NEXT_RESUME, minimumBackgroundDuration: 60 * 5 });

// Download the update silently, and install optional updates
// on the next restart, but install mandatory updates on the next resume.
hotLoad.sync({ mandatoryInstallMode: hotLoad.InstallMode.ON_NEXT_RESUME });

// Changing the title displayed in the
// confirmation dialog of an "active" update
hotLoad.sync({ updateDialog: { title: "An update is available!" } });

// Displaying an update prompt which includes the
// description associated with the HotLoad release
hotLoad.sync({
   updateDialog: {
    appendReleaseDescription: true,
    descriptionPrefix: "\n\nChange log:\n"   
   },
   installMode: hotLoad.InstallMode.IMMEDIATE
});
```

In addition to the options, the `sync` method also accepts two optional function parameters which allow you to subscribe to the lifecycle of the `sync` "pipeline" in order to display additional UI as needed (e.g. a "checking for update modal or a download progress modal):

* __syncStatusChangedCallback__ *((syncStatus: Number) => void)* - Called when the sync process moves from one stage to another in the overall update process. The method is called with a status code which represents the current state, and can be any of the [`SyncStatus`](#syncstatus) values.

* __downloadProgressCallback__ *((progress: DownloadProgress) => void)* - Called periodically when an available update is being downloaded from the HotLoad server. The method is called with a `DownloadProgress` object, which contains the following two properties:
    * __totalBytes__ *(Number)* - The total number of bytes expected to be received for this update package
    * __receivedBytes__ *(Number)* - The number of bytes downloaded thus far.

Example Usage:

```javascript
// Prompt the user when an update is available
// and then display a "downloading" modal 
hotLoad.sync({ updateDialog: true }, (status) => {
    switch (status) {
        case hotLoad.SyncStatus.DOWNLOADING_PACKAGE:
            // Show "downloading" modal
            break;
        case hotLoad.SyncStatus.INSTALLING_UPDATE:
            // Hide "downloading" modal
            break;
    }
});
```

This method returns a `Promise` which is resolved to a `SyncStatus` code that indicates why the `sync` call succeeded. This code can be one of the following `SyncStatus` values:

* __hotLoad.SyncStatus.UP_TO_DATE__ *(4)* - The app is up-to-date with the HotLoad server.

* __hotLoad.SyncStatus.UPDATE_IGNORED__ *(5)* - The app had an optional update which the end user chose to ignore. (This is only applicable when the `updateDialog` is used)

* __hotLoad.SyncStatus.UPDATE_INSTALLED__ *(6)* - The update has been installed and will be run either immediately after the `syncStatusChangedCallback` function returns or the next time the app resumes/restarts, depending on the `InstallMode` specified in `SyncOptions`.

* __hotLoad.SyncStatus.SYNC_IN_PROGRESS__ *(7)* - There is an ongoing `sync` operation running which prevents the current call from being executed.

The `sync` method can be called anywhere you'd like to check for an update. That could be in the `componentWillMount` lifecycle event of your root component, the onPress handler of a `<TouchableHighlight>` component, in the callback of a periodic timer, or whatever else makes sense for your needs. Just like the `checkForUpdate` method, it will perform the network request to check for an update in the background, so it won't impact your UI thread and/or JavaScript thread's responsiveness.

#### Package objects

The `checkForUpdate` and `getCurrentPackage` methods return promises, that when resolved, provide acces to "package" objects. The package represents your code update as well as any extra metadata (e.g. description, mandatory?). The HotLoad API has the distinction between the following types of packages:

* [LocalPackage](#localpackage): Represents a downloaded update that is either already running, or has been installed and is pending an app restart.

* [RemotePackage](#remotepackage): Represents an available update on the HotLoad server that hasn't been downloaded yet.

##### LocalPackage

Contains details about an update that has been downloaded locally or already installed. You can get a reference to an instance of this object either by calling the module-level `getCurrentPackage` method, or as the value of the promise returned by the `RemotePackage.download` method.

###### Properties
- __appVersion__: The app binary version that this update is dependent on. This is the value that was specified via the `appStoreVersion` parameter when calling the CLI's `release` command. *(String)*
- __deploymentKey__: The deployment key that was used to originally download this update. *(String)*
- __description__: The description of the update. This is the same value that you specified in the CLI when you released the update. *(String)*
- __failedInstall__: Indicates whether this update has been previously installed but was rolled back. The `sync` method will automatically ignore updates which have previously failed, so you only need to worry about this property if using `checkForUpdate`. *(Boolean)*
- __isFirstRun__: Indicates whether this is the first time the update has been run after being installed. This is useful for determining whether you would like to show a "What's New?" UI to the end user after installing an update. *(Boolean)*
- __isMandatory__: Indicates whether the update is considered mandatory.  This is the value that was specified in the CLI when the update was released. *(Boolean)*
- __isPending__: Indicates whether this update is in a "pending" state. When `true`, that means the update has been downloaded and installed, but the app restart needed to apply it hasn't occurred yet, and therefore, it's changes aren't currently visible to the end-user. *(Boolean)*
- __label__: The internal label automatically given to the update by the HotLoad server. This value uniquely identifies the update within it's deployment. *(String)*
- __packageHash__: The SHA hash value of the update. *(String)*
- __packageSize__: The size of the code contained within the update, in bytes. *(Number)*

###### Methods
- __install(installMode: hotLoad.InstallMode = hotLoad.InstallMode.ON_NEXT_RESTART, minimumBackgroundDuration = 0): Promise&lt;void&gt;__: Installs the update by saving it to the location on disk where the runtime expects to find the latest version of the app. The `installMode` parameter controls when the changes are actually presented to the end user. The default value is to wait until the next app restart to display the changes, but you can refer to the [`InstallMode`](#installmode) enum reference for a description of the available options and what they do. If the `installMode` parameter is set to `InstallMode.ON_NEXT_RESUME`, then the `minimumBackgroundDuration` parameter allows you to control how long the app must have been in the background before forcing the install after it is resumed. 

##### RemotePackage

Contains details about an update that is available for download from the HotLoad server. You get a reference to an instance of this object by calling the `checkForUpdate` method when an update is available. If you are using the `sync` API, you don't need to worry about the `RemotePackage`, since it will handle the download and installation process automatically for you.

###### Properties

The `RemotePackage` inherits all of the same properties as the `LocalPackage`, but includes one additional one:

- __downloadUrl__: The URL at which the package is available for download. This property is only needed for advanced usage, since the `download` method will automatically handle the acquisition of updates for you. *(String)*

###### Methods
- __download(downloadProgressCallback?: Function): Promise&lt;LocalPackage&gt;__: Downloads the available update from the HotLoad service. If a `downloadProgressCallback` is specified, it will be called periodically with a `DownloadProgress` object (`{ totalBytes: Number, receivedBytes: Number }`) that reports the progress of the download until it completes. Returns a Promise that resolves with the `LocalPackage`.

#### Enums

The HotLoad API includes the following enums which can be used to customize the update experience:

##### InstallMode

This enum specified when you would like an installed update to actually be applied, and can be passed to either the `sync` or `LocalPackage.install` methods. It includes the following values:

* __hotLoad.InstallMode.IMMEDIATE__ *(0)* - Indicates that you want to install the update and restart the app immediately. This value is appropriate for debugging scenarios as well as when displaying an update prompt to the user, since they would expect to see the changes immediately after accepting the installation. Additionally, this mode can be used to enforce mandatory updates, since it removes the potentially undesired latency between the update installation and the next time the end user restarts or resumes the app.

* __hotLoad.InstallMode.ON_NEXT_RESTART__ *(1)* - Indicates that you want to install the update, but not forcibly restart the app. When the app is "naturally" restarted (due the OS or end user killing it), the update will be seamlessly picked up. This value is appropriate when performing silent updates, since it would likely be disruptive to the end user if the app suddenly restarted out of nowhere, since they wouldn't have realized an update was even downloaded. This is the default mode used for both the `sync` and `LocalPackage.install` methods.
 
* __hotLoad.InstallMode.ON_NEXT_RESUME__ *(2)* - Indicates that you want to install the update, but don't want to restart the app until the next time the end user resumes it from the background. This way, you don't disrupt their current session, but you can get the update in front of them sooner then having to wait for the next natural restart. This value is appropriate for silent installs that can be applied on resume in a non-invasive way.

##### SyncStatus

This enum is provided to the `syncStatusChangedCallback` function that can be passed to the `sync` method, in order to hook into the overall update process. It includes the following values:

* __hotLoad.SyncStatus.CHECKING_FOR_UPDATE__ *(0)* - The HotLoad server is being queried for an update.
* __hotLoad.SyncStatus.AWAITING_USER_ACTION__ *(1)* - An update is available, and a confirmation dialog was shown to the end user. (This is only applicable when the `updateDialog` is used)
* __hotLoad.SyncStatus.DOWNLOADING_PACKAGE__ *(2)* - An available update is being downloaded from the HotLoad server.
* __hotLoad.SyncStatus.INSTALLING_UPDATE__ *(3)* - An available update was downloaded and is about to be installed.
* __hotLoad.SyncStatus.UP_TO_DATE__ *(4)* - The app is fully up-to-date with the configured deployment.
* __hotLoad.SyncStatus.UPDATE_IGNORED__ *(5)* - The app has an optional update, which the end user chose to ignore. (This is only applicable when the `updateDialog` is used)
* __hotLoad.SyncStatus.UPDATE_INSTALLED__ *(6)* - An available update has been installed and will be run either immediately after the `syncStatusChangedCallback` function returns or the next time the app resumes/restarts, depending on the `InstallMode` specified in `SyncOptions`.
* __hotLoad.SyncStatus.SYNC_IN_PROGRESS__ *(7)* - There is an ongoing `sync` operation running which prevents the current call from being executed.
* __hotLoad.SyncStatus.UNKNOWN_ERROR__ *(-1)* - The sync operation encountered an unknown error. 

### Objective-C API Reference (iOS)

The Objective-C API is made available by importing the `HotLoad.h` header into your `AppDelegate.m` file, and consists of a single public class named `HotLoad`.

#### HotLoad

Contains static methods for retreiving the `NSURL` that represents the most recent JavaScript bundle file, and can be passed to the `RCTRootView`'s `initWithBundleURL` method when bootstrapping your app in the `AppDelegate.m` file. 

The `HotLoad` class' methods can be thought of as composite resolvers which always load the appropriate bundle, in order to accomodate the following scenarios:

1. When an end-user installs your app from the store (e.g. `1.0.0`), they will get the JS bundle that is contained within the binary. This is the behavior you would get without using HotLoad, but we make sure it doesn't break :)

2. As soon as you begin releasing HotLoad updates, your end-users will get the JS bundle that represents the latest release for the configured deployment. This is the behavior that allows you to iterate beyond what you shipped to the store.

3. As soon as you release an update to the app store (e.g. `1.1.0`), and your end-users update it, they will once again get the JS bundle that is contained within the binary. This behavior ensures that HotLoad updates that targetted a previous app store version aren't used (since we don't know it they would work), and your end-users always have a working version of your app.

4. Repeat #2 and #3 as the HotLoad releases and app store releases continue on into infinity (and beyond?)

Because of this behavior, you can safely deploy updates to both the app store(s) and HotLoad as neccesary, and rest assured that your end-users will always get the most recent version.  

##### Methods

- __(NSURL \*)bundleURL__ - Returns the most recent JS bundle `NSURL` as described above. This method assumes that the name of the JS bundle contained within your app binary is `main.jsbundle`.

- __(NSURL \*)bundleURLForResource:(NSString \*)resourceName__ - Equivalent to the `bundleURL` method, but also allows customizing the name of the JS bundle that is looked for within the app binary. This is useful if you aren't naming this file `main` (which is the default convention). This method assumes that the JS bundle's extension is `*.jsbundle`.

- __(NSURL \*)bundleURLForResource:(NSString \*)resourceName withExtension:(NSString \*)resourceExtension__: Equivalent to the `bundleURLForResource:` method, but also allows customizing the extension used by the JS bundle that is looked for within the app binary. This is useful if you aren't naming this file `*.jsbundle` (which is the default convention).

- __(void)setDeploymentKey:(NSString \*)deploymentKey__ - Sets the deployment key that the app should use when querying for updates. This is a dynamic alternative to setting the deployment key in your `Info.plist` and/or specifying a deployment key in JS when calling `checkForUpdate` or `sync`.

### Java API Reference (Android)

The Java API is made available by importing the `com.microsoft.hotload.react.HotLoad` class into your `MainActivity.java` file, and consists of a single public class named `HotLoad`.

#### HotLoad

Constructs the HotLoad client runtime and includes methods for integrating HotLoad into your app's `ReactInstanceManager`.

##### Constructors

- __HotLoad(String deploymentKey, Activity mainActivity)__ - Creates a new instance of the HotLoad runtime, that will be used to query the service for updates via the provided deployment key. The `mainActivity` parameter should always be set to `this` when configuring your `ReactInstanceManager` inside the `MainActivity` class. This constructor puts the HotLoad runtime into "release mode", so if you want to enable debugging behavior, use the following constructor instead.

- __HotLoad(String deploymentKey, Activity mainActivity, bool isDebugMode)__ - Equivalent to the previous constructor, but allows you to specify whether you want the HotLoad runtime to be in debug mode or not. When using this constructor, the `isDebugMode` parameter should always be set to `BuildConfig.DEBUG` in order to stay synchronized with your build type. When putting HotLoad into debug mode, the following behaviors are enabled:

    1. Old HotLoad updates aren't deleted from storage whenever a new binary is deployed to the emulator/device. This behavior enables you to deploy new binaries, without bumping the version during development, and without continuously getting the same update every time your app calls `sync`.
    
    2. The local cache that the React Native runtime maintains in debug mode is deleted whenever a HotLoad update is installed. This ensures that when the app is restarted after an update is applied, you will see the expected changes. As soon as [this PR](https://github.com/facebook/react-native/pull/4738) is merged, we won't need to do this anymore.
    
##### Methods

- __getBundleUrl(String bundleName)__ - Returns the path to the most recent version of your app's JS bundle file, using the specified resource name (e.g. `index.android.bundle`). This method has the same resolution behavior as the Objective-C equivalent described above.

- __getReactPackage()__ - Returns a `ReactPackage` object that should be added to your `ReactInstanceManager` via its `addPackage` method. Without this, the `react-native-code-push` JS module won't be available to your script.

## Example Apps

The React Native community has graciously created some awesome open source apps that can serve as examples for developers that are getting started. The following is a list of OSS React Native apps that are also using HotLoad, and can therefore be used to see how others are using the service:

* [Feline for Product Hunt](https://github.com/arjunkomath/Feline-for-Product-Hunt) - An Android client for Product Hunt.
* [GeoEncoding](https://github.com/LynxITDigital/GeoEncoding) - An app by [Lynx IT Digital](https://digital.lynxit.com.au) which demonstrates how to use numerous React Native components and modules.
* [Math Facts](https://github.com/Khan/math-facts) - An app by Khan Academy to help memorize math facts more easily.
* [MoveIt!](https://github.com/multunus/moveit-react-native) - An app by [Multunus](http://www.multunus.com) that allows employees within a company to track their work-outs.

*Note: If you've developed a React Native app using HotLoad, that is also open-source, please let us know. We would love to add it to this list!*

## Debugging / Troubleshooting

The `sync` method includes a lot of diagnostic logging out-of-the-box, so if you're encountering an issue when using it, the best thing to try first is examining the output logs of your app. This will tell you whether the app is configured correctly (e.g. can the plugin find your deployment key?), if the app is able to reach the server, if an available update is being discovered, if the update is being successfully downloaded/installed, etc. We want to continue improving the logging to be as intuitive/comprehensive as possible, so please [let us know](mailto:hotloadfeed@microsoft.com) if you find it to be confusing or missing anything.

![Xcode Console](https://cloud.githubusercontent.com/assets/116461/13536459/d2687bea-e1f4-11e5-9998-b048ca8d201e.png)

To view these logs, you can use either the Chrome DevTools Console, the XCode Console (iOS) and/or ADB logcat (Android). By default, React Native logs are disabled on iOS in release builds, so if you want to view them in a release build, you simply need to make the following changes to your `AppDelegate.m` file:

1. Add an `#import "RCTLog.h"` statement

2. Add the following statement to the top of your `application:didFinishLaunchingWithOptions` method:

    ```objective-c
    RCTSetLogThreshold(RCTLogLevelInfo);
    ```

Now you'll be able to see HotLoad logs in either debug or release mode, on both iOS or Android. If examining the logs don't provide an indication of the issue, please refer to the following common issues for additional resolution ideas:

| Issue / Symptom | Possible Solution |
|-----------------|-------------------|
| Compilation Error | Double-check that your version of React Native is [compatible](#supported-react-native-platforms) with the HotLoad version you are using. |
| Network timeout / hang when calling `sync` or `checkForUpadte` in the iOS Simulator | Try resetting the simulator by selecting the `Simulator -> Reset Content and Settings..` menu item, and then re-running your app. |
| Server responds with a `404` when calling `sync` or `checkForUpdate` | Double-check that the deployment key you added to your `Info.plist` (iOS), `build.gradle` (Android) or that you're passing to `sync`/`checkForUpdate`, is in fact correct. You can run `code-push deployment ls [APP_NAME] -k` to view the correct keys for your app deployments. |
| Update not being discovered | Double-check that the version of your running app (e.g. `1.0.0`) matches the version you specified when releasing the update to HotLoad. Additionally, make sure that you are releasing to the same deployment that your app is configured to sync with. |
| Update not being displayed after restart | If you're not calling `sync` on app start (e.g. within `componentDidMount` of your root component), then you need to explicitly call `notifyApplicationReady` on app start, otherwise, the plugin will think your update failed and roll it back. |
| Images dissappear after installing HotLoad update | If your app is using the React Native assets system to load images (i.e. the `require(./foo.png)` syntax), then you **MUST** release your assets along with your JS bundle to HotLoad. Follow [these instructions](#releasing-updates-javascript--images) to see how to do this. |
| No JS bundle is being found when running your app against the iOS simulator | By default, React Native doesn't generate your JS bundle when running against the simulator. Therefore, if you're using `[HotLoad bundleURL]`, and targetting the iOS simulator, you may be getting a `nil` result. This issue will be fixed in RN 0.22.0, but only for release builds. You can unblock this scenario right now by making [this change](https://github.com/facebook/react-native/commit/9ae3714f4bebdd2bcab4d7fdbf23acebdc5ed2ba) locally.
