 // Import the functions you need from the SDKs you need
 import { initializeApp } from "firebase/app";
 import { getFirestore } from "firebase/firestore";
 // TODO: Add SDKs for Firebase products that you want to use
 // https://firebase.google.com/docs/web/setup#available-libraries
 // Your web app's Firebase configuration
 const firebaseConfig = {
    apiKey: "AIzaSyDmzc_IQxaCLP9Q2_-QbuvLETEjRwIdPS4",
    authDomain: "onlinebank-85db5.firebaseapp.com",
    projectId: "onlinebank-85db5",
    storageBucket: "onlinebank-85db5.appspot.com",
    messagingSenderId: "1061678284346",
    appId: "1:1061678284346:web:c4d18cb36839fc7ea9ff03",
    measurementId: "G-3NLS33JXS1"
  };

 // Initialize Firebase

 const app = initializeApp(firebaseConfig);
 // Export firestore database
 // It will be imported into your react app whenever it is needed
 export const db = getFirestore(app);
