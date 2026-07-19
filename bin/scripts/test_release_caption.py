from pathlib import Path
from tempfile import TemporaryDirectory
import unittest

from release_caption import CAPTION_BUDGET, read_gradle_property, render_test_caption


class ReleaseCaptionTest(unittest.TestCase):
    def test_renders_conventional_commit_with_link_and_escaping(self):
        caption = render_test_caption(
            "feat(ci): improve <test> notification",
            "12.9.0",
            "6966",
            "abcdef123456",
            "https://github.com/NextAlone/Nagram",
        )

        self.assertIn(
            "🧪 <b>Nagram Test</b> <code>12.9.0</code> <i>(6966)</i>", caption
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
        self.assertLessEqual(len(caption), CAPTION_BUDGET)
        self.assertTrue(caption.endswith("…"))

    def test_reads_gradle_property(self):
        with TemporaryDirectory() as directory:
            path = Path(directory) / "gradle.properties"
            path.write_text("APP_VERSION_NAME=12.9.0\n", encoding="utf-8")

            self.assertEqual("12.9.0", read_gradle_property("APP_VERSION_NAME", path))
            self.assertEqual("Unknown", read_gradle_property("APP_VERSION_CODE", path))


if __name__ == "__main__":
    unittest.main()
