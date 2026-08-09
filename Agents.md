# Agent Instructions

## Collaboration

The user actively edits this repository while Codex is working.

When making changes:

- Treat the current Git worktree as the source of truth.
- Always inspect and use all current changes in Git before editing related files.
- Assume uncommitted changes may be user work. Do not revert, overwrite, or discard them unless explicitly asked.
- If a file has user edits and Codex needs to modify it, work with the current contents rather than restoring an older version.
- If concurrent changes make a requested edit ambiguous, inspect the latest file state and make the smallest compatible change.

## CLI Documentation

This project uses Picocli for command-line switches.

Whenever Picocli switches are introduced, removed, renamed, or have their behavior/defaults changed:

- Update `README.md` in the same change.
- Keep examples in `README.md` aligned with the current generated `sorty --help` output.
- Call out short-option conflicts explicitly, such as `-s` being speed `--slow` while seed is `--seed`.

## Verification

For code changes, prefer:

```bash
env -u JAVA_HOME ./gradlew test
env -u JAVA_HOME ./gradlew installDist
```

For CLI changes, also verify:

```bash
env -u JAVA_HOME ./sorty.sh --help
```
