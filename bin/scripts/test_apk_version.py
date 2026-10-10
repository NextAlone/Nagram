import unittest
from pathlib import Path

from apk_version import read_apk_version


class ApkVersionTest(unittest.TestCase):
    def test_reads_actual_nagram_version_from_apk_filename(self):
        path = Path("Nagram-v12.9.0(1241)-arm64-v8a.apk")

        self.assertEqual(("12.9.0", 1241), read_apk_version(path))

        with self.assertRaisesRegex(ValueError, "Cannot read version"):
            read_apk_version(Path("app.apk"))


if __name__ == "__main__":
    unittest.main()
