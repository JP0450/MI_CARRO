package com.unaula.proyecto_final.data

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.firestoreSettings

object FirebaseProvider {
    private var firestoreInstance: FirebaseFirestore? = null

    fun getFirestore(context: Context): FirebaseFirestore? {
        if (firestoreInstance == null) {
            try {
                if (FirebaseApp.getApps(context).isEmpty()) {
                    FirebaseApp.initializeApp(context)
                }
                val db = FirebaseFirestore.getInstance()
                db.firestoreSettings = firestoreSettings {
                    setLocalCacheSettings(
                        PersistentCacheSettings.newBuilder().build()
                    )
                }
                firestoreInstance = db
            } catch (e: Exception) {
                e.printStackTrace()
                firestoreInstance = null
            }
        }
        return firestoreInstance
    }
}
