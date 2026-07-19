from html import escape
from pathlib import Path
import re

CAPTION_BUDGET = 900

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


def read_gradle_property(key: str, path: Path = Path("gradle.properties")) -> str:
    prefix = f"{key}="
    with path.open(encoding="utf-8") as properties:
        for line in properties:
            if line.startswith(prefix):
                return line.removeprefix(prefix).strip()
    return "Unknown"


def render_test_caption(
    commit_message: str,
    version_name: str,
    version_code: str,
    commit_sha: str = "",
    repository_url: str = "",
) -> str:
    subject = (
        commit_message.replace("\r", "").strip().partition("\n")[0]
        or "No commit metadata."
    )
    match = SUBJECT_RE.match(subject)
    if match:
        group = GROUPS.get(match.group("type").lower(), "📌 <b>Other</b>")
    elif subject.lower().startswith("merge "):
        group = GROUPS["merge"]
    else:
        group = "📌 <b>Other</b>"
    header = (
        f"🧪 <b>Nagram Test</b> <code>{escape(version_name)}</code> "
        f"<i>({escape(version_code)})</i>"
    )

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

    def assemble(text: str) -> str:
        return f"{header}\n\n{group}\n{prefix}{escape(text)}"

    rendered = assemble(subject)
    if len(rendered) <= CAPTION_BUDGET:
        return rendered

    low, high = 0, len(subject)
    while low < high:
        mid = (low + high + 1) // 2
        if len(assemble(subject[:mid].rstrip() + "…")) <= CAPTION_BUDGET:
            low = mid
        else:
            high = mid - 1
    return assemble(subject[:low].rstrip() + "…")
