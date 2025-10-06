package com.cutechat.app.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtils {
    
    private static FirebaseAuth auth;
    private static FirebaseDatabase database;
    private static FirebaseFirestore firestore;
    private static FirebaseStorage storage;

    public static FirebaseAuth getAuth() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static FirebaseDatabase getDatabase() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
        }
        return database;
    }

    public static FirebaseFirestore getFirestore() {
        if (firestore == null) {
            firestore = FirebaseFirestore.getInstance();
        }
        return firestore;
    }

    public static FirebaseStorage getStorage() {
        if (storage == null) {
            storage = FirebaseStorage.getInstance();
        }
        return storage;
    }

    public static DatabaseReference getUsersRef() {
        return getDatabase().getReference(Constants.REF_USERS);
    }

    public static DatabaseReference getMessagesRef() {
        return getDatabase().getReference(Constants.REF_MESSAGES);
    }

    public static DatabaseReference getFriendRequestsRef() {
        return getDatabase().getReference(Constants.REF_FRIEND_REQUESTS);
    }

    public static DatabaseReference getFriendsRef() {
        return getDatabase().getReference(Constants.REF_FRIENDS);
    }

    public static DatabaseReference getCallsRef() {
        return getDatabase().getReference(Constants.REF_CALLS);
    }

    public static DatabaseReference getTypingRef() {
        return getDatabase().getReference(Constants.REF_TYPING);
    }

    public static DatabaseReference getPresenceRef() {
        return getDatabase().getReference(Constants.REF_PRESENCE);
    }

    public static StorageReference getProfileImagesRef() {
        return getStorage().getReference("profile_images");
    }

    public static StorageReference getChatImagesRef() {
        return getStorage().getReference("chat_images");
    }

    public static String getCurrentUserId() {
        return getAuth().getCurrentUser() != null ? getAuth().getCurrentUser().getUid() : null;
    }

    public static String getChatRoomId(String userId1, String userId2) {
        return userId1.compareTo(userId2) < 0 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }
}
