
### Setup react-native
```shell
$ npm init
$ npm install --save react react-native
$ curl -o .flowconfig https://raw.githubusercontent.com/facebook/react-native/master/.flowconfig
$ npm install react-native-cli
$ npm install --save stellar-base
```
### Build Debug
###Build Relase
 ```shell
$ ./node_modules/.bin/react-native bundle --platform android --dev false --entry-file index.android.js --bundle-output platform/android/src/main/assets/index.android.bundle --assets-dest platform/android/src/main/res/
```

