# SUDOQ

![build with gradle assemble](https://github.com/timo-a/SudoQ/actions/workflows/gradle_assemble.yml/badge.svg)


ANDROID STUFF

All you need you get over here http://developer.android.com/index.html
The SDK -> http://developer.android.com/sdk/index.html

SETUP

Import with Android-Studio should go seamlessly.

To run the app on the emulator or a phone you have to … 
… compile to the target SDK (Not min SDK) otherwise you'll get compile errors.
… add the sudoq-model to your sudoq-app project's buildpath in case eclipse got that wrong.
… create an assets folder called assets within the sudoq-app project and put the  Sudokus folder which contains pre-generated Sudokus in it.

WORKING THE CODE

Some advise:

Keep any android specific code away from the model.
In case you got stuck: http://xkcd.com/ or http://stackoverflow.com/questions/tagged/android

That should be all. In case of emergencies Don't Panic, and call one of the devs.

And don't forget your towel!



