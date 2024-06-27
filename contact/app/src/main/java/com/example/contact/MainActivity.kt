package com.example.contact

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var contacts: ArrayList<String>
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.contact_list)
        searchView = findViewById(R.id.search_view)

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
                adapter.filter.filter(newText)
                return false
            }
        })
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
                                        contacts.add("$name: $phoneNumber")
                                    }
                                }
                                pc.close()
                            }
                        }
                    }
                }
                it.close()
            }
        }

        adapter = ArrayAdapter(this, R.layout.list_item, contacts)
        listView.adapter = adapter
    }
}
