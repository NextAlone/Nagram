import re
from html import escape, unescape
from pathlib import Path

_TAG_RE = re.compile(r"<[^>]+>")


def visible_length(html_text: str) -> int:
    """Return the number of characters Telegram displays (tags stripped, entities decoded)."""
    return len(unescape(_TAG_RE.sub("", html_text)))

CAPTION_BUDGET = 900
CHANGELOG_IGNORE_MARKER = "[ignore]"

GROUPS = {
    "feat": "✨ <b>Features</b>",
    "fix": "🔧 <b>Fixes</b>",
    "perf": "⚡ <b>Performance</b>",
    "refactor": "♻️ <b>Refactor</b>",
    "refa": "♻️ <b>Refactor</b>",
    "docs": "📝 <b>Docs</b>",
    "doc": "📝 <b>Docs</b>",
    "test": "✅ <b>Tests</b>",
    "build": "📦 <b>Build</b>",
    "ci": "⚙️ <b>CI</b>",
    "style": "💄 <b>Style</b>",
    "chore": "🧹 <b>Chore</b>",
    "revert": "⏪ <b>Revert</b>",
    "merge": "🔀 <b>Merge</b>",
}
SUBJECT_RE = re.compile(r"^(?P<type>[A-Za-z]+)(?:\([^)]*\))?!?:\s*.+$")
APK_VERSION_RE = re.compile(r"-v(?P<name>.+)\((?P<code>\d+)\)-[^/]+\.apk$")
PR_REF_RE = re.compile(r"\(#(\d+)\)")


def linkify_prs(text: str, repository_url: str) -> str:
    """Replace parenthesized PR references (e.g. PR 123 written as "(#123)") with HTML anchor links."""
    if not repository_url:
        return text

    def replace(m: re.Match) -> str:
        number = m.group(1)
        link = f'<a href="{escape(repository_url)}/pull/{number}">#{number}</a>'
        return f"({link})"

    return PR_REF_RE.sub(replace, text)

def is_changelog_ignored(commit_message: str) -> bool:
    return CHANGELOG_IGNORE_MARKER in commit_message


def read_gradle_property(key: str, path: Path = Path("gradle.properties")) -> str:
    prefix = f"{key}="
    with path.open(encoding="utf-8") as properties:
        for line in properties:
            if line.startswith(prefix):
                return line.removeprefix(prefix).strip()
    return "Unknown"


def read_apk_version(path: Path) -> tuple[str, int]:
    match = APK_VERSION_RE.search(path.name)
    if match is None:
        raise ValueError(f"Cannot read version from APK filename: {path.name}")
    return match.group("name"), int(match.group("code"))


def render_test_caption(
    commit_message: str,
    version_name: str,
    version_code: str,
    commit_sha: str = "",
    repository_url: str = "",
) -> str:
    normalized_message = commit_message.replace("\r", "").strip()
    header = (
        f"🧪 <b>Nagram</b> <code>{escape(version_name)}</code> "
        f"<i>({escape(version_code)})</i> · <b>Test Version</b>"
    )
    if is_changelog_ignored(normalized_message):
        return header

    subject, separator, detail = normalized_message.partition("\n")
    subject = subject or "No commit metadata."
    detail = detail.strip() if separator else ""
    match = SUBJECT_RE.match(subject)
    if match:
        group = GROUPS.get(match.group("type").lower(), "📌 <b>Other</b>")
    elif subject.lower().startswith("merge "):
        group = GROUPS["merge"]
    else:
        group = "📌 <b>Other</b>"

    commit_sha = commit_sha.strip()
    repository_url = repository_url.rstrip("/")
    if commit_sha:
        chip = f"[{escape(commit_sha[:7])}]"
        if repository_url:
            chip = f'<a href="{escape(repository_url)}/commit/{escape(commit_sha)}">{chip}</a>'
        else:
            chip = f"<code>{chip}</code>"
        prefix = f"• {chip} "
    else:
        prefix = "• "

    def assemble(subject_text: str, detail_text: str = "") -> str:
        rendered = (
            f"{header}\n\n{group}\n"
            f"{prefix}{linkify_prs(escape(subject_text), repository_url)}"
        )
        if detail_text:
            rendered += (
                f"\n\n💬 <b>Detail</b>\n"
                f"{linkify_prs(escape(detail_text), repository_url)}"
            )
        return rendered

    rendered = assemble(subject, detail)
    if visible_length(rendered) <= CAPTION_BUDGET:
        return rendered

    low, high = 0, len(detail)
    while low < high:
        mid = (low + high + 1) // 2
        if visible_length(assemble(subject, detail[:mid].rstrip() + "…")) <= CAPTION_BUDGET:
            low = mid
        else:
            high = mid - 1
    if low:
        return assemble(subject, detail[:low].rstrip() + "…")
    if visible_length(assemble(subject)) <= CAPTION_BUDGET:
        return assemble(subject)

    low, high = 0, len(subject)
    while low < high:
        mid = (low + high + 1) // 2
        if visible_length(assemble(subject[:mid].rstrip() + "…")) <= CAPTION_BUDGET:
            low = mid
        else:
            high = mid - 1
    return assemble(subject[:low].rstrip() + "…")
