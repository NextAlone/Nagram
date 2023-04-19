import requests
import sys
import os

def read_env(key):
    if key in os.environ:
        return os.environ[key]
    else:
        print(f"failed to read env {key}")
        return ""

APK_FOLDER = "apks"
API_PREFIX = "http://127.0.0.1:38118/bot" + os.environ["BOT_TOKEN"] + "/"

BOT_TARGET = read_env("BOT_TARGET")
ADMIN_USERID = BOT_TARGET.replace("-100","")

VERSION_NAME = read_env("VERSION_NAME")
VERSION_CODE = read_env("VERSION_CODE")
COMMIT_HASH = read_env("GITHUB_SHA")
COMMIT_MESSAGE = read_env("COMMIT_MESSAGE")

APK_CHANNEL_ID = "@NekoXApks"
UPDATE_CHANNEL_ID = "@NekogramX"
UPDATE_METADATA_CHANNEL_ID = "@nekox_update_metadata"
CI_CHANNEL_ID = "@NekoX_CI"


def addEntity(entities, origin_str, en_type, content, url = None) -> str:
    origin_len = len(origin_str)
    entity = {
        "type": en_type,
        "offset": origin_len,
        "length": len(content)
    }
    if url:
        entity["url"] = url
    entities.append(entity)
    return content


def waitReply(mid):
    last_update = 0
    while True:
        print(f"Waiting reply for {mid} offset {last_update}...")
        resp = requests.post(API_PREFIX + "getUpdates", json={"allowed_updates":["message"], "timeout": 20, "offset": last_update + 1})
        resp = resp.json()
        if not resp["ok"]:
            continue
        resp = resp["result"]
        for update in resp:
            if 'message' in update:
                msg = update["message"]
                if 'from' in msg and str(msg['from']["id"]) == ADMIN_USERID:
                    if 'reply_to_message' in msg and str(msg["reply_to_message"]["message_id"]) == str(mid):
                        return msg
        for update in resp:
            last_update = max(last_update, update["update_id"])


def sendMessage(message, user_id = BOT_TARGET, entities = None) -> int:
    data = {
        "chat_id" : user_id,
        "text": message,
        "entities": entities
    }
    print(message)
    print(entities)
    resp = requests.post(API_PREFIX + "sendMessage", json=data).json()
    print(resp)
    return int(resp["result"]["message_id"])


def sendDocument(user_id, path, message = "", entities = None):
    files = {'document': open(path, 'rb')}
    data = {'chat_id': user_id,
            'caption': message,
            'parse_mode': 'Markdown',
            'caption_entities': entities}
    response = requests.post(API_PREFIX + "sendDocument", files=files, data=data)
    print(response.json())


def sendRelease():
    apks = os.listdir(APK_FOLDER)
    apks.sort()
    print(apks)

    # read message from admin
    mid = sendMessage(f"Please reply the release message for the version {VERSION_NAME},{VERSION_CODE}:", user_id=BOT_TARGET)
    admin_resp = waitReply(mid)

    # send message and apks to APK channel
    message = f"=== {VERSION_NAME} ==="
    apk_channel_first_id = sendMessage(message, user_id=APK_CHANNEL_ID)

    for apk in apks:
        path = os.path.join(APK_FOLDER, apk)
        sendDocument(user_id=APK_CHANNEL_ID, path=path)
    
    # generate release message and send to update channel
    entities = []
    text = ""
    text += addEntity(entities, text, "hashtag", f"#{'PRE_RELEASE' if 'preview' in VERSION_NAME else 'RELEASE'}")
    text += " "
    text += addEntity(entities, text, "text_link", " ", f'https://t.me/{APK_CHANNEL_ID.replace("@","")}/{apk_channel_first_id + 1}')
    text += " "
    text += addEntity(entities, text, "bold", VERSION_NAME)
    text += "\n\n"
    if "entities" not in admin_resp:
        admin_resp["entities"] = list()
    resp_entities = admin_resp["entities"]
    for en in resp_entities:
        copy = en.copy()
        copy["offset"] += len(text)
        entities.append(copy)
    text += admin_resp["text"]
    text += "\n\n"
    text += addEntity(entities, text, "text_link", "GitHub Release", f"https://github.com/NekoX-Dev/NekoX/releases/{VERSION_NAME}")
    text += " | "
    text += addEntity(entities, text, "text_link", "Apks", f'https://t.me/{APK_CHANNEL_ID.replace("@","")}/{apk_channel_first_id}')
    text += " | "
    text += addEntity(entities, text, "text_link", "Check Update", "tg://update/")

    sendMessage(text, user_id=UPDATE_CHANNEL_ID, entities=entities)

    # send release message to metadata channel
    mid = sendMessage(admin_resp["text"], user_id=UPDATE_METADATA_CHANNEL_ID, entities=admin_resp["entities"])
    meta_msg = f"{VERSION_NAME},{VERSION_CODE},{apk_channel_first_id},{mid}"
    sendMessage(meta_msg, user_id=UPDATE_METADATA_CHANNEL_ID)


def sendCIRelease():
    apks = os.listdir(APK_FOLDER)
    apks.sort()
    apk = os.path.join(APK_FOLDER, apks[0])
    entities = []
    message = f"CI Build\n\n{COMMIT_MESSAGE}\n\n"
    message += addEntity(entities, message, "text_link", COMMIT_HASH[0:8], f"https://github.com/NekoX-Dev/NekoX/commit/{COMMIT_HASH}")
    sendDocument(user_id=CI_CHANNEL_ID, path = apk, message=message, )


if __name__ == '__main__':
    print(sys.argv)
    if len(sys.argv) != 2:
        print("Run Type: release, ci, debug")
        sys.stdout.flush()
        sys.stderr.flush()
        exit(1)
    mode = sys.argv[1]
    if mode == "release":
        sendRelease()
    elif mode == "ci":
        if COMMIT_MESSAGE.startswith("ci"):
            CI_CHANNEL_ID = BOT_TARGET
        sendCIRelease()
    elif mode == "debug":
        APK_CHANNEL_ID = "@test_channel_nekox"
        UPDATE_CHANNEL_ID = "@test_channel_nekox"
        UPDATE_METADATA_CHANNEL_ID = "@test_channel_nekox"
        sendRelease()
    else:
        print("unknown mode")

