const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
// const express = require('express');
// const cors = require('cors')({origin: true});
// const app = express();

//
// const validateFirebaseIdToken = (req, res, next) => {
//     console.log('Check id');
//     if ((!req.headers.authorization || !req.headers.authorization.startsWith('Bearer ' ))) {
//       res.status(403).send('Unauthorized');
//       return;
//     }
//
//
//   let idToken;
//
//   if (req.headers.authorization && req.headers.authorization.startsWith('Bearer ' )) {
//     console.log('we found an "authorization" header');
//     idToken = req.headers.authorization.split('Bearer ')[1];
//   }
//   admin.auth().verifyIdToken(idToken).then(decodedIdToken => {
//     console.log('ID was correctly decoded', decodedIdToken);
//     req.user = decodedIdToken;
//     next();
//     }).catch(error => {
//     console.error('Error while verifying the Firebase ID token:', error);
//     res.status(403).send('Unauthorized');
//
//   });
// }

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });


// exports.dailyNotification = functions.https.onRequest(app) => {
//
// });


exports.cronEndpoint = functions.https.onRequest((req, res) => {
  console.log("woohoo");

const options = {
  priority: "high",
  timeToLive: 100
}

  var topic = 'dailyNotification' // required fill with device token or topics
  var payload = {
      notification: {
          title: 'Time Goalie',
          body: 'Don\'t forget to save some goals today!'
      }
  };
  //promise style
  admin.messaging().sendToTopic(topic, payload, options)
      .then(function(response){
          console.log("Successfully sent with response: ", response);
      })
      .catch(function(err){
          console.log("Something has gone wrong!");
          console.error(err);
      });
  res.send('sent');
});
