// Import the functions you need from the SDKs you need
import { initializeApp } from 'firebase/app';
import { getMessaging, getToken } from 'firebase/messaging';
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries
import { getFirestore } from 'firebase/firestore';

// Your web app's Firebase configuration
const firebaseConfig = {
    apiKey: process.env.REACT_APP_API_KEY,
    authDomain: process.env.REACT_APP_AUTH_DOMAIN,
    projectId: process.env.REACT_APP_PROJECT_ID,
    storageBucket: process.env.REACT_APP_STORAGE_BUCKET,
    messagingSenderId: process.env.REACT_APP_MESSAGING_SENDER_ID,
    appId: process.env.REACT_APP_ID,
};

// Initialize Firebase
export const app = initializeApp(firebaseConfig);
export const db = getFirestore(app);

export const messaging = getMessaging(app);

export const askForNotification = async () => {
    const permission = await Notification.requestPermission()
    console.log(permission)
    if (permission === 'granted') {
        const token = await getToken(messaging, {vapidKey: 'BNHFBFqa0wB8QnVqaOQhJf1UZaT7q2JiMLVf0hXicKeHLTGoGds7NV5kKzuX0hGr9bt8sDOdOrQl6uwoF9melTU'})
        console.log(token)
    }
};
