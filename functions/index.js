const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);
const db = admin.firestore();

exports.dailyScheduledFunctionCrontab = functions.pubsub.schedule("0 9 * * *")
    .timeZone("Asia/Seoul")
    .onRun(async (context) => {
      const curr = new Date();
      const utc = curr.getTime() + (curr.getTimezoneOffset() * 60 * 1000);
      const KR_TIME_DIFF = 9 * 60 * 60 * 1000;
      const now = new Date(utc + (KR_TIME_DIFF)).getTime();
      const reservationRef = db.collection("reservations");
      const userRef = db.collection("users");

      const returnQuery = reservationRef
          .where("state", "==", 2);
      await returnQuery.get().then((snapshot) => {
        snapshot.forEach(async (reservationDoc) => {
          if (reservationDoc.get("reservationDate.end") <= now) {
            await reservationDoc.ref.update("state", -2);
            const reservation = await reservationDoc.ref.get();
            const lenderId = await reservation.data().lenderId;
            userRef.doc(lenderId).get().then(async (userDoc) => {
              if (userDoc.exists) {
                const token = await userDoc.data().messageToken;
                const payload = {
                  "data": {
                    "type": "차량 반납",
                    "message": "반납이 완료되었습니다.",
                    "reservationId": reservationDoc.id,
                    "carId": reservation.data().carId,
                    "fromId": reservation.data().ownerId,
                  },
                };
                await admin.messaging().sendToDevice(token, payload);
              }
            });
          }
        });
      });

      const rentQuery = reservationRef.where("state", "==", 1);
      await rentQuery.get().then((snapshot) => {
        snapshot.forEach((doc) => {
          if (doc.get("reservationDate.start") <= now &&
              doc.get("reservationDate.end") >= now) {
            doc.ref.update("state", 2);
          }
        });
      });

      const cancelQuery = reservationRef.where("state", "==", 0);
      await cancelQuery.get().then((snapshot) => {
        snapshot.forEach((doc) => {
          if (doc.get("reservationDate.start") <= now) {
            doc.ref.update("state", -1);
          }
        });
      });
      return null;
    });
