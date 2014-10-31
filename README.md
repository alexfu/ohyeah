# Building
Prior to building, ensure you have a `local.properties` file at the root of the directory that contains the following...

    sdk.dir=/path/to/android/sdk

or, have a `ANDROID_HOME` enviornment variable set to the path of your Android SDK.

Finally...

build with Gradle...

    ./gradlew assembleDebug

install with Gradle...

    ./gradlew installDebug

or, to view all Gradle tasks...

    ./gradlew tasks
