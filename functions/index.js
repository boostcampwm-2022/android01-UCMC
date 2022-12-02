const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);
const db = admin.firestore();

exports.dailyScheduledFunctionCrontab = functions.pubsub.schedule("0 0 * * *")
    .timeZone("Asia/Seoul")
    .onRun(async (context) => {
      const curr = new Date();
      const utc = curr.getTime() + (curr.getTimezoneOffset() * 60 * 1000);
      const KR_TIME_DIFF = 9 * 60 * 60 * 1000;
      const now = new Date(utc + (KR_TIME_DIFF)).getTime();
      const reservationRef = db.collection("reservations");
      const userRef = db.collection("users");

      const returnQuery = reservationRef
          .where("state", "==", "대여중");
      await returnQuery.get().then((snapshot) => {
        snapshot.forEach((reservationDoc) => {
          if (reservationDoc.get("reservationDate.end") <= now) {
            reservationDoc.ref.update("state", "반납완료");
            const lenderId = reservationDoc.get("lenderId");
            userRef.doc(lenderId).get().then((userDoc) => {
              const token = userDoc.get("messageToken");
              const payload = {
                "data": {
                  "type": "반납완료",
                  "message": "반납이 완료되었습니다.",
                  "reservationId": reservationDoc.id,
                  "carId": reservationDoc.get("carId"),
                  "fromId": reservationDoc.get("ownerId"),
                },
              };
              admin.messaging().sendToDevice(token, payload);
            });
          }
        });
      });

      const rentQuery = reservationRef.where("state", "==", "허락");
      await rentQuery.get().then((snapshot) => {
        snapshot.forEach((doc) => {
          if (doc.get("reservationDate.start") <= now &&
              doc.get("reservationDate.end") >= now) {
            doc.ref.update("state", "대여중");
          }
        });
      });
      return null;
    });
