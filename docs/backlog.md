# TransCryptor — Backlog

The tracked improvement work, grouped by the phases in [`../ROADMAP.md`](../ROADMAP.md).
Each item links to its GitHub issue.

## Phase 1 — Correct & runnable
- [ ] [#1](https://github.com/csteel45/TransCryptor/issues/1) — processFile() never encrypts or writes output — it reads bytes, sleeps, and discards them
- [ ] [#2](https://github.com/csteel45/TransCryptor/issues/2) — Remove hardcoded `C:\devel\shack` path in TransCryptor.main
- [ ] [#3](https://github.com/csteel45/TransCryptor/issues/3) — ProcessDirectory: use java.nio.file, handle null from File.list(), drop string path concatenation
- [x] [#4](https://github.com/csteel45/TransCryptor/issues/4) — OutputViewer corrupts non-ASCII output and mutates Swing off the EDT — **done** (UTF-8 buffered decode, EDT-marshalled, capped)
- [ ] [#5](https://github.com/csteel45/TransCryptor/issues/5) — Stop calling System.exit() from library methods; surface exceptions instead
- [ ] [#6](https://github.com/csteel45/TransCryptor/issues/6) — Unify the two main() entry points into a single launcher
- [ ] [#10](https://github.com/csteel45/TransCryptor/issues/10) — Launch the Swing UI on the Event Dispatch Thread; replace deprecated Frame.show()

## Phase 2 — Implement the feature (encryption)
- [x] [#11](https://github.com/csteel45/TransCryptor/issues/11) — Implement authenticated file encryption/decryption (AES-256-GCM + PBKDF2) — *flagship* — **done** (`Crypto`/`CryptoFormat`/`CryptoFiles`, verified by `CryptoSelfTest`)
- [ ] [#12](https://github.com/csteel45/TransCryptor/issues/12) — Write output safely: temp file + atomic move + optional backup; never corrupt the original
- [ ] [#7](https://github.com/csteel45/TransCryptor/issues/7) — Wire up the GUI actions (Open/TransCrypt/Decrypt/Clean/Undo) and label the placeholder buttons
- [x] [#8](https://github.com/csteel45/TransCryptor/issues/8) — Add a headless CLI mode for scripted encrypt/decrypt — **done** (`TransCryptorCli`)

## Phase 3 — Modernize & harden
- [ ] [#13](https://github.com/csteel45/TransCryptor/issues/13) — Move classes out of the default package into com.fortmoon.transcryptor
- [ ] [#14](https://github.com/csteel45/TransCryptor/issues/14) — Replace legacy idioms: Vector→List<File>, StringBuffer→StringBuilder, generics
- [ ] [#15](https://github.com/csteel45/TransCryptor/issues/15) — Add a Maven build producing a runnable jar (Main-Class manifest)
- [ ] [#16](https://github.com/csteel45/TransCryptor/issues/16) — Add JUnit 5 tests: encrypt→decrypt round-trip and directory walker
- [ ] [#17](https://github.com/csteel45/TransCryptor/issues/17) — Add GitHub Actions CI (build + test on push/PR)
- [ ] [#18](https://github.com/csteel45/TransCryptor/issues/18) — Replace System.out.println/printStackTrace with java.util.logging
- [ ] [#9](https://github.com/csteel45/TransCryptor/issues/9) — Expand README, add CHANGELOG and a crypto threat-model note

## Labels

The semantic labels `security`, `modernization`, `tech-debt`, `tests`, `ci` are referenced by
the roadmap but were not created (the token used to file these issues lacked label-management
permission). They can be added later via the repo's *Issues → Labels* UI, after which the
category can be applied to the relevant issues above.
