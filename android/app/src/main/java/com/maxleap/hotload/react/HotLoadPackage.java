package com.maxleap.hotload.react;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

public class HotLoadPackage {

    private final String HOT_LOAD_FOLDER_PREFIX = "HotLoad";
    private final String CURRENT_PACKAGE_KEY = "currentPackage";
    private final String DIFF_MANIFEST_FILE_NAME = "hothotload.json";
    private final int DOWNLOAD_BUFFER_SIZE = 1024 * 256;
    private final String DOWNLOAD_FILE_NAME = "download.zip";
    private final String DOWNLOAD_URL_KEY = "downloadUrl";
    private final String PACKAGE_FILE_NAME = "app.json";
    private final String PACKAGE_HASH_KEY = "packageHash";
    private final String PREVIOUS_PACKAGE_KEY = "previousPackage";
    private final String RELATIVE_BUNDLE_PATH_KEY = "bundlePath";
    private final String STATUS_FILE = "hotload.json";
    private final String UNZIPPED_FOLDER_NAME = "unzipped";
    private final String UPDATE_BUNDLE_FILE_NAME = "app.jsbundle";

    private String documentsDirectory;

    public HotLoadPackage(String documentsDirectory) {
        this.documentsDirectory = documentsDirectory;
    }

    private String getDownloadFilePath() {
        return HotLoadUtils.appendPathComponent(getHotLoadPath(), DOWNLOAD_FILE_NAME);
    }

    private String getUnzippedFolderPath() {
        return HotLoadUtils.appendPathComponent(getHotLoadPath(), UNZIPPED_FOLDER_NAME);
    }

    private String getDocumentsDirectory() {
        return documentsDirectory;
    }

    private String getHotLoadPath() {
        String hotLoadPath = HotLoadUtils.appendPathComponent(getDocumentsDirectory(), HOT_LOAD_FOLDER_PREFIX);
        if (HotLoad.isUsingTestConfiguration()) {
            hotLoadPath = HotLoadUtils.appendPathComponent(hotLoadPath, "TestPackages");
        }

        return hotLoadPath;
    }

    private String getStatusFilePath() {
        return HotLoadUtils.appendPathComponent(getHotLoadPath(), STATUS_FILE);
    }

    public WritableMap getCurrentPackageInfo() {
        String statusFilePath = getStatusFilePath();
        if (!FileUtils.fileAtPathExists(statusFilePath)) {
            return new WritableNativeMap();
        }

        try {
            return HotLoadUtils.getWritableMapFromFile(statusFilePath);
        } catch (IOException e) {
            throw new HotLoadUnknownException("Error getting current package info", e);
        }
    }

    public void updateCurrentPackageInfo(ReadableMap packageInfo) {
        try {
            HotLoadUtils.writeReadableMapToFile(packageInfo, getStatusFilePath());
        } catch (IOException e) {
            throw new HotLoadUnknownException("Error updating current package info", e);
        }
    }

    public String getCurrentPackageFolderPath() {
        WritableMap info = getCurrentPackageInfo();
        String packageHash = HotLoadUtils.tryGetString(info, CURRENT_PACKAGE_KEY);
        if (packageHash == null) {
            return null;
        }

        return getPackageFolderPath(packageHash);
    }

    public String getCurrentPackageBundlePath() {
        String packageFolder = getCurrentPackageFolderPath();
        if (packageFolder == null) {
            return null;
        }

        WritableMap currentPackage = getCurrentPackage();
        String relativeBundlePath = HotLoadUtils.tryGetString(currentPackage, RELATIVE_BUNDLE_PATH_KEY);
        if (relativeBundlePath == null) {
            return HotLoadUtils.appendPathComponent(packageFolder, UPDATE_BUNDLE_FILE_NAME);
        } else {
            return HotLoadUtils.appendPathComponent(packageFolder, relativeBundlePath);
        }
    }

    public String getPackageFolderPath(String packageHash) {
        return HotLoadUtils.appendPathComponent(getHotLoadPath(), packageHash);
    }

    public String getCurrentPackageHash() {
        WritableMap info = getCurrentPackageInfo();
        return HotLoadUtils.tryGetString(info, CURRENT_PACKAGE_KEY);
    }

    public String getPreviousPackageHash() {
        WritableMap info = getCurrentPackageInfo();
        return HotLoadUtils.tryGetString(info, PREVIOUS_PACKAGE_KEY);
    }

    public WritableMap getCurrentPackage() {
        String folderPath = getCurrentPackageFolderPath();
        if (folderPath == null) {
            return null;
        }

        String packagePath = HotLoadUtils.appendPathComponent(folderPath, PACKAGE_FILE_NAME);
        try {
            return HotLoadUtils.getWritableMapFromFile(packagePath);
        } catch (IOException e) {
            // Should not happen unless the update metadata was somehow deleted.
            return null;
        }
    }

    public WritableMap getPackage(String packageHash) {
        String folderPath = getPackageFolderPath(packageHash);
        String packageFilePath = HotLoadUtils.appendPathComponent(folderPath, PACKAGE_FILE_NAME);
        try {
            return HotLoadUtils.getWritableMapFromFile(packageFilePath);
        } catch (IOException e) {
            return null;
        }
    }

    public void downloadPackage(ReadableMap updatePackage, String expectedBundleFileName,
                                DownloadProgressCallback progressCallback) throws IOException {
        String newUpdateHash = HotLoadUtils.tryGetString(updatePackage, PACKAGE_HASH_KEY);
        String newUpdateFolderPath = getPackageFolderPath(newUpdateHash);
        String newUpdateMetadataPath = HotLoadUtils.appendPathComponent(newUpdateFolderPath, PACKAGE_FILE_NAME);
        if (FileUtils.fileAtPathExists(newUpdateFolderPath)) {
            // This removes any stale data in newPackageFolderPath that could have been left
            // uncleared due to a crash or error during the download or install process.
            FileUtils.deleteDirectoryAtPath(newUpdateFolderPath);
        }

        String downloadUrlString = HotLoadUtils.tryGetString(updatePackage, DOWNLOAD_URL_KEY);
        HttpURLConnection connection = null;
        BufferedInputStream bin = null;
        FileOutputStream fos = null;
        BufferedOutputStream bout = null;
        File downloadFile = null;
        boolean isZip = false;

        // Download the file while checking if it is a zip and notifying client of progress.
        try {
            URL downloadUrl = new URL(downloadUrlString);
            connection = (HttpURLConnection) (downloadUrl.openConnection());

            long totalBytes = connection.getContentLength();
            long receivedBytes = 0;

            bin = new BufferedInputStream(connection.getInputStream());
            File downloadFolder = new File(getHotLoadPath());
            downloadFolder.mkdirs();
            downloadFile = new File(downloadFolder, DOWNLOAD_FILE_NAME);
            fos = new FileOutputStream(downloadFile);
            bout = new BufferedOutputStream(fos, DOWNLOAD_BUFFER_SIZE);
            byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
            byte[] header = new byte[4];

            int numBytesRead = 0;
            while ((numBytesRead = bin.read(data, 0, DOWNLOAD_BUFFER_SIZE)) >= 0) {
                if (receivedBytes < 4) {
                    for (int i = 0; i < numBytesRead; i++) {
                        int headerOffset = (int) (receivedBytes) + i;
                        if (headerOffset >= 4) {
                            break;
                        }

                        header[headerOffset] = data[i];
                    }
                }

                receivedBytes += numBytesRead;
                bout.write(data, 0, numBytesRead);
                progressCallback.call(new DownloadProgress(totalBytes, receivedBytes));
            }

            if (totalBytes != receivedBytes) {
                throw new HotLoadUnknownException("Received " + receivedBytes + " bytes, expected " + totalBytes);
            }

            isZip = ByteBuffer.wrap(header).getInt() == 0x504b0304;
        } catch (MalformedURLException e) {
            throw new HotLoadMalformedDataException(downloadUrlString, e);
        } finally {
            try {
                if (bout != null) bout.close();
                if (fos != null) fos.close();
                if (bin != null) bin.close();
                if (connection != null) connection.disconnect();
            } catch (IOException e) {
                throw new HotLoadUnknownException("Error closing IO resources.", e);
            }
        }

        if (isZip) {
            // Unzip the downloaded file and then delete the zip
            String unzippedFolderPath = getUnzippedFolderPath();
            FileUtils.unzipFile(downloadFile, unzippedFolderPath);
            FileUtils.deleteFileOrFolderSilently(downloadFile);

            // Merge contents with current update based on the manifest
            String diffManifestFilePath = HotLoadUtils.appendPathComponent(unzippedFolderPath,
                    DIFF_MANIFEST_FILE_NAME);
            boolean isDiffUpdate = FileUtils.fileAtPathExists(diffManifestFilePath);
            if (isDiffUpdate) {
                String currentPackageFolderPath = getCurrentPackageFolderPath();
                HotLoadUpdateUtils.copyNecessaryFilesFromCurrentPackage(diffManifestFilePath, currentPackageFolderPath, newUpdateFolderPath);
                File diffManifestFile = new File(diffManifestFilePath);
                diffManifestFile.delete();
            }

            FileUtils.copyDirectoryContents(unzippedFolderPath, newUpdateFolderPath);
            FileUtils.deleteFileAtPathSilently(unzippedFolderPath);

            // For zip updates, we need to find the relative path to the jsBundle and save it in the
            // metadata so that we can find and run it easily the next time.
            String relativeBundlePath = HotLoadUpdateUtils.findJSBundleInUpdateContents(newUpdateFolderPath, expectedBundleFileName);

            if (relativeBundlePath == null) {
                throw new HotLoadInvalidUpdateException("Update is invalid - no files with extension .bundle, .js or .jsbundle were found in the update package.");
            } else {
                if (FileUtils.fileAtPathExists(newUpdateMetadataPath)) {
                    File metadataFileFromOldUpdate = new File(newUpdateMetadataPath);
                    metadataFileFromOldUpdate.delete();
                }

                if (isDiffUpdate) {
                    HotLoadUpdateUtils.verifyHashForDiffUpdate(newUpdateFolderPath, newUpdateHash);
                }

                JSONObject updatePackageJSON = HotLoadUtils.convertReadableToJsonObject(updatePackage);
                try {
                    updatePackageJSON.put(RELATIVE_BUNDLE_PATH_KEY, relativeBundlePath);
                } catch (JSONException e) {
                    throw new HotLoadUnknownException("Unable to set key " +
                            RELATIVE_BUNDLE_PATH_KEY + " to value " + relativeBundlePath +
                            " in update package.", e);
                }

                updatePackage = HotLoadUtils.convertJsonObjectToWritable(updatePackageJSON);
            }
        } else {
            // File is a jsbundle, move it to a folder with the packageHash as its name
            FileUtils.moveFile(downloadFile, newUpdateFolderPath, UPDATE_BUNDLE_FILE_NAME);
        }

        // Save metadata to the folder.
        HotLoadUtils.writeReadableMapToFile(updatePackage, newUpdateMetadataPath);
    }

    public void installPackage(ReadableMap updatePackage, boolean removePendingUpdate) {
        String packageHash = HotLoadUtils.tryGetString(updatePackage, PACKAGE_HASH_KEY);
        WritableMap info = getCurrentPackageInfo();
        if (removePendingUpdate) {
            String currentPackageFolderPath = getCurrentPackageFolderPath();
            if (currentPackageFolderPath != null) {
                FileUtils.deleteDirectoryAtPath(currentPackageFolderPath);
            }
        } else {
            String previousPackageHash = getPreviousPackageHash();
            if (previousPackageHash != null && !previousPackageHash.equals(packageHash)) {
                FileUtils.deleteDirectoryAtPath(getPackageFolderPath(previousPackageHash));
            }

            info.putString(PREVIOUS_PACKAGE_KEY, HotLoadUtils.tryGetString(info, CURRENT_PACKAGE_KEY));
        }

        info.putString(CURRENT_PACKAGE_KEY, packageHash);
        updateCurrentPackageInfo(info);
    }

    public void rollbackPackage() {
        WritableMap info = getCurrentPackageInfo();
        String currentPackageFolderPath = getCurrentPackageFolderPath();
        FileUtils.deleteDirectoryAtPath(currentPackageFolderPath);
        info.putString(CURRENT_PACKAGE_KEY, HotLoadUtils.tryGetString(info, PREVIOUS_PACKAGE_KEY));
        info.putNull(PREVIOUS_PACKAGE_KEY);
        updateCurrentPackageInfo(info);
    }

    public void downloadAndReplaceCurrentBundle(String remoteBundleUrl) throws IOException {
        URL downloadUrl;
        HttpURLConnection connection = null;
        BufferedInputStream bin = null;
        FileOutputStream fos = null;
        BufferedOutputStream bout = null;
        try {
            downloadUrl = new URL(remoteBundleUrl);
            connection = (HttpURLConnection) (downloadUrl.openConnection());
            bin = new BufferedInputStream(connection.getInputStream());
            File downloadFile = new File(getCurrentPackageBundlePath());
            downloadFile.delete();
            fos = new FileOutputStream(downloadFile);
            bout = new BufferedOutputStream(fos, DOWNLOAD_BUFFER_SIZE);
            byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
            int numBytesRead = 0;
            while ((numBytesRead = bin.read(data, 0, DOWNLOAD_BUFFER_SIZE)) >= 0) {
                bout.write(data, 0, numBytesRead);
            }
        } catch (MalformedURLException e) {
            throw new HotLoadMalformedDataException(remoteBundleUrl, e);
        } finally {
            try {
                if (bout != null) bout.close();
                if (fos != null) fos.close();
                if (bin != null) bin.close();
                if (connection != null) connection.disconnect();
            } catch (IOException e) {
                throw new HotLoadUnknownException("Error closing IO resources.", e);
            }
        }
    }

    public void clearUpdates() {
        File statusFile = new File(getStatusFilePath());
        statusFile.delete();
        FileUtils.deleteDirectoryAtPath(getHotLoadPath());
    }
}
