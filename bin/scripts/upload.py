import contextlib
from pathlib import Path
from sys import argv

from pyrogram import Client
from pyrogram.types import InputMediaDocument

api_id = 11535358
api_hash = "33d372962fadb01df47e6ceed4e33cd6"
artifacts_path = Path("artifacts")
test_version = argv[3] == "test" if len(argv) > 2 else None


def find_apk(abi: str) -> Path:
    dirs = list(artifacts_path.glob("*"))
    for dir in dirs:
        if dir.is_dir():
            apks = list(dir.glob("*.apk"))
            for apk in apks:
                if abi in apk.name:
                    return apk


def get_thumb() -> str:
    return "TMessagesProj/src/main/" + "ic_launcher_nagram_round_blue-playstore.png"


def get_caption() -> str:
    pre = "Test version, " if test_version else ""
    with open(artifacts_path / "caption.txt", "r", encoding="utf-8") as f:
        return pre + f.read()


def get_document() -> list["InputMediaDocument"]:
    return [
        InputMediaDocument(
            media=str(find_apk("arm64-v8a")),
            thumb=get_thumb(),
        ),
        InputMediaDocument(
            media=str(find_apk("armeabi-v7a")),
            thumb=get_thumb(),
            caption=get_caption(),
        ),
    ]


def retry(func):
    async def wrapper(*args, **kwargs):
        for _ in range(3):
            try:
                return await func(*args, **kwargs)
            except Exception as e:
                print(e)

    return wrapper


@retry
async def send_to_channel(client: "Client", cid: str):
    with contextlib.suppress(ValueError):
        cid = int(cid)
    await client.send_media_group(
        cid,
        media=get_document(),
    )


def get_client(bot_token: str):
    return Client(
        "helper_bot",
        api_id=api_id,
        api_hash=api_hash,
        bot_token=bot_token,
    )


async def main():
    bot_token = argv[1]
    chat_id = argv[2]
    client = get_client(bot_token)
    await client.start()
    await send_to_channel(client, chat_id)
    await client.log_out()


if __name__ == "__main__":
    from asyncio import run

    run(main())
