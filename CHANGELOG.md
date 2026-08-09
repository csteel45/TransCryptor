# Changelog

All notable changes to TransCryptor are documented here. The format is loosely
based on [Keep a Changelog](https://keepachangelog.com/).

## [Unreleased]

### Added
- **Authenticated encryption engine (#11).** PBKDF2-HMAC-SHA256 key derivation and
  AES-256-GCM encryption, with a versioned `TCR1` file format whose header is
  authenticated as additional data (AAD):
  - `CryptoFormat` — the on-disk container format (magic/version/KDF/salt/IV).
  - `Crypto` — `encrypt` / `decrypt` for byte arrays; fails closed on wrong password
    or tampering; zeroizes key material.
  - `CryptoFiles` — whole-file `encryptFile` / `decryptFile` helpers.
  - `CryptoSelfTest` — standalone verification (round-trip, wrong password, ciphertext
    and header tamper detection, file round-trip). All checks pass.
- MIT `LICENSE`, `README`, and an improvement `ROADMAP` with a linked `docs/backlog.md`.

### Notes
- The engine is verified via `CryptoSelfTest`; wiring it into the GUI and a CLI is
  tracked by issues #7 and #8, and replacing the legacy no-op file loop by #1.
