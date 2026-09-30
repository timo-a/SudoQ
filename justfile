set default-list := true

[arg('bumptype', pattern='major|mj|ma|M|minor|mi|mn|m|patch|p')]
bump-version bumptype:
    kotlinc -script utilities/scripts/bump_version.kts {{bumptype}}

bump-version-code:
    kotlinc -script utilities/scripts/bump_version_code.kts

bump-version-commit:
    kotlinc -script utilities/scripts/bump_version_commit.kts

copy-changelog source target:
    #!/usr/bin/env bash
    FPATH=fastlane/metadata/android
    cp $FPATH/de-DE/changelogs/{{source}}.txt $FPATH/de-DE/changelogs/{{target}}.txt
    cp $FPATH/en-US/changelogs/{{source}}.txt $FPATH/en-US/changelogs/{{target}}.txt
    cp $FPATH/fr-FR/changelogs/{{source}}.txt $FPATH/fr-FR/changelogs/{{target}}.txt

release-notes:
    kotlinc -script utilities/scripts/bump_version_release_summary_googleplay.kts

# requires: .env to contain keepassxc location and entry names (for keystore credentials)
#           keystore.properties to contain keystore location and key alias
#           keepassxc file and keystore to exist
[doc('build a signed release App Bundle (.aab)')]
release-bundle:
    #!/usr/bin/env bash
    set -euo pipefail

    if [ -f .env ]; then
        set -a
        source .env
        set +a
    fi

    KDBX_FILE="${KDBX}"
    KDBX_ENTRY_STORE="${KDBX_ENTRY_STORE}"
    KDBX_ENTRY_KEY="${KDBX_ENTRY_KEY}"

    if [ -z "$KDBX_FILE" ]; then
        echo "Error: KDBX environment variable is not set."
        exit 1
    fi

    if [ -z "$KDBX_ENTRY_STORE" ]; then
        echo "Error: KDBX_ENTRY_STORE environment variable is not set."
        exit 1
    fi

    if [ -z "$KDBX_ENTRY_KEY" ]; then
        echo "Error: KDBX_ENTRY_KEY environment variable is not set."
        exit 1
    fi

    if [ ! -f "$KDBX_FILE" ]; then
        echo "Error: KeePass database '$KDBX_FILE' not found."
        exit 1
    fi

    read -rs -p "Enter password for KeePass database '$KDBX_FILE': " KDBX_PASS
    echo ""

    KEYSTORE_PASSWORD=$(echo "$KDBX_PASS" | keepassxc-cli show -s -a Password "$KDBX_FILE" "$KDBX_ENTRY_STORE" 2>/dev/null || true)

    if [ -z "$KEYSTORE_PASSWORD" ]; then
        echo "Error: Could not retrieve password for entry '$KDBX_ENTRY_STORE' from '$KDBX_FILE'."
        exit 1
    fi

    KEY_PASSWORD=$(echo "$KDBX_PASS" | keepassxc-cli show -s -a Password "$KDBX_FILE" "$KDBX_ENTRY_KEY" 2>/dev/null || true)
    if [ -z "$KEY_PASSWORD" ]; then
        KEY_PASSWORD="$KEYSTORE_PASSWORD"
    fi

    export KEYSTORE_PASSWORD
    export KEY_PASSWORD

    echo "Building signed release bundle..."
    cd sudoq-app && bash ./gradlew :sudoqapp:bundleRelease

