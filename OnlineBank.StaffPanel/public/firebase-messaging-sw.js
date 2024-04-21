// Give the service worker access to Firebase Messaging.
// Note that you can only use Firebase Messaging here. Other Firebase libraries
// are not available in the service worker.
importScripts('https://www.gstatic.com/firebasejs/8.10.1/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/8.10.1/firebase-messaging.js');

// Initialize the Firebase app in the service worker by passing in
// your app's Firebase config object.
// https://firebase.google.com/docs/web/setup#config-object
firebase.initializeApp({
    apiKey: "AIzaSyD-BC23hx7_lEs5DP8ceojL4CFUrnF-ORM",
    authDomain: "onlinebankstorage.firebaseapp.com",
    projectId: "onlinebankstorage",
    storageBucket: "onlinebankstorage.appspot.com",
    messagingSenderId: "932655020837",
    appId: "1:932655020837:web:1f13ece450e7f80170b8cc"
  });

// Retrieve an instance of Firebase Messaging so that it can handle background
// messages.
const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
    console.log(
      '[firebase-messaging-sw.js] Received background message ',
      payload
    );
    // Customize notification here
    const notificationTitle = payload.notification.title;
    const notificationOptions = {
      body: payload.notification.body,
      icon: payload.notification.icon
    };
    
    self.registration.showNotification(notificationTitle, notificationOptions);
  });
