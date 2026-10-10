import json
from pathlib import Path
from sys import argv
from typing import Iterable, Union

from apk_version import read_apk_version
from pyrogram import Client
from pyrogram.types import InputMediaDocument, Message

api_id = 11535358
api_hash = "33d372962fadb01df47e6ceed4e33cd6"
metadata_channel = -1001471208507
metadata_channel_msg_id = 46
artifacts_path = Path("artifacts")


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


def get_document() -> list["InputMediaDocument"]:
    documents = []
    abis = ["arm64-v8a", "armeabi-v7a"]
    for abi in abis:
        if apk := find_apk(abi):
            documents.append(
                InputMediaDocument(
                    media=str(apk),
                    thumb=get_thumb(),
                )
            )
    return documents


def get_timestamp() -> int:
    with open("gradle.properties", "r", encoding="utf-8") as f:
        for line in f:
            if line.startswith("APP_BUILD_TIMESTAMP="):
                return int(line.replace("APP_BUILD_TIMESTAMP=", "").strip())
    raise FileNotFoundError


def get_version() -> tuple[str, int]:
    apk = find_apk("arm64-v8a") or find_apk("armeabi-v7a")
    if apk is None:
        raise FileNotFoundError("No supported APK found")
    return read_apk_version(apk)


def retry(func):
    async def wrapper(*args, **kwargs):
        for _ in range(3):
            try:
                return await func(*args, **kwargs)
            except Exception as e:
                print(e)

    return wrapper


# The in-app updater reads one document message per ABI from the metadata
# channel itself, so the APKs are sent there apart from the channel posts.
@retry
async def send_to_metadata_channel(client: "Client"):
    return await client.send_media_group(
        metadata_channel,
        media=get_document(),
    )


@retry
async def edit_metadata_msg(
    client: "Client", msg: Union["Message", Iterable["Message"]], timestamp: int
):
    message = await client.get_messages(metadata_channel, metadata_channel_msg_id)
    json_dict = json.loads(message.text.replace("#updatetest", ""))
    version_name, version_code = get_version()
    abis = ["gcm", "nogcm"]
    if not isinstance(msg, list):
        v8a, v7a = msg.id, msg.id
    elif len(msg) == 1:
        v8a, v7a = msg[0].id, msg[0].id
    else:
        v8a, v7a = msg[0].id, msg[1].id
    for abi in abis:
        json_dict[abi] = {"armeabi-v7a": v7a, "arm64-v8a": v8a}
    json_dict["version"] = f"{version_name} ({version_code})"
    json_dict["version_code"] = version_code
    json_dict["timestamp"] = timestamp
    json_text = json.dumps(json_dict)
    await message.edit(f"#updatetest{json_text}")


def get_client(bot_token: str):
    return Client(
        "helper_bot",
        api_id=api_id,
        api_hash=api_hash,
        bot_token=bot_token,
    )


async def main():
    timestamp = get_timestamp()
    bot_token = argv[1]
    client = get_client(bot_token)
    await client.start()
    msg = await send_to_metadata_channel(client)
    await edit_metadata_msg(client, msg, timestamp)
    await client.log_out()


if __name__ == "__main__":
    from asyncio import run

    run(main())
