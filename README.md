# Building
Prior to building, ensure you have a `local.properties` file at the root of the directory that contains the following...

    sdk.dir=/path/to/android/sdk

or, have a `ANDROID_HOME` enviornment variable set to the path of your Android SDK.

Finally...

build APK with Gradle...

    ./gradlew assembleDebug

install with Gradle (note: installDebug will also run assembleDebug)...

    ./gradlew installDebug

or, to view all Gradle tasks...

    ./gradlew tasks

Any generated artifacts such as APKs can be found in the `app/build/` directory.
