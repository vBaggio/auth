---
description: 'Commit message conventions for this repository'
applyTo: commit
---

# Commit Message Guidelines

## Format

- Start every commit message with a Conventional Commits prefix: `type(optional-scope): summary`.
- Accepted `type` values (use lowercase): `feat`, `fix`, `docs`, `test`, `refactor`, `chore`, `perf`, `ci`.
- Use an optional `scope` in parentheses for affected modules (e.g. `config`, `controller`); omit the scope if it does not add clarity.
- Write the `summary` in English, using the imperative mood, in <= 72 characters, and without a trailing period.

## Body and Footer

- Separate the summary from the body with a blank line when additional context is needed.
- Keep body lines limited to 100 characters and focus on the reasoning behind the change.
- Use bullet points (`-`) for lists or multiple notes.
- Reference related issues or tickets in a footer using `Refs: #123` or `Fixes: #123` when applicable.

## General Rules

- Ensure each commit encapsulates a logical, self-contained change set.
- Avoid mixing unrelated modifications within a single commit.
- Favor English in all summaries and bodies to keep history consistent.
- Proofread the message before committing to maintain a clean, searchable history.
