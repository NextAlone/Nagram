import re
from pathlib import Path
from sys import argv

APK_VERSION_RE = re.compile(r"-v(?P<name>.+)\((?P<code>\d+)\)-[^/]+\.apk$")


def read_apk_version(path: Path) -> tuple[str, int]:
    match = APK_VERSION_RE.search(path.name)
    if match is None:
        raise ValueError(f"Cannot read version from APK filename: {path.name}")
    return match.group("name"), int(match.group("code"))


if __name__ == "__main__":
    # Printed as step outputs: the channel post takes its heading from them.
    version_name, version_code = read_apk_version(Path(argv[1]))
    print(f"version={version_name}")
    print(f"build={version_code}")
