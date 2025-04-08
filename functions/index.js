const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendDailyReminders = functions.pubsub.schedule("every day 08:00").timeZone("Asia/Kolkata").onRun(async (context) => {
    const snapshot = await admin.firestore().collection("user_plants").get();

    snapshot.forEach(doc => {
        const data = doc.data();
        const hour = data.hour;
        const minute = data.minute;

        const now = new Date();
        const targetHour = now.getHours();
        const targetMinute = now.getMinutes();

        if (hour === targetHour && minute === targetMinute && data.fcmToken) {
            const message = {
                notification: {
                    title: `Time to water ${data.name} 🌿`,
                    body: `${data.name} needs hydration! 💧`
                },
                token: data.fcmToken
            };

            admin.messaging().send(message)
                .then(() => {
                    console.log(`Sent reminder to ${data.name}`);
                })
                .catch((error) => {
                    console.error("Error sending message:", error);
                });
        }
    });

    return null;
});
