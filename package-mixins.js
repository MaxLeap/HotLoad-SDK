import { AcquisitionManager as Sdk } from "hot-load/script/acquisition-sdk";
import { DeviceEventEmitter } from "react-native";

// This function is used to augment remote and local
// package objects with additional functionality/properties
// beyond what is included in the metadata sent by the server.
module.exports = (NativeHotLoad) => {
  const remote = (reportStatusDownload) => {
    return {
      async download(downloadProgressCallback) {
        if (!this.downloadUrl) {
          throw new Error("Cannot download an update without a download url");
        }

        let downloadProgressSubscription;
        if (downloadProgressCallback) {
          // Use event subscription to obtain download progress.
          downloadProgressSubscription = DeviceEventEmitter.addListener(
            "HotLoadDownloadProgress",
            downloadProgressCallback
          );
        }

        // Use the downloaded package info. Native code will save the package info
        // so that the client knows what the current package version is.
        try {
          const downloadedPackage = await NativeHotLoad.downloadUpdate(this);
          reportStatusDownload && reportStatusDownload(this);
          return { ...downloadedPackage, ...local };
        } finally {
          downloadProgressSubscription && downloadProgressSubscription.remove();
        }
      },

      isPending: false // A remote package could never be in a pending state
    };
  };

  const local = {
    async install(installMode = NativeHotLoad.hotLoadInstallModeOnNextRestart, minimumBackgroundDuration = 0, updateInstalledCallback) {
      const localPackage = this;
      await NativeHotLoad.installUpdate(this, installMode, minimumBackgroundDuration);
      updateInstalledCallback && updateInstalledCallback();
      if (installMode == NativeHotLoad.hotLoadInstallModeImmediate) {
        NativeHotLoad.restartApp(false);
      } else {
        localPackage.isPending = true; // Mark the package as pending since it hasn't been applied yet
      }
    },

    isPending: false // A local package wouldn't be pending until it was installed
  };

  return { local, remote };
};
