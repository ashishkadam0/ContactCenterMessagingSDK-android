package com.ms.lcw

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class ChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //SoLoader.init(applicationContext, OpenSourceMergedSoMapping)
        //Manually initialize Firebase using FirebaseOptions
        //Refer your google-services.json
        val options = FirebaseOptions.Builder()
            .setApplicationId("1:1012377456421:android:3dbdd13dc348829c9414cc") // e.g. 1:1234567890:android:abcdef123456
            .setApiKey("AIzaSyBgms4o4py0MqxahzlTD4oZfi_45T-Fb8Y") // Your Firebase API key
            .setDatabaseUrl("your-database-url") // Your Firebase Database URL (optional)
            .setProjectId("oc-fcm-test") // Your Firebase Project ID
            .setStorageBucket("your-storage-bucket") // Your Firebase Storage Bucket (optional)
            .build()
        FirebaseApp.initializeApp(this, options)
    }
}
