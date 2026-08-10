# Changelog

All notable changes to TransCryptor are documented here. The format is loosely
based on [Keep a Changelog](https://keepachangelog.com/).

## [Unreleased]

### Added
- **Authenticated encryption engine (#11):** PBKDF2-HMAC-SHA256 + AES-256-GCM with a
  versioned `TCR1` file format whose header is authenticated as AAD
  (`CryptoFormat`, `Crypto`, `CryptoFiles`).
- **Command-line interface (#8):** `transcryptor encrypt|decrypt <in> [out]`, with the
  passphrase read from the console or `$TRANSCRYPTOR_PASSWORD`.
- **Single `Main` launcher (#6):** GUI with no arguments, CLI with arguments.
- **Maven build (#15):** `mvn package` produces a runnable `target/transcryptor.jar`.
- **JUnit 5 test suite (#16):** round-trip, wrong-password, and tamper detection.
- **GitHub Actions CI (#17):** `mvn verify` on push and pull request.
- **`SECURITY.md`** — cryptographic design and threat model (#9).

### Changed
- Sources moved into the `com.fortmoon.transcryptor` package (#13).
- `OutputViewer` now decodes UTF-8, updates on the Event Dispatch Thread, and caps the
  retained document (#4).
- The Swing UI launches on the EDT via `setVisible(true)` instead of the deprecated
  `show()` (#10).
- File writes are crash-safe: temp file + atomic move, so the target is never corrupted,
  including in-place encryption (#12).

### Removed
- The superseded, broken legacy console class — resolving the defects that lived only in
  it (#1, #2, #3, #5, #14).
