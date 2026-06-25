# 📱 Sovereign Crypto & Cloud Mining Wallet

A 100% pure Kotlin and Jetpack Compose Android application built using **Clean Architecture** principles. This wallet operates independently with local military-grade encryption, peer-to-peer transaction simulations, and an automated cloud mining node engine.

---

## 🚀 Key Features

* **512-bit Security Infrastructure:** Generates a cryptographically secure 24-word BIP-39 standard mnemonic seed phrase for user sovereignty.
* **Cloud Mining Node Engine:** A background-threaded simulation using Kotlin Coroutines and StateFlow that mints Sovereign Coins locally based on configurable network hashrates.
* **ButtPay P2P Gateway:** A clean, Google Pay-inspired user interface to execute secure peer-to-peer asset transfers with cryptographic SHA-256 transaction hashing and balance validation.
* **Military-Grade Local Storage:** Utilizes Android `EncryptedSharedPreferences` via the Jetpack Security library to encrypt all sensitive mnemonic seeds, user configurations, and mining balances at rest.
* **Obfuscated Network Syncing:** Implements run-time byte-inversion obfuscation to manage and secure internal API keys and remote server endpoint URLs against reverse-engineering.

---

## 🛠️ Architecture Blueprint

The project strictly follows the **Data-UI-ViewModel** separation pattern to maintain high stability, testability, and isolated layer logic:

```text
app/src/main/java/com/clean/cryptowallet/
│
├── data/
│   ├── mining/      # Mining Core Engine & Local State Models
│   ├── network/     # Obfuscated API Keys & Server Sync Managers
│   ├── payment/     # ButtPay P2P Validation Logic & Transactions
│   └── security/    # Encrypted Database & SHA-256 App-Lock Verification
│
└── ui/
    ├── dashboard/   # Multi-tab Bottom Navigation Hub
    ├── mining/      # Neon-Glow Mining Dashboard View
    ├── payment/     # Google Pay Styled Transaction UI
    ├── security/    # Custom Numeric Security Pin-Pad Screen
    └── wallet/      # 24-Word Mnemonic Generation View
