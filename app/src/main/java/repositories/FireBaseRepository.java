package repositories;

import com.google.firebase.database.FirebaseDatabase;

public class FireBaseRepository {
    private volatile static FireBaseRepository sFirebaseRepository;
    private FirebaseDatabase database;

    public static synchronized FireBaseRepository getInstance() {
        if(sFirebaseRepository == null) {
            synchronized (FireBaseRepository.class) {
                sFirebaseRepository = new FireBaseRepository();
            }
        }
        return sFirebaseRepository;
    }

    private FireBaseRepository(){
        database = FirebaseDatabase.getInstance("https://monopoly-b9e36-default-rtdb.europe-west1.firebasedatabase.app/");
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public void setDatabase(FirebaseDatabase database) {
        this.database = database;
    }
}
