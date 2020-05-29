# Vehicle-Breakdown-Mechanic-Finder
An Android Application to find the nearby Mechanics 

P.S: If you're going to download the full project please use your own firebase API, in this project API will NOT be mantained and the app may not work.

### Implementation Guide

1 - Project 1 - Open the Project in your android studio; 2 - !!!!IMPORTANT!!!! Change the Package Name. You can check how to do that here (https://stackoverflow.com/questions/16804093/android-studio-rename-package)

2 - Firebase Panel 1 - Create Firebase Project (https://console.firebase.google.com/); 2 - import the file google-service.json into your project as the instructions say; 3 - Change Pay Plan to either Flame or Blaze; 4 - Go to Firebase -> Registration and activate Login/Registration with email 5 - Go to Firebase -> storage and activate it;

3 - google maps 1 - Add your project to the google API console (https://console.cloud.google.com/apis?pli=1) 2 - Activate google Maps API 3 - Activate google Places API 4 - Add google maps API key to the res/values/Strings.xml file in the string google_maps_key

4 - PayPal 1 - Install Node.js. Check my video to see how it is done (https://www.youtube.com/watch?v=nLxH15a4-6g&list=PLxabZQCAe5fgXx8cn2iKOtt0VFJrf5bOd&index=42); 2 - go to paypal developer and create an app; 3 - enable payouts in the app you've just created; 4 - Add the paypal credentials to the node.js project. Again follow the youtube video to see how it is done; 5 - Set the fee in your index.js file to the percentage that you want 6 - deploy the project; 7 - Go to the android studio -> java -> your package name -> PayPalConfig: a) add the PAYPAL_CLIENT_ID which you get from the paypal developer control Panel; b) add the PAYPAL_PAYOUT_URL which you get in the firebase control panel -> functions and the url that you want is the payouts;

![Screenshot_20200210-090757](https://user-images.githubusercontent.com/28781884/83223729-398aca80-a1ae-11ea-8e4a-b8165337c595.png)
