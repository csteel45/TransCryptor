# TransCryptor — Improvement Roadmap

TransCryptor began (circa 1998–2000) as a Swing prototype for encrypting directories of
files. In its current archived state it is an honest skeleton: the UI shell and an output
console exist, but **the encryption itself was never implemented**, the GUI actions are
unwired, and the code uses late-1990s idioms. This roadmap turns it into a small, correct,
modern file-encryption utility while keeping its original character.

## Current-state assessment

- **No actual cryptography.** Despite the name and `import java.security.*`, nothing is
  encrypted. `TransCryptor.processFile()` opens each file, reads it in 64-byte chunks,
  `Thread.sleep(10)`s, and discards the bytes — there is no cipher and no output written.
- **Unwired GUI.** `TransCryptorFrame` is a NetBeans-style form skeleton: buttons are
  labeled `"jButton1"`, and every menu item except **Exit** has no action listener.
- **Two competing entry points.** Both `TransCryptor` and `TransCryptorFrame` declare
  `main()`; `TransCryptor.main()` hardcodes `C:\devel\shack`.
- **Correctness bugs.** `ProcessDirectory` builds paths by string concatenation and can
  NPE on `dir.list()`; library methods call `System.exit(-1)` on error; `OutputViewer`
  casts each byte to a `char` (corrupting non-ASCII) and mutates a Swing component off the
  Event Dispatch Thread.
- **Legacy idioms.** Deprecated `Frame.show()`, raw `Vector`/`elementAt`, default package,
  `System.out.println` + `printStackTrace` for all diagnostics.
- **No engineering scaffold.** No build system (bare `javac`), no tests, no CI, minimal docs.

## Phased plan

### Phase 1 — Correct & runnable (fix the prototype)
Make what exists behave correctly and launch cleanly, without yet adding crypto.
- Remove the hardcoded `C:\devel\shack` path and unify the two `main()` methods into one launcher (#3, #10).
- Fix `processFile()` stream handling and `ProcessDirectory` path/NPE bugs; use `java.nio.file` (#2, #5).
- Fix `OutputViewer` byte→char corruption and marshal appends onto the EDT; cap buffer growth (#4).
- Stop calling `System.exit()` from library methods; surface exceptions (#6).
- Launch the Swing UI on the EDT; replace deprecated `show()` (#7, #8).

### Phase 2 — Implement the actual feature (encryption)
Deliver the capability the tool is named for.
- Implement authenticated encryption: **AES-256-GCM** with a random IV, **PBKDF2** (or Argon2) password-based key derivation, and a versioned file header (#1).
- Write encrypted output safely: temp file + atomic move, optional `.bak`, never corrupt the original on failure (#17).
- Wire the GUI actions (Open, TransCrypt, Decrypt, Clean, Undo) and give the buttons real labels (#11).
- Add a headless CLI mode so encryption is scriptable without the GUI (#12).

### Phase 3 — Modernize & harden the project
- Move classes into a real package (e.g. `com.fortmoon.transcryptor`) (#9).
- Replace legacy APIs (`Vector` → `List<File>`, `StringBuffer` → `StringBuilder`) (#7).
- Add a **Maven** build producing a runnable jar with a `Main-Class` manifest (#13).
- Add **JUnit 5** tests for the encrypt→decrypt round-trip and the directory walker (#14).
- Add **GitHub Actions** CI (build + test on push/PR) (#15).
- Replace `println`/`printStackTrace` with `java.util.logging` (#16-adjacent).
- Expand the README, add a CHANGELOG, and document the crypto design / threat model (#16).

## Issue index

The tracked work items (created as GitHub issues) are summarized in
[`docs/backlog.md`](docs/backlog.md); each links to its issue once filed.
