#!/usr/bin/env python3
"""Generate unlock codes for CPMAI Prep App (Unofficial). Sell one code per customer."""
import hashlib
import secrets
import sys

SECRET = "cpmai-prep-unofficial-2026-key"


def checksum(body: str) -> str:
    digest = hashlib.sha256((body.upper() + SECRET).encode()).hexdigest()
    return digest[:4].upper()


def make_key() -> str:
    body = secrets.token_hex(3).upper()
    return f"PREP-{body}-{checksum(body)}"


if __name__ == "__main__":
    n = int(sys.argv[1]) if len(sys.argv) > 1 else 5
    print("Give each paying customer one code. They enter it in Unlock full version.")
    for _ in range(n):
        print(make_key())
