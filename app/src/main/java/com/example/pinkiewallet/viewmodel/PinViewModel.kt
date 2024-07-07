import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PinViewModel : ViewModel() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val _pinVerificationResult = MutableLiveData<Boolean>()
    val pinVerificationResult: LiveData<Boolean> = _pinVerificationResult

    private val _balanceUpdateResult = MutableLiveData<Boolean>()
    val balanceUpdateResult: LiveData<Boolean> = _balanceUpdateResult

    fun verifyPin(inputPin: String, hashedInputPin: String) {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            val databaseReference = database.getReference("users").child(userId).child("pin")

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val storedHashedPin = snapshot.getValue(String::class.java)
                    _pinVerificationResult.value = hashedInputPin == storedHashedPin
                }

                override fun onCancelled(error: DatabaseError) {
                    _pinVerificationResult.value = false
                }
            })
        } else {
            _pinVerificationResult.value = false
        }
    }

    fun updateBalance(jumlahHarga: Int) {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            val userRef = database.getReference("users").child(userId)

            userRef.child("balance").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val currentBalance = snapshot.getValue(Int::class.java) ?: 0

                        if (currentBalance >= jumlahHarga) {
                            val newBalance = currentBalance - jumlahHarga
                            userRef.child("balance").setValue(newBalance).addOnCompleteListener { task ->
                                _balanceUpdateResult.value = task.isSuccessful
                            }
                        } else {
                            _balanceUpdateResult.value = false
                        }
                    } else {
                        _balanceUpdateResult.value = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _balanceUpdateResult.value = false
                }
            })
        } else {
            _balanceUpdateResult.value = false
        }
    }
}
