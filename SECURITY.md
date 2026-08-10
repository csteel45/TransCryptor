# Security & threat model

TransCryptor encrypts the **contents of files at rest** under a passphrase. This note
states what that does and does not protect, so you can judge whether it fits your use.

## Cryptographic design

- **Key derivation:** PBKDF2-HMAC-SHA256, 210,000 iterations, over a random 128-bit salt.
- **Encryption:** AES-256-GCM with a random 96-bit IV and a 128-bit authentication tag.
- **Format:** a versioned `TCR1` header (salt, IV, KDF parameters) is prepended to the
  ciphertext and authenticated as additional data (AAD), so the parameters cannot be
  altered without detection.
- Each file gets a fresh salt and IV; key material is zeroized after use.

## What it protects against

- **Confidentiality at rest:** an attacker who obtains an encrypted `.tcr` file cannot
  recover the plaintext without the passphrase.
- **Integrity / tampering:** any modification to the ciphertext *or* the header, or use of
  the wrong passphrase, causes decryption to **fail closed** (no partial or garbage output).

## What it does NOT protect against

- **Weak passphrases.** PBKDF2 slows guessing but cannot save a weak or reused password —
  choose a strong one.
- **A compromised host.** Malware, keyloggers, or memory scraping on the machine doing the
  encryption/decryption defeat any file encryptor.
- **Plaintext elsewhere.** Copies, temp files, swap, backups, or editor history outside the
  files you encrypt are not covered.
- **Metadata.** File size, name, and existence are not hidden.
- **Side channels** and unvetted deployment. This is archival/hobby software and has **not
  been independently audited**.

## Recommendation

For casual protection of personal files it is straightforward and uses standard,
strong primitives. For high-stakes secrets, prefer a vetted, audited tool
(e.g. [age](https://github.com/FiloSottile/age) or GnuPG) and full-disk encryption.
