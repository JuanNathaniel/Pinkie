package com.example.pinkiewallet.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pinkiewallet.R
import com.example.pinkiewallet.backend.TransferActivity
import com.google.firebase.database.*

class ContactActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var contacts: ArrayList<String>
    private lateinit var filteredContacts: ArrayList<String>
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        supportActionBar?.hide()

        listView = findViewById(R.id.contact_list)
        searchView = findViewById(R.id.search_view)
        database = FirebaseDatabase.getInstance().reference.child("users") // menyesuaikan dengan struktur data Anda di Firebase

        // Cek dan minta izin jika belum diberikan
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
        } else {
            // Izin sudah diberikan, tampilkan kontak
            loadContacts()
        }

        // Setup SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterContacts(newText)
                return false
            }
        })

        // Setup ListView click listener
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedContact = filteredContacts[position]
            Log.d("ContactActivity", "Contact clicked: $selectedContact") // Log for debugging
            if (selectedContact.contains("(Connected)")) {
                val phoneNumber = selectedContact.split(":")[1].split(" ")[1]
                Log.d("ContactActivity", "Navigating to TransferActivity with phone number: $phoneNumber") // Log for debugging
                val intent = Intent(this, TransferActivity::class.java)
                intent.putExtra("nomor_telepon", phoneNumber)
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Izin diberikan
                loadContacts()
            } else {
                // Izin ditolak
                Toast.makeText(this, "Permission to access contacts denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadContacts() {
        contacts = ArrayList()

        val contentResolver = contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.let {
            if (it.count > 0) {
                val tempContacts = ArrayList<Pair<String, String>>()
                while (it.moveToNext()) {
                    val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
                    val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    val hasPhoneNumberIndex = it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)

                    if (idIndex >= 0 && nameIndex >= 0 && hasPhoneNumberIndex >= 0) {
                        val id = it.getString(idIndex)
                        val name = it.getString(nameIndex)

                        if (it.getInt(hasPhoneNumberIndex) > 0) {
                            val phoneCursor: Cursor? = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                arrayOf(id),
                                null
                            )

                            phoneCursor?.let { pc ->
                                val phoneNumberIndex = pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                if (phoneNumberIndex >= 0) {
                                    while (pc.moveToNext()) {
                                        val phoneNumber = pc.getString(phoneNumberIndex)
                                        tempContacts.add(Pair(name, phoneNumber))
                                    }
                                }
                                pc.close()
                            }
                        }
                    }
                }
                it.close()
                checkContactsInFirebase(tempContacts)
            }
        }
    }

    private fun checkContactsInFirebase(tempContacts: ArrayList<Pair<String, String>>) {
        val totalContacts = tempContacts.size
        var processedContacts = 0

        for (contact in tempContacts) {
            val name = contact.first
            val phoneNumber = contact.second
            database.orderByChild("phone_number").equalTo(phoneNumber)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            contacts.add("$name: $phoneNumber (Connected)")
                        } else {
                            contacts.add("$name: $phoneNumber")
                        }
                        processedContacts++
                        if (processedContacts == totalContacts) {
                            // Update adapter after all contacts are processed
                            filteredContacts = ArrayList(contacts)
                            adapter = ArrayAdapter(this@ContactActivity, R.layout.list_contact, filteredContacts)
                            listView.adapter = adapter
                            adapter.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@ContactActivity, "Error checking contact in Firebase", Toast.LENGTH_SHORT).show()
                        processedContacts++
                        if (processedContacts == totalContacts) {
                            // Update adapter after all contacts are processed
                            filteredContacts = ArrayList(contacts)
                            adapter = ArrayAdapter(this@ContactActivity, R.layout.list_contact, filteredContacts)
                            listView.adapter = adapter
                            adapter.notifyDataSetChanged()
                        }
                    }
                })
        }
    }

    private fun filterContacts(query: String?) {
        filteredContacts.clear()
        if (query.isNullOrEmpty()) {
            filteredContacts.addAll(contacts)
        } else {
            val lowerCaseQuery = query.lowercase()
            filteredContacts.addAll(
                contacts.filter {
                    it.lowercase().contains(lowerCaseQuery)
                }
            )
        }
        adapter.notifyDataSetChanged()
    }
}
