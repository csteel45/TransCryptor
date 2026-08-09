# TransCryptor

A small file-encryption utility (with a Swing UI and a console output viewer),
originally written circa 1998–2000 as a personal tool and archived here, MIT-licensed.
It is being refreshed — see the [ROADMAP](ROADMAP.md) and the open issues.

## Status

- ✅ **Authenticated encryption engine** — PBKDF2 + AES-256-GCM (see below)
- 🚧 GUI and CLI wiring for the engine (issues [#7](https://github.com/csteel45/TransCryptor/issues/7), [#8](https://github.com/csteel45/TransCryptor/issues/8), [#1](https://github.com/csteel45/TransCryptor/issues/1))

## Cryptography

The encryption engine provides authenticated, password-based encryption:

- **PBKDF2-HMAC-SHA256** key derivation (random 128-bit salt, 210,000 iterations)
- **AES-256-GCM** with a random 96-bit IV
- A versioned `TCR1` file header (salt, IV, KDF parameters) authenticated as additional
  data, so a wrong password or any tampering with the header or ciphertext fails closed.

## Build & verify

```bash
javac -d build *.java
java -cp build CryptoSelfTest
```

Requires only a standard JDK (no external dependencies). `CryptoSelfTest` exercises the
round-trip, wrong-password rejection, and tamper detection end to end.

## License

MIT — see [LICENSE](LICENSE).
