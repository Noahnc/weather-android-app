# weather-android-app
a simple android client to check for the current temperature at Zürich 
Tiefenbrunnen and send local push notifications above a predefined threshold

# how to use
1. open the project at its root with android studio
2. run application on an emulator or physical device
3. (optional: on android 13: allow push notifications with the permission dialog)
4. enter a temperature threshold with the input dialog
5. start and stop foreground service using the in-app buttons

# disclaimer
as discussed on 26.09, we are not comparing the temperature difference for better testability. 
instead, we are comparing the user input with the fetched temperature and display a push notification
if there is any difference between these two.

# credits
programmed by milan, noah and serafin
