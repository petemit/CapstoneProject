const functions = require('firebase-functions');
const admin = require('firebase-admin');
const secureCompare = require('secure-compare');
admin.initializeApp(functions.config().firebase);

exports.cronEndpoint = functions.https.onRequest((req, res) => {

const key = req.query.key;

// Exit if the keys don't match
if (!secureCompare(key, functions.config().cron.key)) {
  console.log('The key provided in the request does not match the key set in the environment. Check that', key,
      'matches the cron.key attribute in `firebase env:get`');
  res.status(403).send('Security key does not match. Make sure your "key" URL query parameter matches the ' +
      'cron.key environment variable.');
  return;
}


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
