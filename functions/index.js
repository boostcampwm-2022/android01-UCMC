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

      const returnQuery = reservationRef
          .where("state", "==", "대여중");
      await returnQuery.get().then((snapshot) => {
        snapshot.forEach((doc) => {
          if (doc.get("reservationDate.end") <= now) {
            doc.ref.update("state", "반납 완료");
          }
        });
      });

      const rentQuery = reservationRef.where("state", "==", "예약 완료");
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
