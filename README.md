# Vehicle-Breakdown-Mechanic-Finder
An Android Application to find the nearby Mechanics 

P.S: If you're going to download the full project please use your own firebase API, in this project API will NOT be mantained and the app may not work.

### Implementation Guide

1 - Project 1 - Open the Project in your android studio; 2 - !!!!IMPORTANT!!!! Change the Package Name. You can check how to do that here (https://stackoverflow.com/questions/16804093/android-studio-rename-package)

2 - Firebase Panel 1 - Create Firebase Project (https://console.firebase.google.com/); 2 - import the file google-service.json into your project as the instructions say; 3 - Change Pay Plan to either Flame or Blaze; 4 - Go to Firebase -> Registration and activate Login/Registration with email 5 - Go to Firebase -> storage and activate it;

3 - google maps 1 - Add your project to the google API console (https://console.cloud.google.com/apis?pli=1) 2 - Activate google Maps API 3 - Activate google Places API 4 - Add google maps API key to the res/values/Strings.xml file in the string google_maps_key

4 - PayPal 1 - Go to paypal developer and create an app; 2 - enable payouts in the app you've just created; 3 - Add the paypal credentials to the project; 4 - Set the fee in your index.js file to the percentage that you want 5 - deploy the project; 6 - Go to the android studio -> java -> your package name -> PayPalConfig: a) add the PAYPAL_CLIENT_ID which you get from the paypal developer control Panel; b) add the PAYPAL_PAYOUT_URL which you get in the firebase control panel -> functions and the url that you want is the payouts;


### Screenshot

<img src=https://user-images.githubusercontent.com/28781884/83223729-398aca80-a1ae-11ea-8e4a-b8165337c595.png width="300">

#### User

<img src=https://user-images.githubusercontent.com/28781884/83226297-3c88b980-a1b4-11ea-8827-34515dca6a92.PNG width="300" height="630"><img src=https://user-images.githubusercontent.com/28781884/83226641-01d35100-a1b5-11ea-9123-dd4d9264ec6b.png width="300"><img src=https://user-images.githubusercontent.com/28781884/83226884-89b95b00-a1b5-11ea-9ae0-f691daa10186.png width="300"><img src=https://user-images.githubusercontent.com/28781884/83226106-ca17d980-a1b3-11ea-8651-7612f8004ba7.PNG width="300">
<img src=https://user-images.githubusercontent.com/28781884/83226812-61316100-a1b5-11ea-8fe7-35e0d5f7e5f4.PNG width="300"><img src=https://user-images.githubusercontent.com/28781884/83226971-ae153780-a1b5-11ea-92c4-8f7d33987a96.PNG width="300" height="600">

#### Mechanic

<img src=https://user-images.githubusercontent.com/28781884/83227297-69d66700-a1b6-11ea-9d38-f4997ae4be95.PNG width="300" height="550"><img src=https://user-images.githubusercontent.com/28781884/83224886-1a416c80-a1b1-11ea-8b1c-9baf9792b16d.PNG width="300">

<img src=https://user-images.githubusercontent.com/28781884/83227563-16184d80-a1b7-11ea-8214-68c1929e8983.PNG width="300"><img src=https://user-images.githubusercontent.com/28781884/83227495-f123da80-a1b6-11ea-8662-2fad3b9300ca.PNG width="300">





