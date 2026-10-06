import unittest
from pathlib import Path
from tempfile import TemporaryDirectory

from release_caption import (
    CAPTION_BUDGET,
    is_changelog_ignored,
    linkify_prs,
    read_apk_version,
    read_gradle_property,
    render_test_caption,
    visible_length,
)


class ReleaseCaptionTest(unittest.TestCase):
    def test_renders_conventional_commit_with_link_and_escaping(self):
        caption = render_test_caption(
            "feat(ci): improve <test> notification",
            "12.9.0",
            "1241",
            "abcdef123456",
            "https://github.com/NextAlone/Nagram",
        )

        self.assertIn(
            "🧪 <b>Nagram</b> <code>12.9.0</code> <i>(1241)</i> · <b>Test Version</b>",
            caption,
        )
        self.assertIn("✨ <b>Features</b>", caption)
        self.assertIn(
            '<a href="https://github.com/NextAlone/Nagram/commit/abcdef123456">[abcdef1]</a>',
            caption,
        )
        self.assertIn("improve &lt;test&gt; notification", caption)

    def test_uses_merge_and_other_groups_and_stays_within_caption_budget(self):
        merge_caption = render_test_caption("Merge branch 'main'", "test", "1")
        caption = render_test_caption("release " + "<&>" * 1000, "test", "1")

        self.assertIn("🔀 <b>Merge</b>", merge_caption)
        self.assertIn("📌 <b>Other</b>", caption)
        self.assertLessEqual(visible_length(caption), CAPTION_BUDGET)
        self.assertTrue(caption.endswith("…"))

    def test_omits_commit_marked_ignore_from_changelog(self):
        commit_message = "chore: regenerate artifacts\n\ninternal only [ignore]"
        caption = render_test_caption(
            commit_message,
            "12.9.0",
            "1241",
            "abcdef123456",
            "https://github.com/NextAlone/Nagram",
        )

        self.assertTrue(is_changelog_ignored(commit_message))
        self.assertEqual(
            "🧪 <b>Nagram</b> <code>12.9.0</code> <i>(1241)</i> · <b>Test Version</b>",
            caption,
        )

    def test_reads_gradle_property(self):
        with TemporaryDirectory() as directory:
            path = Path(directory) / "gradle.properties"
            path.write_text("APP_VERSION_NAME=12.9.0\n", encoding="utf-8")

            self.assertEqual("12.9.0", read_gradle_property("APP_VERSION_NAME", path))
            self.assertEqual("Unknown", read_gradle_property("APP_VERSION_CODE", path))

    def test_reads_actual_nagram_version_from_apk_filename(self):
        path = Path("Nagram-v12.9.0(1241)-arm64-v8a.apk")

        self.assertEqual(("12.9.0", 1241), read_apk_version(path))

        with self.assertRaisesRegex(ValueError, "Cannot read version"):
            read_apk_version(Path("app.apk"))


    def test_linkifies_parenthesized_pr_ref_in_subject(self):
        caption = render_test_caption(
            "feat: add configurable switch style (#156)",
            "12.9.0",
            "1241",
            "abcdef123456",
            "https://github.com/NextAlone/Nagram",
        )

        self.assertIn(
            '(<a href="https://github.com/NextAlone/Nagram/pull/156">#156</a>)',
            caption,
        )


    def test_linkifies_pr_ref_in_detail_body(self):
        caption = render_test_caption(
            "fix(AyuFilter): NPE when rebuilding regex filter cache\n\nSee (#173) for more context.",
            "12.9.0",
            "1241",
            "abcdef123456",
            "https://github.com/NextAlone/Nagram",
        )

        self.assertIn(
            '(<a href="https://github.com/NextAlone/Nagram/pull/173">#173</a>)',
            caption,
        )

    def test_no_link_when_repository_url_missing(self):
        result = linkify_prs("fix (#99)", "")
        self.assertEqual("fix (#99)", result)

if __name__ == "__main__":
    unittest.main()
