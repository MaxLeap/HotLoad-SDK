'use strict';

import React, {
  AppRegistry,
  Dimensions,
  Image,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from "react-native";

import Button from "react-native-button";
import HotLoad from "react-native-hot-load";

let HotLoadDemoApp = React.createClass({
  async sync() {
    let self = this;
    try {
      return await HotLoad.sync(
        {
          updateDialog: true,
          installMode: HotLoad.InstallMode.ON_NEXT_RESUME
        },
        (syncStatus) => {
          switch(syncStatus) {
            case HotLoad.SyncStatus.CHECKING_FOR_UPDATE:
              self.setState({
                syncMessage: "Checking for update."
              });
              break;
            case HotLoad.SyncStatus.DOWNLOADING_PACKAGE:
              self.setState({
                syncMessage: "Downloading package."
              });
              break;
            case HotLoad.SyncStatus.AWAITING_USER_ACTION:
              self.setState({
                syncMessage: "Awaiting user action."
              });
              break;
            case HotLoad.SyncStatus.INSTALLING_UPDATE:
              self.setState({
                syncMessage: "Installing update."
              });
              break;
            case HotLoad.SyncStatus.UP_TO_DATE:
              self.setState({
                syncMessage: "App up to date.",
                progress: false
              });
              break;
            case HotLoad.SyncStatus.UPDATE_IGNORED:
              self.setState({
                syncMessage: "Update cancelled by user.",
                progress: false
              });
              break;
            case HotLoad.SyncStatus.UPDATE_INSTALLED:
              self.setState({
                syncMessage: "Update installed and will be run when the app next resumes.",
                progress: false
              });
              break;
            case HotLoad.SyncStatus.UNKNOWN_ERROR:
              self.setState({
                syncMessage: "An unknown error occurred.",
                progress: false
              });
              break;
          }
        },
        (progress) => {
          self.setState({
            progress: progress
          });
        }
      );
    } catch (error) {
      HotLoad.log(error);
    }
  },

  componentDidMount() {
      HotLoad.notifyApplicationReady();
  },

  getInitialState() {
    return { };
  },

  render() {
    let syncView, syncButton, progressView;

    if (this.state.syncMessage) {
      syncView = (
        <Text style={styles.messages}>{this.state.syncMessage}</Text>
      );
    } else {
      syncButton = (
        <Button style={{color: 'green'}} onPress={this.sync}>
          Start Sync!
        </Button>
      );
    }

    if (this.state.progress) {
      progressView = (
        <Text style={styles.messages}>{this.state.progress.receivedBytes} of {this.state.progress.totalBytes} bytes received</Text>
      );
    }

    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Welcome to HotLoad!
        </Text>
        {syncButton}
        {syncView}
        {progressView}
        <Image
        style={styles.image}
        resizeMode={Image.resizeMode.contain}
        // source={require('./images/laptop_phone_howitworks.png')}
        source={require('./images/free_apps.png')}
        />
      </View>
    );
  }
});

let styles = StyleSheet.create({
  image: {
    marginTop: 50,
    width: Dimensions.get('window').width - 100,
    height: 365 * (Dimensions.get('window').width - 100) / 651,
  },
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
    paddingTop: 50
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10
  },
  messages: {
    textAlign: 'center',
  },
});

AppRegistry.registerComponent('HotLoadDemoApp', () => HotLoadDemoApp);
