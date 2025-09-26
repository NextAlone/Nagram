import contextlib
import json
from pathlib import Path
from sys import argv
from typing import Union, Iterable

from pyrogram import Client
from pyrogram.types import InputMediaDocument, Message

api_id = 11535358
api_hash = "33d372962fadb01df47e6ceed4e33cd6"
metadata_channel = -1001471208507
metadata_channel_msg_id = 46
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
    documents[-1].caption = get_caption()
    return documents


def get_timestamp() -> int:
    with open("gradle.properties", "r", encoding="utf-8") as f:
        for line in f:
            if line.startswith("APP_BUILD_TIMESTAMP="):
                return int(line.replace("APP_BUILD_TIMESTAMP=", "").strip())
    raise FileNotFoundError


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
    return await client.send_media_group(
        cid,
        media=get_document(),
    )


@retry
async def forward_to_channel(client: "Client", msg: list["Message"]):
    cid = msg[0].chat.id
    msg_ids = [m.id for m in msg]
    return await client.forward_messages(
        metadata_channel,
        cid,
        msg_ids,
    )


@retry
async def edit_metadata_msg(client: "Client", msg: Union["Message", Iterable["Message"]], timestamp: int):
    message = await client.get_messages(metadata_channel, metadata_channel_msg_id)
    json_dict = json.loads(message.text.replace("#updatetest", ""))
    abis = ["gcm", "nogcm"]
    if not isinstance(msg, list):
        v8a, v7a = msg.id, msg.id
    elif len(msg) == 1:
        v8a, v7a = msg[0].id, msg[0].id
    else:
        v8a, v7a = msg[0].id, msg[1].id
    for abi in abis:
        json_dict[abi] = {"armeabi-v7a":v7a,"arm64-v8a":v8a}
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
    chat_id = argv[2]
    client = get_client(bot_token)
    await client.start()
    msg = await send_to_channel(client, chat_id)
    msg = await forward_to_channel(client, msg)
    await edit_metadata_msg(client, msg, timestamp)
    await client.log_out()


if __name__ == "__main__":
    from asyncio import run

    run(main())
