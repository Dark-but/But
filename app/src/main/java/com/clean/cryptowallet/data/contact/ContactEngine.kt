package com.clean.cryptowallet.data.contact

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

/**
 * कांटेक्ट बुक का डेटा मॉडल
 */
data class WalletContact(
    val id: String,
    val name: String,
    val addressOrUid: String
)

class ContactEngine {
    private val _contacts = MutableStateFlow<List<WalletContact>>(emptyList())
    val contacts: StateFlow<List<WalletContact>> = _contacts.asStateFlow()

    init {
        // टेस्ट करने के लिए कुछ डिफ़ॉल्ट डमी कांटेक्ट डाल देते हैं
        _contacts.value = listOf(
            WalletContact(UUID.randomUUID().toString(), "Sovereign Dev", "0x7a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t"),
            WalletContact(UUID.randomUUID().toString(), "ButtPay Admin", "BN_ADMIN99X")
        )
    }

    /**
     * नया कांटेक्ट लिस्ट में जोड़ना
     */
    fun addNewContact(name: String, addressOrUid: String): Boolean {
        if (name.isBlank() || addressOrUid.isBlank()) return false
        
        val newContact = WalletContact(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            addressOrUid = addressOrUid.trim()
        )
        
        _contacts.update { currentList -> currentList + newContact }
        return true
    }

    /**
     * लिस्ट से कोई कांटेक्ट डिलीट करना
     */
    fun deleteContact(contactId: String) {
        _contacts.update { currentList -> currentList.filter { it.id != contactId } }
    }
}
